#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
个性化推荐排序算法（基于内容 + 行为）
输入（stdin JSON）：
{
  "candidates": [{
    "attractionId": 1,
    "typeId": 2,
    "browseCount": 100,
    "favoriteCount": 20,
    "averageRating": 4.6,
    "ticketPrice": 120,
    "bestSeason": "夏季,秋季"
  }],
  "preference": {
    "preferAttractionTypeId": 2,
    "budgetFloor": 50,
    "budgetRange": 300,
    "preferSeason": "夏季,秋季"
  },
  "behaviorByType": {
    "2": {"clickCount": 10, "staySeconds": 900}
  },
  "behaviorEventCount": 25,
  "params": {
    "behaviorSwitchThreshold": 20,
    "coldPreferenceWeight": 0.6,
    "coldHotWeight": 0.4,
    "matureProfileWeight": 0.8,
    "matureHotWeight": 0.2,
    "profileExplicitWeight": 0.4,
    "profileImplicitWeight": 0.6,
    "behaviorClickWeight": 0.4,
    "behaviorStayWeight": 0.6
  }
}

输出（stdout JSON）：
{
  "orderedIds": [1,2,3],
  "behaviorEnabled": true,
  "scores": {"1": 0.8234, "2": 0.7123}
}
"""

import json
import math
import sys


def safe_float(v, default=0.0):
    try:
        if v is None:
            return default
        return float(v)
    except Exception:
        return default


def safe_int(v, default=0):
    try:
        if v is None:
            return default
        return int(v)
    except Exception:
        return default


def normalize_type_id(v):
    if v is None:
        return None
    try:
        return int(v)
    except Exception:
        return None


def normalize(value, max_value):
    if max_value <= 0:
        return 0.0
    n = value / max_value
    if n < 0:
        return 0.0
    if n > 1:
        return 1.0
    return n


def explicit_score(item, pref):
    if not pref:
        return 0.0

    score = 0.0

    pref_type = pref.get("preferAttractionTypeId")
    if pref_type is not None and item.get("typeId") == pref_type:
        score += 0.55

    pref_season = pref.get("preferSeason")
    best_season = item.get("bestSeason") or ""
    if pref_season and best_season:
        seasons = [s.strip() for s in str(pref_season).split(",") if s.strip()]
        if any(season in best_season for season in seasons):
            score += 0.2

    budget_floor = pref.get("budgetFloor")
    budget_range = pref.get("budgetRange")
    ticket_price = safe_float(item.get("ticketPrice"), 0.0)
    if budget_floor is not None and budget_range is not None:
        if safe_int(budget_floor) <= ticket_price <= safe_int(budget_range):
            score += 0.25

    return min(score, 1.0)


def hot_score(item, max_browse, max_favorite, max_rating):
    browse = normalize(safe_int(item.get("browseCount"), 0), max_browse)
    favorite = normalize(safe_int(item.get("favoriteCount"), 0), max_favorite)
    rating = normalize(safe_float(item.get("averageRating"), 0.0), max_rating)
    return 0.5 * browse + 0.3 * favorite + 0.2 * rating


def implicit_score(item, behavior_by_type, max_click, max_stay, click_w, stay_w):
    t = item.get("typeId")
    if t is None:
        return 0.0
    stat = behavior_by_type.get(str(t)) or behavior_by_type.get(t)
    if not stat:
        return 0.0

    click_norm = normalize(safe_int(stat.get("clickCount"), 0), max_click)
    stay_norm = normalize(safe_int(stat.get("staySeconds"), 0), max_stay)
    return click_w * click_norm + stay_w * stay_norm


def diversify(scored_items, max_per_type=3):
    result = []
    overflow = []
    seen = {}

    for item in scored_items:
        t = item["candidate"].get("typeId")
        cnt = seen.get(t, 0)
        if cnt < max_per_type:
            result.append(item)
            seen[t] = cnt + 1
        else:
            overflow.append(item)

    result.extend(overflow)
    return result


def main():
    raw = sys.stdin.read().strip()
    if not raw:
        print(json.dumps({"orderedIds": [], "behaviorEnabled": False, "scores": {}}))
        return

    data = json.loads(raw)

    candidates = data.get("candidates") or []
    pref = data.get("preference")
    behavior_by_type = data.get("behaviorByType") or {}
    behavior_event_count = safe_int(data.get("behaviorEventCount"), 0)
    params = data.get("params") or {}

    behavior_switch_threshold = safe_int(params.get("behaviorSwitchThreshold"), 20)
    cold_pref_w = safe_float(params.get("coldPreferenceWeight"), 0.6)
    cold_hot_w = safe_float(params.get("coldHotWeight"), 0.4)
    mature_profile_w = safe_float(params.get("matureProfileWeight"), 0.8)
    mature_hot_w = safe_float(params.get("matureHotWeight"), 0.2)
    profile_explicit_w = safe_float(params.get("profileExplicitWeight"), 0.4)
    profile_implicit_w = safe_float(params.get("profileImplicitWeight"), 0.6)
    behavior_click_w = safe_float(params.get("behaviorClickWeight"), 0.4)
    behavior_stay_w = safe_float(params.get("behaviorStayWeight"), 0.6)

    behavior_enabled = behavior_event_count >= behavior_switch_threshold

    max_browse = max([safe_int(c.get("browseCount"), 0) for c in candidates], default=1)
    max_favorite = max([safe_int(c.get("favoriteCount"), 0) for c in candidates], default=1)
    max_rating = max([safe_float(c.get("averageRating"), 0.0) for c in candidates], default=5.0)

    all_click = [safe_int(v.get("clickCount"), 0) for v in behavior_by_type.values()] or [1]
    all_stay = [safe_int(v.get("staySeconds"), 0) for v in behavior_by_type.values()] or [1]
    max_click = max(all_click)
    max_stay = max(all_stay)

    scored = []
    score_map = {}
    prefer_type_id = normalize_type_id(pref.get("preferAttractionTypeId")) if pref else None

    for c in candidates:
        hs = hot_score(c, max_browse, max_favorite, max_rating)
        es = explicit_score(c, pref)
        ims = implicit_score(c, behavior_by_type, max_click, max_stay, behavior_click_w, behavior_stay_w)
        item_type_id = normalize_type_id(c.get("typeId"))
        type_match = prefer_type_id is not None and item_type_id == prefer_type_id

        if not pref:
            total = hs
        elif behavior_enabled:
            profile = profile_explicit_w * es + profile_implicit_w * ims
            total = mature_profile_w * profile + mature_hot_w * hs
        else:
            total = cold_pref_w * es + cold_hot_w * hs

        total = round(total, 6)
        scored.append({"candidate": c, "score": total, "typeMatch": type_match})
        aid = c.get("attractionId")
        if aid is not None:
            score_map[str(aid)] = total

    # 有显式偏好类型时，先保证偏好类型优先，再按综合分排序。
    if prefer_type_id is not None:
        scored.sort(key=lambda x: (1 if x["typeMatch"] else 0, x["score"]), reverse=True)
    else:
        scored.sort(key=lambda x: x["score"], reverse=True)
    scored = diversify(scored)

    ordered_ids = [x["candidate"].get("attractionId") for x in scored if x["candidate"].get("attractionId") is not None]

    out = {
        "orderedIds": ordered_ids,
        "behaviorEnabled": behavior_enabled,
        "scores": score_map,
    }
    print(json.dumps(out, ensure_ascii=False))


if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(json.dumps({"orderedIds": [], "behaviorEnabled": False, "scores": {}, "error": str(e)}, ensure_ascii=False))
        sys.exit(0)
