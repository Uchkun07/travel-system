#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
模拟退火算法 — 景点最优游览顺序求解
====================================
输入（stdin，JSON）：
  nodeCount        : int   总节点数（index 0 = 出发地，1..n-1 = 景点）
  distMatrix       : 2D    距离矩阵 (km)
  timeMatrix       : 2D    耗时矩阵 (min)
  costMatrix       : 2D    通行费矩阵 (元)
  timeWeight       : float 时间权重（0~1），费用权重 = 1 - timeWeight
  initialTemp      : float 初始温度
  coolingRate      : float 冷却系数 (0~1)
  minTemp          : float 终止温度
  iterationsPerTemp: int   每温度迭代次数

输出（stdout，JSON）：
  order : List[int]  从 index 1 开始的景点访问顺序（不含出发地 0）
  cost  : float      最优路径目标函数值（越小越优）

说明：
  目标函数 = timeWeight * 总耗时(min) + (1-timeWeight) * 总通行费(元)
  采用 Metropolis 准则接受劣解，避免陷入局部最优。
"""

import sys
import json
import math
import random


# ─────────────────────────── 工具函数 ──────────────────────────────────────

def calc_route_cost(order, time_matrix, cost_matrix, time_weight):
    """
    计算给定景点顺序的目标函数值。
    order: [景点节点 index 列表]，出发地固定为 0。
    路径：0 → order[0] → order[1] → ... → order[-1]
    """
    cost_weight = 1.0 - time_weight
    total = 0.0
    prev = 0  # 出发地
    for node in order:
        total += time_weight * time_matrix[prev][node] \
               + cost_weight * cost_matrix[prev][node]
        prev = node
    return total


def swap_neighbor(order):
    """随机交换两个景点位置（产生邻居解）"""
    i, j = random.sample(range(len(order)), 2)
    new_order = order[:]
    new_order[i], new_order[j] = new_order[j], new_order[i]
    return new_order


def reverse_segment_neighbor(order):
    """随机反转一段子序列（2-opt 扰动）"""
    if len(order) < 2:
        return order[:]
    i, j = sorted(random.sample(range(len(order)), 2))
    new_order = order[:i] + order[i:j+1][::-1] + order[j+1:]
    return new_order


def random_neighbor(order):
    """随机选择扰动方式"""
    return swap_neighbor(order) if random.random() < 0.5 \
           else reverse_segment_neighbor(order)


# ─────────────────────────── 主算法 ────────────────────────────────────────

def simulated_annealing(
        node_count,
        time_matrix,
        cost_matrix,
        time_weight,
        initial_temp,
        cooling_rate,
        min_temp,
        iterations_per_temp):
    """
    模拟退火求景点最优游览顺序。

    Returns
    -------
    best_order : List[int]   最优景点节点顺序（不含出发地 0）
    best_cost  : float       对应目标函数值
    """
    # 初始解：1,2,...,n-1（顺序访问）
    nodes = list(range(1, node_count))
    if len(nodes) == 0:
        return [], 0.0
    if len(nodes) == 1:
        return nodes, calc_route_cost(nodes, time_matrix, cost_matrix, time_weight)

    current_order = nodes[:]
    random.shuffle(current_order)
    current_cost = calc_route_cost(current_order, time_matrix, cost_matrix, time_weight)

    best_order = current_order[:]
    best_cost  = current_cost

    temp = initial_temp
    iter_count = 0

    while temp > min_temp:
        for _ in range(iterations_per_temp):
            new_order = random_neighbor(current_order)
            new_cost  = calc_route_cost(new_order, time_matrix, cost_matrix, time_weight)

            delta = new_cost - current_cost

            # Metropolis 准则：接受更优解，或以概率 e^(-delta/T) 接受劣解
            if delta < 0 or random.random() < math.exp(-delta / temp):
                current_order = new_order
                current_cost  = new_cost

                if current_cost < best_cost:
                    best_order = current_order[:]
                    best_cost  = current_cost

        temp *= cooling_rate
        iter_count += 1

    return best_order, best_cost


# ─────────────────────────── 入口 ──────────────────────────────────────────

def main():
    # 读取 stdin 中的 JSON 输入
    try:
        raw = sys.stdin.read().strip()
        if not raw:
            raise ValueError("stdin 为空")
        data = json.loads(raw)
    except Exception as e:
        print(f"[ERROR] 读取输入失败: {e}", file=sys.stderr)
        # 降级：返回顺序访问
        sys.exit(1)

    node_count         = int(data.get("nodeCount", 1))
    time_matrix        = data.get("timeMatrix", [[0]])
    cost_matrix        = data.get("costMatrix", [[0]])
    time_weight        = float(data.get("timeWeight", 0.5))
    initial_temp       = float(data.get("initialTemp", 10000.0))
    cooling_rate       = float(data.get("coolingRate", 0.995))
    min_temp           = float(data.get("minTemp", 1.0))
    iterations_per_temp = int(data.get("iterationsPerTemp", 100))

    print(f"[SA] nodes={node_count}, timeWeight={time_weight}, "
          f"T0={initial_temp}, cool={cooling_rate}", file=sys.stderr)

    best_order, best_cost = simulated_annealing(
        node_count, time_matrix, cost_matrix, time_weight,
        initial_temp, cooling_rate, min_temp, iterations_per_temp
    )

    print(f"[SA] best_cost={best_cost:.2f}, order={best_order}", file=sys.stderr)

    # 输出结果至 stdout（Java ProcessBuilder 读取）
    result = {"order": best_order, "cost": round(best_cost, 4)}
    print(json.dumps(result, ensure_ascii=False))


if __name__ == "__main__":
    main()
