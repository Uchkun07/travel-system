<template>
  <div class="container">
    <div class="banner">
      <img class="img" :src="attractionDetail?.mainImageUrl" alt="mainImage" />
      <div class="banner-overlay">
        <h1>{{ attractionDetail?.name }}</h1>
        <div class="location">
          <span class="address"
            ><i class="fa-solid fa-location-dot"></i
            >{{ attractionDetail?.address }}</span
          >
          <span class="rating">
            <i class="fa-solid fa-star"></i>
            <span class="rating-text"
              >{{ attractionDetail?.averageRating }}（{{
                attractionDetail?.browseCount
              }}人评论）</span
            >
          </span>
        </div>
      </div>
    </div>

    <div class="mainContent">
      <!-- 左侧内容区 -->
      <div class="contentLeft">
        <!-- 景点概览 -->
        <div class="detailCard">
          <h2 class="attractionTitle">景点概览</h2>
          <p class="attractionDescription">
            {{ attractionDetail?.description || "暂无描述" }}
          </p>
          <div class="tipsFrame">
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-clock"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">最佳观光季节</h2>
                <span class="tipsInfo">{{
                  attractionDetail?.bestSeason || "暂无信息"
                }}</span>
              </div>
            </div>
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-ticket-alt"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">开放时间</h2>
                <span class="tipsInfo">{{
                  attractionDetail?.openingHours || "暂无信息"
                }}</span>
              </div>
            </div>
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-sun"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">建议游玩时间</h2>
                <span class="tipsInfo">{{
                  attractionDetail?.estimatedPlayTime
                    ? `${attractionDetail.estimatedPlayTime} 小时`
                    : "暂无信息"
                }}</span>
              </div>
            </div>
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-history"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">历史底蕴</h2>
                <span class="tipsInfo">{{
                  attractionDetail?.historicalContext || "暂无信息"
                }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 图片画廊 -->
        <div class="detailCard" v-if="galleryImages.length > 0">
          <h2 class="attractionTitle">景点掠影</h2>
          <div class="imageGallery">
            <div
              class="galleryItem"
              v-for="(img, index) in galleryImages"
              :key="index"
              :class="{ 'first-item': index === 0 }"
            >
              <img :src="img" :alt="`景点图片${index + 1}`" />
            </div>
          </div>
        </div>

        <!-- 旅行者实用贴士 -->
        <div class="detailCard">
          <h2 class="attractionTitle">旅行者实用贴士</h2>
          <div class="tipsFrame">
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-utensils"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">附近美食</h2>
                <div v-if="nearbyFoodList.length > 0" class="foodList">
                  <div
                    v-for="(food, index) in nearbyFoodList"
                    :key="index"
                    class="foodItem"
                  >
                    <strong>{{ food.name }}</strong>
                    <span v-if="food.description"
                      >：{{ food.description }}</span
                    >
                  </div>
                </div>
                <span v-else class="tipsInfo">暂无推荐</span>
              </div>
            </div>
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-bus"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">交通指南</h2>
                <span class="tipsInfo">{{
                  attractionDetail?.transportationGuide || "暂无信息"
                }}</span>
              </div>
            </div>
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-shield-alt"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">安全提示</h2>
                <span class="tipsInfo">{{
                  attractionDetail?.safetyTips || "暂无特殊提示"
                }}</span>
              </div>
            </div>
            <div class="tipsCard">
              <span class="tipsIcon"><i class="fas fa-globe"></i></span>
              <div class="tipsText">
                <h2 class="infoTitle">官方网站</h2>
                <span class="tipsInfo">
                  <a
                    v-if="attractionDetail?.officialWebsite"
                    :href="attractionDetail.officialWebsite"
                    target="_blank"
                    class="website-link"
                  >
                    点击访问
                  </a>
                  <span v-else>暂无信息</span>
                </span>
              </div>
            </div>
          </div>

          <!-- 地图容器 -->
          <div class="mapContainer">
            <div id="amap-container" ref="mapContainer" v-show="showMap"></div>
            <div v-if="!showMap" class="mapPlaceholder" @click="initMap">
              <h3>{{ attractionDetail?.name || "景点" }}位置</h3>
              <p>点击下方按钮查看交互式景区地图，精准导航至景点</p>
              <button class="mapActionBtn">查看地图</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧边栏 -->
      <aside class="sidebar">
        <!-- 关键信息卡片 -->
        <div class="sidebarCard">
          <h2 class="attractionTitle">关键信息</h2>
          <div class="infoSummary">
            <div class="infoRow">
              <span class="infoLabel">开放时间</span>
              <span class="infoValue">{{
                attractionDetail?.openingHours || "全天开放"
              }}</span>
            </div>
            <div class="infoRow">
              <span class="infoLabel">门票价格</span>
              <span class="infoValue">
                {{
                  attractionDetail?.ticketPrice
                    ? `¥${attractionDetail.ticketPrice}`
                    : "免费"
                }}
              </span>
            </div>
            <div class="infoRow">
              <span class="infoLabel">建议时长</span>
              <span class="infoValue">
                {{
                  attractionDetail?.estimatedPlayTime
                    ? `${attractionDetail.estimatedPlayTime} 分钟`
                    : "半天"
                }}
              </span>
            </div>
            <div class="infoRow">
              <span class="infoLabel">最佳季节</span>
              <span class="infoValue">{{
                attractionDetail?.bestSeason || "四季皆宜"
              }}</span>
            </div>
            <div class="infoRow">
              <span class="infoLabel">人气指数</span>
              <span class="infoValue">{{
                attractionDetail?.popularity || 0
              }}</span>
            </div>
            <div class="infoRow">
              <span class="infoLabel">浏览量</span>
              <span class="infoValue">{{
                attractionDetail?.browseCount || 0
              }}</span>
            </div>
          </div>

          <h3 class="attractionTitle" style="margin-top: 2rem">景点特色标签</h3>
          <div
            class="badgeContainer"
            v-if="attractionDetail?.tags && attractionDetail.tags.length > 0"
          >
            <span
              class="badge"
              v-for="tag in attractionDetail.tags"
              :key="tag.tagId"
            >
              {{ tag.tagName }}
            </span>
          </div>
          <div class="badgeContainer" v-else>
            <span class="badge">精选景点</span>
          </div>

          <button
            class="bookingBtn"
            @click="toggleCollect"
            :disabled="collectLoading"
          >
            <i :class="isCollected ? 'fas fa-heart' : 'far fa-heart'"></i>
            {{ isCollected ? "已收藏" : "收藏景点" }}
          </button>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getAttractionDetail, type AttractionDetail } from "@/apis/attraction";
import { useCollectionStore } from "@/stores/collection";
import { useUserStore } from "@/stores/user";
import { createBrowseTracker, type BrowseTracker } from "@/utils/browseTracker";

const route = useRoute();
const router = useRouter();
const collectionStore = useCollectionStore();
const userStore = useUserStore();

const attractionDetail = ref<AttractionDetail | null>(null);
const loading = ref(true);
const collectLoading = ref(false);
const showMap = ref(false);
const mapContainer = ref<HTMLElement | null>(null);
let mapInstance: any = null;
let browseTracker: BrowseTracker | null = null;

declare global {
  interface Window {
    AMap: any;
    _AMapSecurityConfig: any;
  }
}

// 计算图片库
const galleryImages = computed(() => {
  if (!attractionDetail.value?.multiImageUrls) return [];
  try {
    const urls = JSON.parse(attractionDetail.value.multiImageUrls);
    return Array.isArray(urls) ? urls : [];
  } catch {
    return [];
  }
});

// 计算附近美食列表
const nearbyFoodList = computed(() => {
  if (!attractionDetail.value?.nearbyFood) return [];
  try {
    const foods = JSON.parse(attractionDetail.value.nearbyFood);
    return Array.isArray(foods) ? foods : [];
  } catch {
    return [];
  }
});

// 收藏状态
const isCollected = computed(() => {
  const id = attractionDetail.value?.attractionId;
  return id ? collectionStore.isCollected(id) : false;
});

// 加载景点详情
const loadAttractionDetail = async () => {
  const attractionId = Number(route.params.id);
  if (!attractionId || !Number.isFinite(attractionId)) {
    ElMessage.error("无效的景点ID");
    router.push("/");
    return;
  }

  loading.value = true;
  try {
    const response = await getAttractionDetail(attractionId);
    if (response.code === 200 && response.data) {
      attractionDetail.value = response.data;

      // 启动浏览追踪（仅登录用户）
      if (userStore.isLoggedIn && userStore.userId) {
        browseTracker = createBrowseTracker(userStore.userId, attractionId, {
          reportInterval: 30000, // 每30秒上报一次
          autoStart: true, // 自动开始追踪
          trackVisibility: true, // 追踪页面可见性
        });
      }
    } else {
      ElMessage.error(response.message || "加载景点详情失败");
      router.push("/");
    }
  } catch (error: any) {
    console.error("加载景点详情失败:", error);
    ElMessage.error("加载景点详情失败");
    router.push("/");
  } finally {
    loading.value = false;
  }
};

// 切换收藏
const toggleCollect = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再收藏");
    return;
  }

  const id = attractionDetail.value?.attractionId;
  if (!id) return;

  collectLoading.value = true;
  await collectionStore.toggleCollection(id);
  collectLoading.value = false;
};

// 初始化地图
const initMap = () => {
  if (!attractionDetail.value) {
    ElMessage.warning("景点信息加载中，请稍候");
    return;
  }

  const { latitude, longitude } = attractionDetail.value;

  if (!latitude || !longitude) {
    ElMessage.warning("该景点暂无位置信息");
    return;
  }

  if (!window.AMap) {
    ElMessage.error("地图API加载失败，请刷新页面重试");
    return;
  }

  showMap.value = true;

  // 等待DOM更新后初始化地图
  setTimeout(() => {
    if (!mapContainer.value) return;

    mapInstance = new window.AMap.Map("amap-container", {
      zoom: 15,
      center: [longitude, latitude],
      viewMode: "3D",
      pitch: 30,
    });

    // 添加标记
    const marker = new window.AMap.Marker({
      position: new window.AMap.LngLat(longitude, latitude),
      title: attractionDetail.value?.name || "景点位置",
      icon: new window.AMap.Icon({
        size: new window.AMap.Size(40, 50),
        image:
          "//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png",
        imageSize: new window.AMap.Size(40, 50),
      }),
    });

    mapInstance.add(marker);

    // 添加信息窗体
    const infoWindow = new window.AMap.InfoWindow({
      content: `
        <div style="padding: 10px;">
          <h3 style="margin: 0 0 8px 0; font-size: 16px; color: #2e7d32;">${
            attractionDetail.value?.name || "景点"
          }</h3>
          <p style="margin: 0; font-size: 14px; color: #666;">${
            attractionDetail.value?.address || "位置信息"
          }</p>
        </div>
      `,
      offset: new window.AMap.Pixel(0, -30),
    });

    marker.on("click", () => {
      infoWindow.open(mapInstance, marker.getPosition());
    });
  }, 100);
};

onMounted(() => {
  loadAttractionDetail();
});

// 组件卸载时停止追踪
onBeforeUnmount(async () => {
  if (browseTracker) {
    await browseTracker.stop();
    browseTracker = null;
  }
});
</script>

<style scoped lang="scss">
.container {
  width: 100%;
  height: auto;
  padding: 20px 18rem;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.banner {
  height: 450px;
  width: 100%;
  border-radius: 1rem;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
}

.banner .img {
  display: block;
  width: 100%;
  height: 100%;
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 2rem;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;

  h1 {
    position: absolute;
    bottom: 60px;
    left: 40px;
    color: white;
    font-size: 40px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
  }

  .location {
    display: flex;
    width: 100%;
    height: auto;
    gap: 1rem;

    .address {
      color: rgba(255, 255, 255, 255);
      font-family: Noto Sans SC;
      font-size: 17.6px;
      font-weight: 400;
      line-height: 28.16px;
      letter-spacing: 0px;
      text-align: left;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    .rating {
      height: 35px;
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      align-items: center;
      gap: 8px;
      padding: 3.2px 12.8px;
      border-radius: 20px;
      background: rgba(255, 255, 255, 0.2);

      .rating-text {
        color: rgba(255, 255, 255, 255);
        font-family: Noto Sans SC;
        font-size: 17.6px;
        font-weight: 400;
        line-height: 28.16px;
        letter-spacing: 0px;
        text-align: left;
      }
    }
  }
}

/* 主内容区 */
.mainContent {
  display: grid;
  grid-template-columns: 3fr 1fr;
  gap: 2.5rem;
  padding: 1rem 0 3rem;
}

/* 左侧内容 */
.contentLeft {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.detailCard {
  min-height: 200px;
  border-radius: 12px;
  padding: 2rem;
  box-sizing: border-box;
  box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 255);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;

  .attractionTitle {
    color: rgba(46, 125, 50, 255);
    font-family: Noto Sans SC;
    font-size: 24px;
    font-weight: 700;
    line-height: 38.4px;
    letter-spacing: 0px;
    text-align: left;
    margin: 0px;
    position: relative;
    padding-left: 1.2rem;

    &::before {
      content: "";
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      height: 60%;
      width: 4px;
      background: #ff9800;
      border-radius: 2px;
    }
  }

  .attractionDescription {
    color: rgba(84, 110, 122, 255);
    font-family: Noto Sans SC;
    font-size: 16px;
    font-weight: 400;
    line-height: 28.8px;
    letter-spacing: 0px;
    text-align: left;
  }

  .tipsFrame {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;

    .tipsCard {
      display: flex;
      flex-direction: row;
      justify-content: flex-start;
      align-items: flex-start;
      gap: 16px;

      .tipsIcon {
        width: 50px;
        height: 50px;
        display: flex;
        flex-direction: row;
        justify-content: center;
        align-items: center;
        border-radius: 12px;
        background: rgba(46, 125, 50, 0.1);
        flex-shrink: 0;

        i {
          color: rgba(46, 125, 50, 255);
          font-size: 20px;
        }
      }

      .tipsText {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 6px;

        .infoTitle {
          color: rgba(38, 50, 56, 255);
          font-family: Noto Sans SC;
          font-size: 17.6px;
          font-weight: 700;
          line-height: 28.16px;
          letter-spacing: 0px;
          text-align: left;
          margin: 0px;
        }

        .tipsInfo {
          color: rgba(84, 110, 122, 255);
          font-family: Noto Sans SC;
          font-size: 15.2px;
          font-weight: 400;
          line-height: 24.32px;
          letter-spacing: 0px;
          text-align: left;

          .website-link {
            color: rgba(46, 125, 50, 255);
            text-decoration: none;
            transition: all 0.3s ease;

            &:hover {
              text-decoration: underline;
              color: rgba(76, 175, 80, 255);
            }
          }
        }

        .foodList {
          display: flex;
          flex-direction: column;
          .foodItem {
            color: rgba(84, 110, 122, 255);
            font-family: Noto Sans SC;
            font-size: 15.2px;
            font-weight: 400;
            line-height: 24.32px;
            letter-spacing: 0px;
            text-align: left;

            strong {
              color: rgba(84, 110, 122, 255);
              font-weight: 400;
            }
          }
        }
      }
    }
  }

  .mapContainer {
    margin-top: 2rem;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

    #amap-container {
      width: 100%;
      height: 400px;
    }

    .mapPlaceholder {
      width: 100%;
      height: 400px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: white;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;

      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><path d="M50 10 L90 90 L10 90 Z" fill="rgba(255,255,255,0.05)"/></svg>')
          center/80px 80px;
        opacity: 0.1;
        pointer-events: none;
      }

      &:hover {
        transform: scale(1.02);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);

        .mapActionBtn {
          background: #fff;
          color: #667eea;
          transform: translateY(-2px);
          box-shadow: 0 8px 20px rgba(255, 255, 255, 0.3);
        }
      }

      h3 {
        font-size: 1.8rem;
        margin: 0 0 1rem 0;
        font-weight: 700;
        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
        z-index: 1;
      }

      p {
        font-size: 1rem;
        margin: 0 0 2rem 0;
        opacity: 0.95;
        z-index: 1;
      }

      .mapActionBtn {
        padding: 1rem 2.5rem;
        background: rgba(255, 255, 255, 0.2);
        color: white;
        border: 2px solid white;
        border-radius: 50px;
        font-size: 1.1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        backdrop-filter: blur(10px);
        z-index: 1;

        &:hover {
          background: white;
          color: #667eea;
        }
      }
    }
  }
}

/* 图片画廊 */
.imageGallery {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.2rem;

  .galleryItem {
    border-radius: 8px;
    height: 220px;
    overflow: hidden;
    cursor: pointer;

    &.first-item {
      grid-column: 1 / 3;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }

    &:hover img {
      transform: scale(1.08);
    }
  }
}

/* 右侧边栏 */
.sidebar {
  position: sticky;
  top: 6rem;
  height: fit-content;
}

.sidebarCard {
  background: white;
  border-radius: 12px;
  box-shadow: 0px 4px 12px 0px rgba(0, 0, 0, 0.08);
  padding: 2rem;

  .attractionTitle {
    color: rgba(46, 125, 50, 255);
    font-family: Noto Sans SC;
    font-size: 24px;
    font-weight: 700;
    line-height: 38.4px;
    letter-spacing: 0px;
    text-align: left;
    margin: 0 0 1.5rem 0;
    position: relative;
    padding-left: 1.2rem;

    &::before {
      content: "";
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      height: 60%;
      width: 4px;
      background: #ff9800;
      border-radius: 2px;
    }
  }

  .infoSummary {
    display: flex;
    flex-direction: column;
    gap: 1.2rem;

    .infoRow {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding-bottom: 1.2rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);

      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }

      .infoLabel {
        flex: 0 0 100px;
        font-weight: 500;
        color: rgba(38, 50, 56, 255);
      }

      .infoValue {
        flex: 1;
        color: rgba(84, 110, 122, 255);
      }
    }
  }

  .badgeContainer {
    display: flex;
    flex-wrap: wrap;
    gap: 0.8rem;
    margin: 1.5rem 0;

    .badge {
      background: rgba(46, 125, 50, 0.1);
      color: rgba(46, 125, 50, 255);
      padding: 0.5rem 1rem;
      border-radius: 30px;
      font-size: 0.9rem;
      font-weight: 500;
    }
  }

  .bookingBtn {
    display: block;
    width: 100%;
    background: #ff9800;
    color: white;
    text-align: center;
    padding: 1rem;
    border: none;
    border-radius: 8px;
    font-weight: 600;
    font-size: 1.1rem;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: 1rem;

    &:hover:not(:disabled) {
      background: #f57c00;
      transform: translateY(-3px);
      box-shadow: 0 8px 20px rgba(255, 152, 0, 0.3);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    i {
      margin-right: 0.5rem;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1100px) {
  .mainContent {
    grid-template-columns: 2fr 1fr;
  }

  .imageGallery {
    grid-template-columns: 1fr;

    .galleryItem.first-item {
      grid-column: 1;
    }
  }
}

@media (max-width: 768px) {
  .container {
    padding: 20px 2rem;
  }

  .mainContent {
    grid-template-columns: 1fr;
    padding: 2rem 0;
  }

  .detailCard .tipsFrame {
    grid-template-columns: 1fr;
  }

  .imageGallery {
    grid-template-columns: 1fr 1fr;

    .galleryItem.first-item {
      grid-column: 1 / 3;
    }
  }

  .sidebar {
    position: static;
  }
}

@media (max-width: 576px) {
  .container {
    padding: 0 1.2rem;
  }

  .banner {
    height: 300px;

    .banner-overlay h1 {
      font-size: 2rem;
      bottom: 50px;
      left: 20px;
    }

    .banner-overlay .location {
      flex-direction: column;
      align-items: flex-start;
    }
  }

  .imageGallery {
    grid-template-columns: 1fr;

    .galleryItem.first-item {
      grid-column: 1;
    }
  }
}
</style>
