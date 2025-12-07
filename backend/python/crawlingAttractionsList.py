"""
中国知名景点爬虫程序
数据源：
1. 维基百科 - 中国世界遗产列表
2. 维基百科 - 中国5A级景区列表
3. 维基百科 - 中国各省旅游景点
目标：爬取中国所有知名景点
"""

import requests
from bs4 import BeautifulSoup
import json
import time
import re
from typing import List, Dict
from urllib.parse import quote


class ChinaAttractionCrawler:
    """中国景点爬虫"""
    
    def __init__(self):
        """初始化爬虫"""
        self.session = requests.Session()
        self.session.headers.update({
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
        })
        self.attractions = []
        self.attraction_names = set()  # 用于去重
    
    def crawl_china_world_heritage(self):
        """爬取中国世界遗产"""
        print("📍 正在爬取中国世界遗产...")
        
        url = "https://zh.wikipedia.org/wiki/中国世界遗产列表"
        
        try:
            response = self.session.get(url, timeout=10)
            response.raise_for_status()
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 查找所有表格
            tables = soup.find_all('table', class_='wikitable')
            
            for table in tables:
                rows = table.find_all('tr')[1:]
                
                for row in rows:
                    cells = row.find_all(['td', 'th'])
                    if len(cells) >= 2:
                        # 提取景点名称
                        name_cell = cells[0] if len(cells) > 0 else None
                        if name_cell:
                            name_link = name_cell.find('a')
                            if name_link:
                                name = name_link.get_text(strip=True)
                            else:
                                name = name_cell.get_text(strip=True)
                            
                            # 提取省份/地区
                            province = ""
                            if len(cells) >= 3:
                                province = cells[2].get_text(strip=True)
                            
                            if name and name not in self.attraction_names:
                                self.attraction_names.add(name)
                                self.attractions.append({
                                    "name": name,
                                    "province": province,
                                    "country": "中国",
                                    "source": "世界遗产"
                                })
            
            print(f"  ✓ 中国世界遗产: {len([a for a in self.attractions if a['source'] == '世界遗产'])} 个")
            
        except Exception as e:
            print(f"  ✗ 世界遗产爬取失败: {e}")
    
    def crawl_5a_scenic_areas(self):
        """爬取中国5A级景区"""
        print("📍 正在爬取5A级景区...")
        
        url = "https://zh.wikipedia.org/wiki/国家5A级旅游景区"
        
        try:
            response = self.session.get(url, timeout=10)
            response.raise_for_status()
            soup = BeautifulSoup(response.content, 'html.parser')
            
            # 查找所有表格
            tables = soup.find_all('table', class_='wikitable')
            
            for table in tables:
                rows = table.find_all('tr')[1:]
                
                for row in rows:
                    cells = row.find_all(['td', 'th'])
                    if len(cells) >= 2:
                        # 提取景点名称（通常在第2列或第3列）
                        name = ""
                        province = ""
                        
                        for idx, cell in enumerate(cells):
                            text = cell.get_text(strip=True)
                            link = cell.find('a')
                            
                            # 景点名称通常有链接
                            if link and len(text) > 2 and idx >= 1:
                                if not name:
                                    name = text
                            
                            # 省份信息
                            if any(p in text for p in ['省', '市', '自治区', '特别行政区']):
                                province = text
                        
                        if name and name not in self.attraction_names:
                            self.attraction_names.add(name)
                            self.attractions.append({
                                "name": name,
                                "province": province,
                                "country": "中国",
                                "source": "5A级景区"
                            })
            
            print(f"  ✓ 5A级景区: {len([a for a in self.attractions if a['source'] == '5A级景区'])} 个")
            
        except Exception as e:
            print(f"  ✗ 5A级景区爬取失败: {e}")
    
    def crawl_provincial_attractions(self):
        """爬取各省旅游景点"""
        print("📍 正在爬取各省旅游景点...")
        
        # 中国各省份和直辖市
        provinces = [
            "北京市", "上海市", "天津市", "重庆市",
            "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省",
            "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省",
            "河南省", "湖北省", "湖南省", "广东省", "海南省",
            "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省",
            "内蒙古自治区", "广西壮族自治区", "西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区",
            "香港特别行政区", "澳门特别行政区", "台湾省"
        ]
        
        for province in provinces:
            try:
                # 尝试旅游页面
                url = f"https://zh.wikipedia.org/wiki/{province}旅游"
                response = self.session.get(url, timeout=10)
                
                if response.status_code != 200:
                    # 尝试主页面
                    url = f"https://zh.wikipedia.org/wiki/{province}"
                    response = self.session.get(url, timeout=10)
                
                if response.status_code == 200:
                    soup = BeautifulSoup(response.content, 'html.parser')
                    
                    # 查找旅游、景点相关章节
                    for heading in soup.find_all(['h2', 'h3', 'h4']):
                        heading_text = heading.get_text().lower()
                        
                        if any(keyword in heading_text for keyword in [
                            '旅游', '景点', '名胜', '古迹', '风景', '遗产', '公园', '寺庙', '博物馆'
                        ]):
                            # 获取该章节下的列表
                            next_element = heading.find_next_sibling()
                            
                            while next_element and next_element.name not in ['h2', 'h3', 'h4']:
                                if next_element.name in ['ul', 'ol']:
                                    items = next_element.find_all('li')
                                    
                                    for item in items:
                                        link = item.find('a')
                                        if link:
                                            name = link.get_text(strip=True)
                                            
                                            if (name and len(name) > 1 and 
                                                name not in self.attraction_names and
                                                not name.startswith(('编辑', '参考', '维基'))):
                                                
                                                self.attraction_names.add(name)
                                                self.attractions.append({
                                                    "name": name,
                                                    "province": province,
                                                    "country": "中国",
                                                    "source": f"{province}景点"
                                                })
                                
                                next_element = next_element.find_next_sibling()
                
                time.sleep(0.5)
                
            except Exception as e:
                pass
        
        print(f"  ✓ 各省景点: {len([a for a in self.attractions if '景点' in a.get('source', '')])} 个")
    
    def crawl_famous_mountains(self):
        """爬取中国名山"""
        print("📍 正在爬取中国名山...")
        
        mountain_lists = [
            "中国五岳",
            "中国四大佛教名山",
            "中国四大道教名山"
        ]
        
        for mountain_list in mountain_lists:
            try:
                url = f"https://zh.wikipedia.org/wiki/{mountain_list}"
                response = self.session.get(url, timeout=10)
                
                if response.status_code == 200:
                    soup = BeautifulSoup(response.content, 'html.parser')
                    
                    # 查找所有链接
                    content = soup.find('div', id='mw-content-text')
                    if content:
                        links = content.find_all('a', href=True)
                        
                        for link in links:
                            text = link.get_text(strip=True)
                            
                            if (text and '山' in text and len(text) <= 10 and
                                text not in self.attraction_names and
                                not text.startswith(('编辑', '参考'))):
                                
                                self.attraction_names.add(text)
                                self.attractions.append({
                                    "name": text,
                                    "province": "",
                                    "country": "中国",
                                    "source": "名山"
                                })
                
                time.sleep(0.3)
                
            except Exception as e:
                pass
        
        print(f"  ✓ 名山: {len([a for a in self.attractions if a['source'] == '名山'])} 个")
    
    def crawl_ancient_towns(self):
        """爬取中国古镇"""
        print("📍 正在爬取中国古镇...")
        
        try:
            url = "https://zh.wikipedia.org/wiki/中国历史文化名镇"
            response = self.session.get(url, timeout=10)
            
            if response.status_code == 200:
                soup = BeautifulSoup(response.content, 'html.parser')
                
                # 查找所有表格
                tables = soup.find_all('table', class_='wikitable')
                
                for table in tables:
                    rows = table.find_all('tr')[1:]
                    
                    for row in rows:
                        cells = row.find_all(['td', 'th'])
                        
                        if len(cells) >= 1:
                            name_cell = cells[0]
                            link = name_cell.find('a')
                            
                            if link:
                                name = link.get_text(strip=True)
                            else:
                                name = name_cell.get_text(strip=True)
                            
                            province = ""
                            if len(cells) >= 2:
                                province = cells[1].get_text(strip=True)
                            
                            if name and name not in self.attraction_names:
                                self.attraction_names.add(name)
                                self.attractions.append({
                                    "name": name,
                                    "province": province,
                                    "country": "中国",
                                    "source": "历史文化名镇"
                                })
            
            print(f"  ✓ 古镇: {len([a for a in self.attractions if a['source'] == '历史文化名镇'])} 个")
            
        except Exception as e:
            print(f"  ✗ 古镇爬取失败: {e}")
    
    def save_results(self, filename="china_attractions.json"):
        """保存结果到JSON文件"""
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(self.attractions, f, ensure_ascii=False, indent=2)
        
        print(f"\n✅ 已保存 {len(self.attractions)} 个景点到 {filename}")
    
    def print_summary(self):
        """打印统计摘要"""
        print(f"\n{'='*80}")
        print(f"爬取结果统计")
        print(f"{'='*80}")
        print(f"总景点数: {len(self.attractions)}")
        
        # 按来源统计
        sources = {}
        for attr in self.attractions:
            source = attr.get('source', '未知')
            sources[source] = sources.get(source, 0) + 1
        
        print(f"\n按来源分布:")
        for source, count in sorted(sources.items(), key=lambda x: x[1], reverse=True):
            print(f"  {source}: {count} 个")
        
        # 按省份统计
        provinces = {}
        for attr in self.attractions:
            province = attr.get('province', '未知')
            if province:
                provinces[province] = provinces.get(province, 0) + 1
        
        if provinces:
            print(f"\n按省份分布（前10）:")
            for province, count in sorted(provinces.items(), key=lambda x: x[1], reverse=True)[:10]:
                print(f"  {province}: {count} 个")
        
        print(f"\n{'='*80}\n")
    
    def print_table_sample(self, limit=50):
        """打印前N个景点的表格"""
        print(f"\n{'='*100}")
        print(f"{'序号':<6}{'景点名称':<35}{'省份':<20}{'来源':<25}")
        print(f"{'='*100}")
        
        for idx, attraction in enumerate(self.attractions[:limit], 1):
            name = attraction['name'][:33]
            province = attraction.get('province', '')[:18]
            source = attraction.get('source', '')[:23]
            print(f"{idx:<6}{name:<35}{province:<20}{source:<25}")
        
        if len(self.attractions) > limit:
            print(f"... (还有 {len(self.attractions) - limit} 个景点未显示)")
        
        print(f"{'='*100}\n")


def main():
    """主函数"""
    print("🇨🇳 中国景点爬虫启动...\n")
    
    crawler = ChinaAttractionCrawler()
    
    # 执行各项爬取任务
    crawler.crawl_china_world_heritage()
    crawler.crawl_5a_scenic_areas()
    crawler.crawl_famous_mountains()
    crawler.crawl_ancient_towns()
    crawler.crawl_provincial_attractions()
    
    # 显示统计
    crawler.print_summary()
    
    # 显示前50个景点
    crawler.print_table_sample(50)
    
    # 保存结果
    crawler.save_results("china_attractions.json")
    
    print("✅ 爬取完成！")


if __name__ == "__main__":
    main()
