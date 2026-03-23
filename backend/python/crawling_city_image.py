#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从携程搜索页抓取城市主图，并生成 city.city_url 更新 SQL。

抓取规则：
- 页面入口： https://you.ctrip.com/searchsite/Sight?query=<城市名>
- 目标节点： div.city-head-image-container img

输入：
- backend/sql/travel_system.sql（仅解析 city 表 INSERT）

输出：
- backend/sql/update_city_images_from_xiecheng.sql
- backend/sql/unmatched_cities_from_xiecheng.txt
"""

from __future__ import annotations

import argparse
import random
import re
import time
from pathlib import Path
from typing import List, Optional, Tuple
from urllib.parse import quote, urljoin

import requests
from bs4 import BeautifulSoup

ROOT = Path(__file__).resolve().parents[2]
DEFAULT_SQL_FILE = ROOT / "backend" / "sql" / "travel_system.sql"
DEFAULT_OUT_SQL = ROOT / "backend" / "sql" / "update_city_images_from_xiecheng.sql"
DEFAULT_UNMATCHED = ROOT / "backend" / "sql" / "unmatched_cities_from_xiecheng.txt"

SEARCH_URL_TEMPLATES = [
    "https://you.ctrip.com/searchsite/Sight?query={query}",
    "https://you.ctrip.com/searchsite/?query={query}",
    "https://you.ctrip.com/searchsite?query={query}",
]


def sql_escape(value: str) -> str:
    return value.replace("\\", "\\\\").replace("'", "\\'")


def split_sql_values(values_expr: str) -> List[str]:
    """将 SQL VALUES(...) 的内容按逗号拆分（忽略字符串内逗号）。"""
    out: List[str] = []
    cur: List[str] = []
    in_quote = False
    i = 0
    while i < len(values_expr):
        ch = values_expr[i]

        if ch == "'":
            cur.append(ch)
            # SQL 字符串中 '' 代表转义单引号
            if in_quote and i + 1 < len(values_expr) and values_expr[i + 1] == "'":
                cur.append(values_expr[i + 1])
                i += 2
                continue
            in_quote = not in_quote
            i += 1
            continue

        if ch == "," and not in_quote:
            out.append("".join(cur).strip())
            cur = []
            i += 1
            continue

        cur.append(ch)
        i += 1

    if cur:
        out.append("".join(cur).strip())
    return out


def unquote_sql_string(token: str) -> str:
    t = token.strip()
    if len(t) >= 2 and t[0] == "'" and t[-1] == "'":
        t = t[1:-1]
    # 兼容两种常见转义
    t = t.replace("''", "'")
    t = t.replace("\\'", "'").replace("\\\\", "\\")
    return t


def parse_cities_from_sql(sql_file: Path) -> List[Tuple[int, str]]:
    text = sql_file.read_text(encoding="utf-8", errors="ignore")
    pattern = re.compile(r"INSERT INTO\s+`city`\s+VALUES\s*\((.*?)\);", re.S)

    result: List[Tuple[int, str]] = []
    for m in pattern.finditer(text):
        values_expr = m.group(1)
        fields = split_sql_values(values_expr)
        if len(fields) < 2:
            continue

        city_id_token = fields[0]
        city_name_token = fields[1]
        if not city_id_token.isdigit():
            continue

        city_id = int(city_id_token)
        city_name = unquote_sql_string(city_name_token)
        if city_name:
            result.append((city_id, city_name))

    # 去重并保持顺序
    seen = set()
    uniq: List[Tuple[int, str]] = []
    for city_id, city_name in result:
        if city_id in seen:
            continue
        seen.add(city_id)
        uniq.append((city_id, city_name))
    return uniq


class CtripCityImageCrawler:
    def __init__(self, timeout: int, delay: float) -> None:
        self.timeout = timeout
        self.delay = delay
        self.session = requests.Session()
        self.ua_pool = [
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edge/124.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15",
        ]

    def _headers(self) -> dict:
        return {
            "User-Agent": random.choice(self.ua_pool),
            "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
            "Referer": "https://you.ctrip.com/",
        }

    @staticmethod
    def normalize_img_url(url: str) -> str:
        u = (url or "").strip()
        if not u:
            return ""
        if u.startswith("//"):
            return "https:" + u
        if u.startswith("/"):
            return "https://you.ctrip.com" + u
        return u

    def _extract_target_image(self, html: str) -> Optional[str]:
        soup = BeautifulSoup(html, "html.parser")
        img = soup.select_one("div.city-head-image-container img")
        if img is None:
            return None

        src = (
            img.get("src")
            or img.get("data-src")
            or img.get("data-original")
            or img.get("srcset", "").split(" ")[0]
        )
        src = self.normalize_img_url(src)
        return src if src.startswith("http") else None

    def _extract_place_link(self, html: str) -> Optional[str]:
        soup = BeautifulSoup(html, "html.parser")
        a = soup.select_one("a[href*='/place/'][href$='.html']")
        if a is None:
            return None
        href = (a.get("href") or "").strip()
        if not href:
            return None
        return urljoin("https://you.ctrip.com", href)

    def fetch_city_image(self, city_name: str) -> Optional[str]:
        query = quote(city_name)
        last_err = None
        for attempt in range(1, 4):
            try:
                # 1) 优先：直接在搜索结果页抓 city-head-image-container
                for tpl in SEARCH_URL_TEMPLATES:
                    url = tpl.format(query=query)
                    resp = self.session.get(url, headers=self._headers(), timeout=self.timeout)
                    if resp.status_code >= 500:
                        continue
                    img_url = self._extract_target_image(resp.text)
                    if img_url:
                        return img_url

                    # 2) 回退：从搜索页提取 /place/xxx.html，再抓同样的目标容器
                    place_link = self._extract_place_link(resp.text)
                    if place_link:
                        place_resp = self.session.get(place_link, headers=self._headers(), timeout=self.timeout)
                        if place_resp.status_code < 500:
                            place_img = self._extract_target_image(place_resp.text)
                            if place_img:
                                return place_img

                return None
            except Exception as exc:
                last_err = exc
                time.sleep(0.8 * attempt)

        if last_err is not None:
            print(f"[WARN] {city_name} 抓取失败: {last_err}")
        return None

    def crawl(self, cities: List[Tuple[int, str]], max_city: int = 0, start_index: int = 0) -> Tuple[List[Tuple[int, str, str]], List[Tuple[int, str]]]:
        matched: List[Tuple[int, str, str]] = []
        unmatched: List[Tuple[int, str]] = []

        data = cities[start_index:]
        if max_city > 0:
            data = data[:max_city]

        total = len(data)
        try:
            for idx, (city_id, city_name) in enumerate(data, start=1):
                print(f"[{idx}/{total}] {city_name}")
                img_url = self.fetch_city_image(city_name)
                if img_url:
                    matched.append((city_id, city_name, img_url))
                    print(f"  [OK] {img_url}")
                else:
                    unmatched.append((city_id, city_name))
                    print("  [MISS]")

                if self.delay > 0:
                    time.sleep(self.delay + random.uniform(0, self.delay))
        except KeyboardInterrupt:
            print("\n检测到中断，保留当前已抓取结果。")

        return matched, unmatched


def write_update_sql(out_sql: Path, matched: List[Tuple[int, str, str]]) -> None:
    lines = [
        "-- 由 crawling_city_image.py 生成",
        "START TRANSACTION;",
    ]
    for city_id, city_name, img_url in matched:
        lines.append(
            "UPDATE `city` SET "
            f"`city_url`='{sql_escape(img_url)}' "
            f"WHERE `city_id`={city_id} AND `city_name`='{sql_escape(city_name)}';"
        )
    lines.append("COMMIT;")
    out_sql.write_text("\n".join(lines) + "\n", encoding="utf-8")


def write_unmatched(out_file: Path, unmatched: List[Tuple[int, str]]) -> None:
    lines = [f"{city_id}\t{city_name}" for city_id, city_name in unmatched]
    out_file.write_text("\n".join(lines) + ("\n" if lines else ""), encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="从携程抓取城市图并生成 city_url 更新 SQL")
    parser.add_argument("--sql-file", type=Path, default=DEFAULT_SQL_FILE, help="输入 SQL 文件")
    parser.add_argument("--out-sql", type=Path, default=DEFAULT_OUT_SQL, help="输出更新 SQL")
    parser.add_argument("--out-unmatched", type=Path, default=DEFAULT_UNMATCHED, help="输出未命中城市列表")
    parser.add_argument("--timeout", type=int, default=20, help="请求超时秒数")
    parser.add_argument("--delay", type=float, default=0.2, help="请求间隔基值秒")
    parser.add_argument("--max-city", type=int, default=0, help="最大抓取城市数（0 表示全部）")
    parser.add_argument("--start-index", type=int, default=0, help="从第几个城市开始（0-based）")
    return parser.parse_args()


def main() -> None:
    args = parse_args()

    cities = parse_cities_from_sql(args.sql_file)
    print(f"SQL 城市数: {len(cities)}")

    crawler = CtripCityImageCrawler(timeout=args.timeout, delay=args.delay)

    matched: List[Tuple[int, str, str]] = []
    unmatched: List[Tuple[int, str]] = []

    matched, unmatched = crawler.crawl(cities, max_city=args.max_city, start_index=args.start_index)

    write_update_sql(args.out_sql, matched)
    if not unmatched:
        # 全命中时保持空；中断时由 matched 反推当前处理区间的未命中
        processed = cities[args.start_index : args.start_index + args.max_city] if args.max_city > 0 else cities[args.start_index :]
        matched_ids = {city_id for city_id, _, _ in matched}
        unmatched = [(city_id, name) for city_id, name in processed if city_id not in matched_ids]
    write_unmatched(args.out_unmatched, unmatched)

    print("=" * 60)
    print(f"匹配成功: {len(matched)}")
    print(f"未命中: {len(unmatched)}")
    print(f"输出 SQL: {args.out_sql}")
    print(f"未命中清单: {args.out_unmatched}")


if __name__ == "__main__":
    main()
