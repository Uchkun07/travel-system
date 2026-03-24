<template>
  <div class="route-result-page">
    <!-- 加载层 -->
    <div v-if="!result" class="loading-screen">
      <el-icon :size="60" class="spin"><Loading /></el-icon>
      <p>路线规划中，请稍候…</p>
    </div>

    <template v-else>
      <!-- 顶部摘要 -->
      <div class="result-header">
        <div class="header-left">
          <h1 class="page-title">🗺️ 路线规划结果</h1>
          <p class="page-sub">按最优游览顺序排列，已综合路费与时间</p>
        </div>
        <el-button type="default" size="large" @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 重新规划
        </el-button>
      </div>

      <!-- 超预算警告 -->
      <el-alert
        v-if="result.overBudget"
        :title="result.tips"
        type="warning"
        show-icon
        :closable="false"
        class="budget-alert"
      />
      <el-alert
        v-else
        :title="result.tips"
        type="success"
        show-icon
        :closable="false"
        class="budget-alert"
      />

      <!-- 汇总卡片 -->
      <div class="summary-cards">
        <div class="s-card">
          <div class="s-label">💰 预估总费用</div>
          <div class="s-value primary">
            ¥{{ result.summary.totalCost.toLocaleString() }}
          </div>
        </div>
        <div class="s-card">
          <div class="s-label">⏱ 总路途时长</div>
          <div class="s-value">
            {{ formatDuration(result.summary.totalDurationMinutes) }}
          </div>
        </div>
        <div class="s-card">
          <div class="s-label">📍 总行驶距离</div>
          <div class="s-value">{{ result.summary.totalDistanceKm }} km</div>
        </div>
        <div class="s-card">
          <div class="s-label">🚗 出行方式</div>
          <div class="s-value">{{ result.summary.travelMode }}</div>
        </div>
        <div class="s-card">
          <div class="s-label">🎯 出行偏好</div>
          <div class="s-value">{{ result.summary.preference }}</div>
        </div>
      </div>

      <!-- 路线步骤 -->
      <div class="linef">
        <!-- 地图展示 -->
        <div ref="mapContainer" class="amap-container"></div>

        <div class="steps-list">
          <div v-for="step in result.steps" :key="step.order" class="step-card">
            <!-- 步骤序号 -->
            <div class="step-badge">{{ step.order }}</div>

            <!-- 途经提示（从上一站/出发地） -->
            <div v-if="step.order > 1" class="travel-info">
              <el-icon><Right /></el-icon>
              <span>
                路途：{{ step.travelDistanceKm }} km ·
                {{ step.travelDurationMinutes }} 分钟 · 通行费 ¥{{
                  step.travelCost
                }}
              </span>
            </div>
            <div v-else class="travel-info first">
              <el-icon><Location /></el-icon>
              <span>出发 → 第一站</span>
            </div>

            <!-- 景点主体 -->
            <div class="step-body">
              <div class="step-image">
                <img
                  :src="imageUrl(step.imageUrl)"
                  :alt="step.name"
                  @error="onImgError($event)"
                />
              </div>
              <div class="step-detail">
                <div class="step-name">{{ step.name }}</div>
                <div class="step-meta-row">
                  <span class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    预计到达：{{ step.arrivalDate }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Timer /></el-icon>
                    游览时长：约 {{ step.estimatedPlayMinutes }} 分钟
                  </span>
                  <span class="meta-item" v-if="step.ticketPrice > 0">
                    🎫 门票：¥{{ step.ticketPrice }}
                  </span>
                  <span class="meta-item" v-if="step.averageRating > 0">
                    ⭐ {{ step.averageRating.toFixed(1) }} 分
                  </span>
                </div>
                <div class="step-meta-row">
                  <span class="meta-item weather">🌤 {{ step.weather }}</span>
                  <el-tag
                    :type="crowdTagType(step.crowdLevel)"
                    size="small"
                    class="crowd-tag"
                  >
                    👥 人流量：{{ step.crowdLevel }}
                  </el-tag>
                </div>
                <div class="step-addr" v-if="step.address">
                  <el-icon><Location /></el-icon>
                  {{ step.address }}
                </div>
              </div>
            </div>
          </div>

          <!-- 终点 -->
          <div class="step-end">
            <el-icon><CircleCheckFilled /></el-icon>
            <span>旅程终点 · 共 {{ result.steps.length }} 个景点</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { useRouter } from "vue-router";
import {
  Loading,
  ArrowLeft,
  Right,
  Location,
  Calendar,
  Timer,
  CircleCheckFilled,
} from "@element-plus/icons-vue";
import type { RoutePlanResult } from "@/apis/routePlan";

const AMAP_KEY = "7af4638cb0fd5d313df83870b542eba6";

const router = useRouter();
const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const result = ref<RoutePlanResult | null>(null);
const mapContainer = ref<HTMLDivElement | null>(null);
// eslint-disable-next-line @typescript-eslint/no-explicit-any
let mapInstance: any = null;

// ── 从 history.state 读取规划结果 ──────────────────────────────────────────
onMounted(async () => {
  const raw = history.state?.result;
  if (raw) {
    try {
      result.value = JSON.parse(raw) as RoutePlanResult;
    } catch (e) {
      console.error("规划结果解析失败", e);
    }
  }
  if (!result.value) {
    router.replace({ name: "RouteLine" });
    return;
  }
  await nextTick();
  initMap();
});

onUnmounted(() => {
  if (mapInstance) {
    mapInstance.destroy();
    mapInstance = null;
  }
});

// ── 高德地图 ──────────────────────────────────────────────────────────────

function loadAMapScript(): Promise<void> {
  return new Promise((resolve, reject) => {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    if ((window as any).AMap) {
      resolve();
      return;
    }
    const cbName = "__amapInitCallback_" + Date.now();
    const script = document.createElement("script");
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${AMAP_KEY}&callback=${cbName}`;
    script.async = true;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    (window as any)[cbName] = () => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      delete (window as any)[cbName];
      resolve();
    };
    script.onerror = () => reject(new Error("AMap 脚本加载失败"));
    document.head.appendChild(script);
  });
}

async function initMap() {
  if (!result.value || !mapContainer.value) return;
  try {
    await loadAMapScript();
  } catch (e) {
    console.warn("地图加载失败，跳过地图展示", e);
    return;
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const AMap = (window as any).AMap;
  const steps = result.value.steps;
  if (!steps || steps.length === 0) return;

  const firstStep = steps[0]!;
  mapInstance = new AMap.Map(mapContainer.value, {
    zoom: 8,
    center: [firstStep.longitude, firstStep.latitude], 
    mapStyle: "amap://styles/light",
  });

  // 序号标记点
  steps.forEach((step) => {
    const markerEl = document.createElement("div");
    markerEl.className = "custom-map-marker";
    markerEl.textContent = String(step.order);
    const marker = new AMap.Marker({
      position: new AMap.LngLat(step.longitude, step.latitude),
      content: markerEl,
      offset: new AMap.Pixel(-16, -16),
      title: step.name,
    });
    // 信息窗
    const info = new AMap.InfoWindow({
      content: `<div style="padding:6px 10px;font-size:13px"><b>${step.name}</b><br/>门票：¥${step.ticketPrice}</div>`,
      offset: new AMap.Pixel(0, -36),
    });
    marker.on("click", () => info.open(mapInstance, marker.getPosition()));
    mapInstance.add(marker);
  });

  // 折线连接各景点
  const path = steps.map((s) => new AMap.LngLat(s.longitude, s.latitude));
  const polyline = new AMap.Polyline({
    path,
    strokeColor: "#1a73e8",
    strokeWeight: 4,
    strokeOpacity: 0.85,
    lineJoin: "round",
    lineCap: "round",
    showDir: true,
  });
  mapInstance.add(polyline);
  mapInstance.setFitView(undefined, false, [20, 20, 20, 20]);
}

// ── 工具函数 ──────────────────────────────────────────────────────────────

function formatDuration(minutes: number): string {
  if (minutes < 60) return `${minutes} 分钟`;
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return m > 0 ? `${h} 小时 ${m} 分钟` : `${h} 小时`;
}

function imageUrl(url: string): string {
  if (!url) return "/img/placeholder.png";
  return url.startsWith("http") ? url : `${baseUrl}${url}`;
}

function onImgError(e: Event) {
  const img = e.target as HTMLImageElement;
  img.src = "/img/placeholder.png";
}

function crowdTagType(level: string): "success" | "warning" | "danger" {
  if (level === "低") return "success";
  if (level === "中") return "warning";
  return "danger";
}

function goBack() {
  router.push({ name: "RouteLine" });
}
</script>

<style scoped lang="scss">
.route-result-page {
  padding: 32px 18rem;
  min-height: calc(100vh - 75px);
  background: #f8f9fa;

  @media (max-width: 1200px) {
    padding: 24px 2rem;
  }
  @media (max-width: 768px) {
    padding: 16px 1rem;
  }
}

// ── 加载 ────────────────────────────────────────────────────────────────────
.loading-screen {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
  color: #5f6368;
  font-size: 1.1rem;

  .spin {
    animation: spin 1.2s linear infinite;
    color: #1a73e8;
  }
}
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

// ── 顶部 ─────────────────────────────────────────────────────────────────────
.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .page-title {
    font-size: 1.8rem;
    font-weight: 700;
    color: #202124;
    margin: 0 0 6px;
  }
  .page-sub {
    color: #5f6368;
    margin: 0;
    font-size: 0.95rem;
  }
}

.budget-alert {
  margin-bottom: 20px;
}

// ── 汇总卡片 ─────────────────────────────────────────────────────────────────
.summary-cards {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 28px;

  .s-card {
    flex: 1;
    min-width: 150px;
    background: #fff;
    border-radius: 12px;
    padding: 16px 20px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);

    .s-label {
      font-size: 0.85rem;
      color: #6b7280;
      margin-bottom: 8px;
    }
    .s-value {
      font-size: 1.3rem;
      font-weight: 700;
      color: #202124;
    }
    .s-value.primary {
      color: #1a73e8;
    }
  }
}

// ── linef 布局 ────────────────────────────────────────────────────────────────
.linef {
  display: flex;
  gap: 24px;
  align-items: flex-start;

  @media (max-width: 1024px) {
    flex-direction: column;
  }
}

.amap-container {
  flex: 1;
  min-width: 0;
  height: 640px;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 20px;

  @media (max-width: 1024px) {
    width: 100%;
    height: 360px;
    position: static;
  }
}

// ── 步骤列表 ─────────────────────────────────────────────────────────────────
.steps-list {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.travel-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 24px;
  font-size: 0.9rem;
  color: #5f6368;
  border-left: 3px dashed #d1d5db;
  margin-left: 22px;

  &.first {
    border-left-color: #1a73e8;
    color: #1a73e8;
  }
}

.step-card {
  position: relative;
}

.step-badge {
  position: absolute;
  left: 0;
  top: 0;
  width: 44px;
  height: 44px;
  background: #1a73e8;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  font-weight: 700;
  z-index: 1;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.4);
}

.step-body {
  display: flex;
  gap: 20px;
  margin-left: 60px;
  margin-bottom: 4px;
  background: #fff;
  border-radius: 14px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.07);

  @media (max-width: 640px) {
    flex-direction: column;
    margin-left: 0;
    margin-top: 48px;
  }
}

.step-image {
  flex: 0 0 140px;
  height: 120px;
  border-radius: 10px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.step-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;

  .step-name {
    font-size: 1.2rem;
    font-weight: 700;
    color: #202124;
  }

  .step-meta-row {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 0.9rem;
    color: #374151;
  }

  .weather {
    color: #1a73e8;
  }

  .crowd-tag {
    font-weight: 500;
  }

  .step-addr {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 0.85rem;
    color: #9ca3af;
  }
}

.step-end {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
  padding: 14px 24px;
  background: #f0fdf4;
  border-radius: 12px;
  border: 1px solid #bbf7d0;
  color: #16a34a;
  font-weight: 600;

  .el-icon {
    font-size: 1.3rem;
  }
}
</style>

<!-- 高德地图自定义标记点（非 scoped，JS 动态插入） -->
<style lang="scss">
.custom-map-marker {
  width: 32px;
  height: 32px;
  background: #1a73e8;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.5);
  border: 2px solid #fff;
  cursor: pointer;
  transition: transform 0.15s;

  &:hover {
    transform: scale(1.15);
  }
}
</style>
