#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
从携程抓取景点图片并生成 attraction 更新 SQL。

设计目标：
1. 独立脚本，不依赖 xiecheng travel 项目的代码结构。
2. 输入 travel_system.sql 中的 attraction 名称。
3. 爬取对应景点主图和多图（4-6 条），输出 UPDATE SQL。

用法示例：
python backend/python/ctrip_attraction_image_crawler.py
python backend/python/ctrip_attraction_image_crawler.py --max-page 60 --sleep 0.2
"""

from __future__ import annotations

import argparse
import csv
import json
import random
import re
import time
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Set, Tuple

import requests

ROOT = Path(__file__).resolve().parents[2]
DEFAULT_SQL_FILE = ROOT / "backend" / "sql" / "travel_system.sql"
DEFAULT_CITY_FILE = ROOT / "backend" / "python" / "xiecheng travel" / "helper" / "files" / "cities.csv"
DEFAULT_OUT_SQL = ROOT / "backend" / "sql" / "update_attraction_images_from_xiecheng.sql"
DEFAULT_UNMATCHED = ROOT / "backend" / "sql" / "unmatched_attractions_from_xiecheng.txt"

ATTRACTION_LIST_API = "https://m.ctrip.com/restapi/soa2/18109/json/getAttractionList"

COMMON_PREFIXES = [
    "新疆维吾尔自治区",
    "新疆",
]
COMMON_SUFFIXES = [
    "风景名胜区",
    "旅游风景区",
    "旅游景区",
    "风景区",
    "景区",
    "旅游区",
    "度假区",
]

# 可按实际数据继续补充
ALIAS_NAME_MAP = {
    "新疆博物馆": "新疆维吾尔自治区博物馆",
    "南山风景区": "乌鲁木齐天山大峡谷",
    "盐湖景区": "新疆盐湖景区",
    "白沙湖（喀什）": "白沙湖",
    "白沙湖（阿勒泰）": "白沙湖",
}


class CtripAttractionImageCrawler:
    def __init__(self, max_page: int, sleep_sec: float, timeout: int) -> None:
        self.max_page = max_page
        self.sleep_sec = sleep_sec
        self.timeout = timeout
        self.session = requests.Session()
        self.session.headers.update(
            {
                "User-Agent": (
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    "AppleWebKit/537.36 (KHTML, like Gecko) "
                    "Chrome/122.0.0.0 Safari/537.36"
                ),
                "Accept": "application/json, text/plain, */*",
                "Content-Type": "application/json",
                "Origin": "https://you.ctrip.com",
                "Referer": "https://you.ctrip.com/",
                "cookieorigin": "https://you.ctrip.com",
            }
        )

    @staticmethod
    def norm_name(name: str) -> str:
        s = (name or "").strip()
        s = re.sub(r"[\s\u3000]+", "", s)
        s = s.replace("（", "(").replace("）", ")")
        s = re.sub(r"[·•，,。！!？?、\-_/]", "", s)
        for p in COMMON_PREFIXES:
            if s.startswith(p):
                s = s[len(p) :]
        for suf in COMMON_SUFFIXES:
            if s.endswith(suf) and len(s) > len(suf) + 1:
                s = s[: -len(suf)]
        s = re.sub(r"\(.*?\)", "", s)
        return s

    @staticmethod
    def sql_escape(value: str) -> str:
        return value.replace("\\", "\\\\").replace("'", "\\'")

    @staticmethod
    def clean_image_url(url: str) -> str:
        u = (url or "").strip()
        if not u.startswith("http"):
            return ""
        u = u.split("?", 1)[0]
        # 统一将缩略图 URL 归并到原图 URL
        u = re.sub(r"_(?:W|D)_\d+_\d+(?=\.(?:jpg|jpeg|png|webp)$)", "", u, flags=re.I)
        return u

    @staticmethod
    def parse_attraction_names(sql_file: Path) -> List[str]:
        pattern = re.compile(r"INSERT INTO\s+`attraction`\s+VALUES\s*\([^\n]*?,\s*'((?:[^'\\]|\\.)*)'\s*,")
        text = sql_file.read_text(encoding="utf-8", errors="ignore")
        names: Set[str] = set()
        for m in pattern.finditer(text):
            raw = m.group(1)
            name = raw.replace("\\'", "'").replace("\\\\", "\\")
            if name:
                names.add(name)
        return sorted(names)

    @staticmethod
    def read_cities(city_file: Path) -> List[Tuple[str, int]]:
        cities: List[Tuple[str, int]] = []
        with city_file.open("r", encoding="utf-8", newline="") as f:
            reader = csv.DictReader(f)
            for row in reader:
                name = (row.get("城市名称") or "").strip()
                cid = (row.get("城市ID") or "").strip()
                if not name or not cid.isdigit():
                    continue
                cities.append((name, int(cid)))
        return cities

    def fetch_attraction_page(self, city_id: int, page: int) -> List[dict]:
        payload = {
            "head": {
                "cid": "09031076110042895602",
                "ctok": "",
                "cver": "1.0",
                "lang": "01",
                "sid": "8888",
                "syscode": "999",
                "auth": "",
                "xsid": "",
                "extension": [],
            },
            "scene": "online",
            "districtId": city_id,
            "index": page,
            "sortType": 1,
            "count": 20,
            "filter": {"filterItems": []},
            "coordinate": {"latitude": 39.9042, "longitude": 116.4074, "coordinateType": "WGS84"},
            "returnModuleType": "all",
        }

        for _ in range(3):
            try:
                resp = self.session.post(ATTRACTION_LIST_API, json=payload, timeout=self.timeout)
                resp.raise_for_status()
                data = resp.json()
                return data.get("attractionList") or []
            except Exception:
                time.sleep(1.2)
        return []

    def fetch_detail_images(self, detail_url: str) -> List[str]:
        if not detail_url:
            return []
        for _ in range(3):
            try:
                r = self.session.get(detail_url, timeout=self.timeout)
                r.raise_for_status()
                html = r.text
                # 仅保留景点内容图域名
                raw_imgs = re.findall(r"https://[^\"']+?\.(?:jpg|jpeg|png|webp)", html, flags=re.I)
                candidates: List[str] = []
                for u in raw_imgs:
                    if "c-ctrip.com/images/" not in u and "dimg04.c-ctrip.com/images/" not in u:
                        continue
                    cleaned = self.clean_image_url(u)
                    if not cleaned:
                        continue
                    if cleaned not in candidates:
                        candidates.append(cleaned)
                return candidates
            except Exception:
                time.sleep(1.0)
        return []

    @staticmethod
    def build_multi(main_url: str, detail_urls: Iterable[str]) -> List[str]:
        uniq: List[str] = []
        if main_url:
            uniq.append(main_url)
        for u in detail_urls:
            if u and u not in uniq:
                uniq.append(u)
            if len(uniq) >= 6:
                break
        if not uniq:
            return []
        seed = list(uniq)
        while len(uniq) < 4:
            uniq.append(seed[len(uniq) % len(seed)])
        return uniq[:6]

    def choose_target_key(self, poi_name: str, targets_raw: Set[str], targets_norm: Dict[str, Set[str]]) -> Optional[str]:
        if poi_name in targets_raw:
            return poi_name

        key = self.norm_name(poi_name)
        if key in targets_norm and len(targets_norm[key]) == 1:
            return next(iter(targets_norm[key]))

        # 反向别名匹配：CSV 名可能是别名值
        for db_name, alias in ALIAS_NAME_MAP.items():
            if alias == poi_name and db_name in targets_raw:
                return db_name

        return None

    def crawl_and_match(
        self,
        target_names: List[str],
        cities: List[Tuple[str, int]],
        max_cities: int = 0,
    ) -> Tuple[Dict[str, Dict[str, object]], Set[str]]:
        targets_raw = set(target_names)
        targets_norm: Dict[str, Set[str]] = {}
        for n in target_names:
            targets_norm.setdefault(self.norm_name(n), set()).add(n)

        # 先处理 DB->别名直接可命中的目标键集合
        alias_targets = {k for k, v in ALIAS_NAME_MAP.items() if k in targets_raw and v}

        matched: Dict[str, Dict[str, object]] = {}

        city_count = 0
        try:
            for city_name, city_id in cities:
                if max_cities > 0 and city_count >= max_cities:
                    break
                city_count += 1
                print(f"[CITY] {city_name}({city_id})")
                for page in range(1, self.max_page + 1):
                    cards = self.fetch_attraction_page(city_id, page)
                    if not cards:
                        break

                    for item in cards:
                        card = item.get("card") or {}
                        poi_name = (card.get("poiName") or "").strip()
                        if not poi_name:
                            continue

                        target_key = self.choose_target_key(poi_name, targets_raw, targets_norm)
                        if not target_key:
                            # 支持 DB 名->别名 的精确比对
                            for db_name in alias_targets:
                                if ALIAS_NAME_MAP.get(db_name) == poi_name:
                                    target_key = db_name
                                    break
                        if not target_key:
                            continue
                        if target_key in matched:
                            continue

                        cover = self.clean_image_url(card.get("coverImageUrl") or "")
                        detail_url = (card.get("detailUrl") or "").strip()
                        detail_imgs = self.fetch_detail_images(detail_url)

                        main_url = cover or (detail_imgs[0] if detail_imgs else "")
                        if not main_url:
                            continue
                        multi_urls = self.build_multi(main_url, detail_imgs)
                        if not multi_urls:
                            continue

                        matched[target_key] = {
                            "source_poi": poi_name,
                            "city": city_name,
                            "main": main_url,
                            "multi": multi_urls,
                            "detail_url": detail_url,
                        }
                        print(f"  [MATCH] {target_key} <- {poi_name} ({len(multi_urls)} imgs)")

                    if self.sleep_sec > 0:
                        time.sleep(self.sleep_sec + random.uniform(0, self.sleep_sec))

                if len(matched) >= len(target_names):
                    break
        except KeyboardInterrupt:
            print("\n检测到中断，返回当前已匹配结果。")

        unmatched = set(target_names) - set(matched.keys())
        return matched, unmatched

    def generate_update_sql(self, matched: Dict[str, Dict[str, object]], out_sql: Path) -> None:
        lines = [
            "-- 由 ctrip_attraction_image_crawler.py 生成",
            "START TRANSACTION;",
        ]
        for name in sorted(matched.keys()):
            row = matched[name]
            main_url = str(row["main"])
            multi_urls = row["multi"]
            multi_json = json.dumps(multi_urls, ensure_ascii=False)
            lines.append(
                "UPDATE `attraction` SET "
                f"`main_image_url`='{self.sql_escape(main_url)}', "
                f"`multi_image_urls`='{self.sql_escape(multi_json)}' "
                f"WHERE `name`='{self.sql_escape(name)}';"
            )
        lines.append("COMMIT;")
        out_sql.write_text("\n".join(lines) + "\n", encoding="utf-8")

    @staticmethod
    def write_unmatched(unmatched: Set[str], out_file: Path) -> None:
        out_file.write_text("\n".join(sorted(unmatched)) + "\n", encoding="utf-8")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="携程景点图片爬虫 + attraction SQL 更新生成")
    parser.add_argument("--sql-file", type=Path, default=DEFAULT_SQL_FILE, help="输入 SQL 文件路径")
    parser.add_argument("--city-file", type=Path, default=DEFAULT_CITY_FILE, help="城市 ID CSV 路径")
    parser.add_argument("--out-sql", type=Path, default=DEFAULT_OUT_SQL, help="输出更新 SQL 路径")
    parser.add_argument("--out-unmatched", type=Path, default=DEFAULT_UNMATCHED, help="输出未匹配景点列表路径")
    parser.add_argument("--max-page", type=int, default=80, help="每个城市最大抓取页数")
    parser.add_argument("--max-cities", type=int, default=0, help="最大抓取城市数（0=不限）")
    parser.add_argument("--sleep", type=float, default=0.15, help="分页抓取间隔基值（秒）")
    parser.add_argument("--timeout", type=int, default=20, help="请求超时秒数")
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    crawler = CtripAttractionImageCrawler(max_page=args.max_page, sleep_sec=args.sleep, timeout=args.timeout)

    target_names = crawler.parse_attraction_names(args.sql_file)
    cities = crawler.read_cities(args.city_file)

    print(f"目标景点数: {len(target_names)}")
    print(f"城市数: {len(cities)}")

    matched: Dict[str, Dict[str, object]] = {}
    unmatched: Set[str] = set(target_names)

    try:
        matched, unmatched = crawler.crawl_and_match(target_names, cities, max_cities=args.max_cities)
    except KeyboardInterrupt:
        print("\n检测到手动中断，正在输出已抓取结果...")
        unmatched = set(target_names) - set(matched.keys())
    finally:
        crawler.generate_update_sql(matched, args.out_sql)
        crawler.write_unmatched(unmatched, args.out_unmatched)

    print("=" * 60)
    print(f"匹配成功: {len(matched)}")
    print(f"未匹配: {len(unmatched)}")
    print(f"输出 SQL: {args.out_sql}")
    print(f"未匹配列表: {args.out_unmatched}")


if __name__ == "__main__":
    main()
