<template>
  <div class="dashboard-root">
    <!-- Top overview cards -->
    <el-row :gutter="20" class="overview-row">
      <el-col
        :xs="24"
        :sm="12"
        :md="8"
        :lg="4"
        v-for="card in overviewCards"
        :key="card.title"
      >
        <el-card shadow="hover" class="overview-card">
          <div class="card-left">
            <div class="card-title">{{ card.title }}</div>
            <div class="card-value">{{ card.value }}</div>
            <div class="card-sub">{{ card.sub }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Main grid: left charts + right charts -->
    <div class="main-grid">
      <div class="left-col">
        <el-card class="panel" shadow="never">
          <div class="panel-title">近 30 天趋势</div>
          <div ref="lineChartRef" class="chart-box"></div>
        </el-card>

        <el-card class="panel" shadow="never">
          <div class="panel-title">热门景点 TOP10（按浏览量）</div>
          <div ref="barChartRef" class="chart-box"></div>
        </el-card>
      </div>

      <div class="right-col">
        <el-card class="panel" shadow="never">
          <div class="panel-title">用户出行人群分布</div>
          <div ref="pieGroupRef" class="chart-box small"></div>
        </el-card>

        <el-card class="panel" shadow="never">
          <div class="panel-title">出行方式分布</div>
          <div ref="pieModeRef" class="chart-box small"></div>
        </el-card>

        <el-card class="panel" shadow="never">
          <div class="panel-title">出行偏好分布</div>
          <div ref="donutRef" class="chart-box small"></div>
        </el-card>
      </div>
    </div>

    <!-- Bottom table -->
    <el-card class="panel bottom-panel" shadow="never">
      <div class="panel-title">最近 10 条路线规划记录</div>
      <el-table
        :data="recentRoutes"
        stripe
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa' }"
      >
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="departure" label="出发地" width="160" />
        <el-table-column prop="travelMode" label="出行方式" width="120" />
        <el-table-column prop="travelGroup" label="出行人群" width="120" />
        <el-table-column prop="budget" label="预算(元)" width="120" />
        <el-table-column prop="createdAt" label="创建时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";
import * as echarts from "echarts";
import { ElMessage } from "element-plus";
import { getDashboardData } from "@/apis/dataBoard";
import type {
  OverviewStats,
  TrendData,
  AttractionStat,
  DistItem,
  RecentRoute,
} from "@/apis/dataBoard";

// ── DOM Refs ──────────────────────────────────────────────────────────────────
const lineChartRef = ref<HTMLElement | null>(null);
const barChartRef = ref<HTMLElement | null>(null);
const pieGroupRef = ref<HTMLElement | null>(null);
const pieModeRef = ref<HTMLElement | null>(null);
const donutRef = ref<HTMLElement | null>(null);

let lineChart: echarts.ECharts | null = null;
let barChart: echarts.ECharts | null = null;
let pieGroupChart: echarts.ECharts | null = null;
let pieModeChart: echarts.ECharts | null = null;
let donutChart: echarts.ECharts | null = null;

// ── 响应式数据（由 API 填充，默认保留 Mock 兜底） ─────────────────────────────
const overviewCards =
  ref<{ title: string; value: string | number; sub: string }[]>(
    buildMockCards(),
  );

const recentRoutes = ref<RecentRoute[]>(buildMockRoutes());

// 图表数据（init 时读取这些变量）
let trendDates: string[] = [];
let trendUserGrowth: number[] = [];
let trendPlanGrowth: number[] = [];
let topAttractions: AttractionStat[] = [];
let groupDist: DistItem[] = [];
let modeDist: DistItem[] = [];
let prefDist: DistItem[] = [];

// ── Mock 兜底数据 ─────────────────────────────────────────────────────────────

function buildMockCards() {
  return [
    { title: "累计用户数", value: "—", sub: "加载中…" },
    { title: "今日新增用户", value: "—", sub: "加载中…" },
    { title: "总景点数", value: "—", sub: "加载中…" },
    { title: "路线规划总次数", value: "—", sub: "加载中…" },
    { title: "总城市数", value: "—", sub: "加载中…" },
    { title: "轮播图点击量", value: "—", sub: "加载中…" },
  ];
}

function buildMockRoutes(): RecentRoute[] {
  const departures = ["北京", "上海", "广州", "成都"];
  const modes = ["自驾", "火车", "飞机"];
  const groups = ["独自", "亲子", "家庭", "朋友"];
  return Array.from({ length: 5 }, (_, i) => ({
    username: `user_${1000 + i}`,
    departure: departures[i % departures.length] ?? "—",
    travelMode: modes[i % modes.length] ?? "—",
    travelGroup: groups[i % groups.length] ?? "—",
    budget: String(Math.round(3000 + i * 500)),
    createdAt: new Date(Date.now() - i * 3600 * 1000 * 6).toLocaleString(),
  }));
}

function buildMockTrend() {
  const today = new Date();
  trendDates = Array.from({ length: 30 }, (_, i) => {
    const d = new Date(today.getTime() - (29 - i) * 86400000);
    return `${d.getMonth() + 1}/${d.getDate()}`;
  });
  trendUserGrowth = Array.from({ length: 30 }, () =>
    Math.round(Math.random() * 50 + 20),
  );
  trendPlanGrowth = Array.from({ length: 30 }, () =>
    Math.round(Math.random() * 30 + 5),
  );
}

function buildMockChartData() {
  topAttractions = Array.from({ length: 10 }, (_, i) => ({
    name: `景点 ${10 - i}`,
    views: Math.round(Math.random() * 5000 + 1000 + i * 200),
  }));
  groupDist = [
    { name: "独自", value: 25 },
    { name: "亲子", value: 18 },
    { name: "家庭", value: 37 },
    { name: "朋友", value: 20 },
  ];
  modeDist = [
    { name: "自驾", value: 45 },
    { name: "火车", value: 30 },
    { name: "飞机", value: 25 },
  ];
  prefDist = [
    { name: "经济", value: 30 },
    { name: "舒适", value: 40 },
    { name: "适中", value: 15 },
    { name: "小众", value: 8 },
    { name: "深度", value: 7 },
  ];
}

// ── 从 API 加载数据 ────────────────────────────────────────────────────────────

async function loadDashboardData() {
  try {
    const res = await getDashboardData();
    if (res.code !== 200 || !res.data) {
      ElMessage.warning("Dashboard 数据获取异常，使用默认数据展示");
      return;
    }
    const d = res.data;

    // 概览卡
    const ov: OverviewStats = d.overview;
    overviewCards.value = [
      {
        title: "累计用户数",
        value: ov.totalUsers.toLocaleString(),
        sub: `今日新增：${ov.todayNewUsers}`,
      },
      {
        title: "今日新增用户",
        value: ov.todayNewUsers,
        sub: "实时统计",
      },
      {
        title: "总景点数",
        value: ov.totalAttractions.toLocaleString(),
        sub: `覆盖城市：${ov.totalCities}`,
      },
      {
        title: "路线规划总次数",
        value: ov.totalRoutePlans.toLocaleString(),
        sub: "已规划路线",
      },
      {
        title: "总城市数",
        value: ov.totalCities.toLocaleString(),
        sub: "上线城市",
      },
      {
        title: "轮播图点击量",
        value: ov.slideshowClicks.toLocaleString(),
        sub: "所有轮播图累计",
      },
    ];

    // 趋势
    const tr: TrendData = d.thirtyDayTrend;
    trendDates = tr.dates;
    trendUserGrowth = tr.userGrowth;
    trendPlanGrowth = tr.planGrowth;

    // 图表数据
    topAttractions = d.topAttractions?.length
      ? d.topAttractions
      : topAttractions;
    groupDist = d.groupDist?.length ? d.groupDist : groupDist;
    modeDist = d.modeDist?.length ? d.modeDist : modeDist;
    prefDist = d.prefDist?.length ? d.prefDist : prefDist;

    // 最近路线
    recentRoutes.value = d.recentRoutes?.length
      ? d.recentRoutes
      : recentRoutes.value;
  } catch (e) {
    console.error("Dashboard API 请求失败，使用 Mock 数据", e);
    ElMessage.warning("网络异常，使用示例数据展示");
  }
}

// ── 图表初始化 ────────────────────────────────────────────────────────────────

function initLineChart() {
  if (!lineChartRef.value) return;
  lineChart = echarts.init(lineChartRef.value);
  lineChart.setOption({
    color: ["#4f9eff", "#72d6a3"],
    tooltip: { trigger: "axis" },
    legend: { data: ["用户增长", "路线规划次数"], top: 8 },
    grid: { left: "6%", right: "4%", bottom: "6%", top: "18%" },
    xAxis: { type: "category", data: trendDates, boundaryGap: false },
    yAxis: { type: "value" },
    series: [
      { name: "用户增长", type: "line", smooth: true, data: trendUserGrowth },
      {
        name: "路线规划次数",
        type: "line",
        smooth: true,
        data: trendPlanGrowth,
      },
    ],
  } as any);
}

function initBarChart() {
  if (!barChartRef.value) return;
  barChart = echarts.init(barChartRef.value);
  const sorted = [...topAttractions].sort((a, b) => a.views - b.views);
  barChart.setOption({
    color: ["#6ea8fe"],
    tooltip: { trigger: "axis" },
    grid: { left: "6%", right: "6%", bottom: "6%", top: "10%" },
    xAxis: { type: "value" },
    yAxis: {
      type: "category",
      data: sorted.map((t) => t.name),
      axisLabel: { interval: 0 },
    },
    series: [
      {
        name: "浏览量",
        type: "bar",
        data: sorted.map((t) => t.views),
        barMaxWidth: 18,
      },
    ],
  } as any);
}

function initPieCharts() {
  if (pieGroupRef.value) {
    pieGroupChart = echarts.init(pieGroupRef.value);
    pieGroupChart.setOption({
      tooltip: { trigger: "item" },
      legend: { bottom: 4 },
      series: [
        {
          name: "出行人群",
          type: "pie",
          radius: ["35%", "65%"],
          avoidLabelOverlap: false,
          data: groupDist,
        },
      ],
    } as any);
  }
  if (pieModeRef.value) {
    pieModeChart = echarts.init(pieModeRef.value);
    pieModeChart.setOption({
      tooltip: { trigger: "item" },
      legend: { bottom: 4 },
      series: [
        {
          name: "出行方式",
          type: "pie",
          radius: ["35%", "65%"],
          data: modeDist,
        },
      ],
    } as any);
  }
  if (donutRef.value) {
    donutChart = echarts.init(donutRef.value);
    donutChart.setOption({
      tooltip: { trigger: "item" },
      legend: { bottom: 4, type: "scroll" },
      series: [
        {
          name: "出行偏好",
          type: "pie",
          radius: ["40%", "70%"],
          data: prefDist,
        },
      ],
    } as any);
  }
}

// ── Resize ────────────────────────────────────────────────────────────────────

function resizeAll() {
  [lineChart, barChart, pieGroupChart, pieModeChart, donutChart].forEach(
    (c) => c && c.resize(),
  );
}

// ── 生命周期 ──────────────────────────────────────────────────────────────────

onMounted(async () => {
  // 先用 Mock 数据填充图表变量，保证即使 API 失败也能渲染
  buildMockTrend();
  buildMockChartData();

  // 请求真实数据（会覆盖 Mock 变量）
  await loadDashboardData();

  // 初始化图表
  try {
    initLineChart();
    initBarChart();
    initPieCharts();
    window.addEventListener("resize", resizeAll);
  } catch (e) {
    console.error(e);
    ElMessage.error("图表初始化失败");
  }
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", resizeAll);
  [lineChart, barChart, pieGroupChart, pieModeChart, donutChart].forEach(
    (c) => c && c.dispose(),
  );
});
</script>

<style scoped>
.dashboard-root {
  padding: 24px;
  background: #f4f6f9;
  min-height: 100vh;
}
.overview-row {
  margin-bottom: 18px;
}
.overview-card {
  height: 100px;
  display: flex;
  align-items: center;
  padding: 14px;
}
.card-left {
  display: flex;
  flex-direction: column;
}
.card-title {
  color: #6b7280;
  font-size: 14px;
}
.card-value {
  font-size: 22px;
  font-weight: 700;
  margin-top: 6px;
}
.card-sub {
  color: #9ca3af;
  font-size: 12px;
  margin-top: 4px;
}

.main-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 20px;
}
.left-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.right-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.panel {
  padding: 12px 16px;
}
.panel-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: #374151;
}
.chart-box {
  width: 100%;
  height: 320px;
}
.chart-box.small {
  height: 220px;
}
.bottom-panel {
  margin-top: 20px;
}

/* responsive */
@media (max-width: 1000px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
  .chart-box {
    height: 300px;
  }
}
</style>
