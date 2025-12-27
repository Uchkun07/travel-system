<template>
  <div class="explore-page">
    <header>
      <img
        class="head-image"
        src="https://images.unsplash.com/photo-1545569341-9eb8b30979d9"
        alt="探索页图片"
      />
      <div class="search">
        <h1 class="searchTitle">下一站，去哪里？</h1>
        <ElInput
          v-model="searchKeyword"
          class="searchInput"
          placeholder="搜索景点或者标签..."
          :prefix-icon="Search"
          @keyup.enter="handleSearch"
        >
          <template #suffix>
            <ElButton
              class="searchButton"
              type="primary"
              plain
              @click="handleSearch"
            >
              搜索
            </ElButton>
          </template>
        </ElInput>
      </div>
      <div class="overlay"></div>
    </header>
    <div class="content">
      <div class="tagsCardFrame">
        <div class="tagsCard" @click="handleTypeClick(3, '自然风光')">
          <ExploreNature class="icon" />
          <span class="title">自然风光</span>
          <span class="total">245个梦幻目的地</span>
        </div>
        <div class="tagsCard" @click="handleTypeClick(4, '文化古迹')">
          <ExploreHistoricalSite class="icon" />
          <span class="title">文化古迹</span>
          <span class="total">189个历史遗迹</span>
        </div>
        <div class="tagsCard" @click="handleTypeClick(6, '休闲度假')">
          <ExploreHoliday class="icon" />
          <span class="title">休闲度假</span>
          <span class="total">512个度假胜地</span>
        </div>
        <div class="tagsCard" @click="handleTypeClick(8, '小众秘境')">
          <ExploreTravel class="icon" />
          <span class="title">小众秘境</span>
          <span class="total">120个隐秘之地</span>
        </div>
      </div>
      <div class="hotAttractions" ref="resultsSection">
        <div class="header">
          <div class="title-section">
            <h2 class="main-title">
              {{
                isSearchMode
                  ? `搜索结果 (${attractionCards.length})`
                  : isViewAllMode
                  ? `全部景点 (${attractionCards.length})`
                  : isTypeMode
                  ? `${selectedTypeName} (${attractionCards.length})`
                  : "本月热门目的地"
              }}
            </h2>
            <p class="subtitle">
              {{
                isSearchMode
                  ? searchKeyword
                    ? `关键词: ${searchKeyword}`
                    : "请输入搜索关键词"
                  : isViewAllMode
                  ? "按热度值排序展示"
                  : isTypeMode
                  ? `探索${selectedTypeName}相关景点`
                  : "基于社区 2,000,000+ 旅行者的真实评分"
              }}
            </p>
          </div>
          <button v-if="isSearchMode" class="view-all" @click="clearSearch">
            返回热门
            <el-icon><ArrowRight /></el-icon>
          </button>
          <button v-else-if="isViewAllMode" class="view-all" @click="backToTop">
            返回首页
            <el-icon><ArrowRight /></el-icon>
          </button>
          <button
            v-else-if="isTypeMode"
            class="view-all"
            @click="clearTypeSearch"
          >
            返回热门
            <el-icon><ArrowRight /></el-icon>
          </button>
          <button v-else class="view-all" @click="handleViewAll">
            查看全部
            <el-icon><ArrowRight /></el-icon>
          </button>
        </div>

        <div v-if="loading" class="loading-state">
          <el-icon class="is-loading" :size="40"><Loading /></el-icon>
          <p>加载中...</p>
        </div>

        <div v-else-if="attractionCards.length === 0" class="empty-state">
          <p>
            {{ isSearchMode ? "未找到相关景点，请尝试其他关键词" : "暂无数据" }}
          </p>
        </div>

        <div v-else class="cards-grid">
          <AttractionCard
            v-for="attraction in attractionCards"
            :key="attraction.id"
            :attraction="attraction"
          />
        </div>

        <!-- 加载更多按钮 (在查看全部模式或类型筛选模式下显示) -->
        <div v-if="isViewAllMode && hasMore" class="load-more-container">
          <ElButton
            class="load-more-btn"
            :loading="loadingMore"
            @click="loadMoreAttractions"
          >
            {{ loadingMore ? "加载中..." : "加载更多" }}
          </ElButton>
        </div>
        <div v-if="isTypeMode && hasMore" class="load-more-container">
          <ElButton
            class="load-more-btn"
            :loading="loadingMore"
            @click="loadMoreTypeAttractions"
          >
            {{ loadingMore ? "加载中..." : "加载更多" }}
          </ElButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { ElInput, ElButton, ElMessage } from "element-plus";
import { Search, ArrowRight, Loading } from "@element-plus/icons-vue";
import ExploreNature from "@/assets/svgs/Explore_nature.vue";
import ExploreHistoricalSite from "@/assets/svgs/Explore_historical_site.vue";
import ExploreHoliday from "@/assets/svgs/Explore_holiday.vue";
import ExploreTravel from "@/assets/svgs/Explore_travel.vue";
import AttractionCard from "@/components/recommend-attraction/attractionCard.vue";
import {
  getTopThreeAttractions,
  getAttractionCards,
  type AttractionCard as AttractionCardType,
} from "@/apis/attraction";

const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// 搜索关键词
const searchKeyword = ref("");
// 是否为搜索模式
const isSearchMode = ref(false);
// 是否为查看全部模式
const isViewAllMode = ref(false);
// 是否为类型筛选模式
const isTypeMode = ref(false);
// 当前选中的类型名称
const selectedTypeName = ref("");
// 结果区域的引用
const resultsSection = ref<HTMLElement | null>(null);

// 热门景点数据
const topAttractions = ref<AttractionCardType[]>([]);
// 搜索结果数据
const searchResults = ref<AttractionCardType[]>([]);
// 类型搜索结果数据
const typeResults = ref<AttractionCardType[]>([]);
// 查看全部数据
const allAttractions = ref<AttractionCardType[]>([]);
// 当前选中的类型ID
const currentTypeId = ref<number | null>(null);
// 加载状态
const loading = ref(false);
const loadingMore = ref(false);

// 分页信息
const currentPage = ref(1);
const pageSize = ref(12);
const totalPages = ref(0);
const hasMore = ref(false);

// 转换后端数据为组件所需格式
const attractionCards = computed(() => {
  let data: AttractionCardType[] = [];
  if (isSearchMode.value) {
    data = searchResults.value;
  } else if (isViewAllMode.value) {
    data = allAttractions.value;
  } else if (isTypeMode.value) {
    data = typeResults.value;
  } else {
    data = topAttractions.value;
  }

  return data.map((attraction) => ({
    id: attraction.attractionId,
    title: attraction.name,
    location: attraction.location || "未知地点",
    description: attraction.description || attraction.name,
    image: attraction.imageUrl?.startsWith("http")
      ? attraction.imageUrl
      : `${baseUrl}${attraction.imageUrl}`,
    price: attraction.ticketPrice || 0,
    rating: attraction.averageRating || 0,
    badge: attraction.type || undefined,
  }));
});

// 获取热门景点数据
const fetchTopAttractions = async () => {
  loading.value = true;
  try {
    const response = await getTopThreeAttractions();
    if (response.code === 200 && response.data) {
      topAttractions.value = response.data;
    }
  } catch (error) {
    console.error("获取热门景点失败:", error);
    ElMessage.error("获取热门景点失败");
  } finally {
    loading.value = false;
  }
};

// 按类型搜索景点
const handleTypeClick = async (typeId: number, typeName: string) => {
  loading.value = true;
  isTypeMode.value = true;
  isSearchMode.value = false;
  isViewAllMode.value = false;
  selectedTypeName.value = typeName;
  currentTypeId.value = typeId;
  currentPage.value = 1;

  try {
    const response = await getAttractionCards({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      typeId: typeId,
    });

    if (response.code === 200 && response.data) {
      typeResults.value = response.data.records || [];
      totalPages.value = response.data.totalPages || 0;
      hasMore.value = currentPage.value < totalPages.value;

      // 滚动到结果区域
      setTimeout(() => {
        resultsSection.value?.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
      }, 100);

      if (typeResults.value.length === 0) {
        ElMessage.info(`暂无${typeName}相关景点`);
      }
    }
  } catch (error) {
    console.error("获取类型景点失败:", error);
    ElMessage.error("获取景点列表失败，请稍后重试");
    typeResults.value = [];
  } finally {
    loading.value = false;
  }
};

// 搜索景点
const handleSearch = async () => {
  const keyword = searchKeyword.value.trim();

  if (!keyword) {
    ElMessage.warning("请输入搜索关键词");
    return;
  }

  loading.value = true;
  isSearchMode.value = true;
  isTypeMode.value = false;
  isViewAllMode.value = false;

  try {
    const response = await getAttractionCards({
      pageNum: 1,
      pageSize: 20,
      name: keyword,
    });

    if (response.code === 200 && response.data) {
      searchResults.value = response.data.records || [];

      // 滚动到结果区域
      setTimeout(() => {
        resultsSection.value?.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
      }, 100);

      if (searchResults.value.length === 0) {
        ElMessage.info("未找到相关景点");
      }
    }
  } catch (error) {
    console.error("搜索失败:", error);
    ElMessage.error("搜索失败，请稍后重试");
    searchResults.value = [];
  } finally {
    loading.value = false;
  }
};

// 查看全部景点（按热度排序）
const handleViewAll = async () => {
  loading.value = true;
  isViewAllMode.value = true;
  isSearchMode.value = false;
  isTypeMode.value = false;
  currentPage.value = 1;

  try {
    const response = await getAttractionCards({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    });

    if (response.code === 200 && response.data) {
      allAttractions.value = response.data.records || [];
      totalPages.value = response.data.totalPages || 0;
      hasMore.value = currentPage.value < totalPages.value;

      // 滚动到结果区域
      setTimeout(() => {
        resultsSection.value?.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
      }, 100);
    }
  } catch (error) {
    console.error("获取全部景点失败:", error);
    ElMessage.error("获取景点列表失败");
    allAttractions.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载更多类型景点
const loadMoreTypeAttractions = async () => {
  if (loadingMore.value || !hasMore.value || currentTypeId.value === null)
    return;

  loadingMore.value = true;
  currentPage.value += 1;

  try {
    const response = await getAttractionCards({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      typeId: currentTypeId.value,
    });

    if (response.code === 200 && response.data) {
      const newAttractions = response.data.records || [];
      typeResults.value = [...typeResults.value, ...newAttractions];
      hasMore.value = currentPage.value < (response.data.totalPages || 0);
    }
  } catch (error) {
    console.error("加载更多失败:", error);
    ElMessage.error("加载更多失败");
  } finally {
    loadingMore.value = false;
  }
};

// 加载更多景点
const loadMoreAttractions = async () => {
  if (loadingMore.value || !hasMore.value) return;

  loadingMore.value = true;
  currentPage.value += 1;

  try {
    const response = await getAttractionCards({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    });

    if (response.code === 200 && response.data) {
      const newAttractions = response.data.records || [];
      allAttractions.value = [...allAttractions.value, ...newAttractions];
      hasMore.value = currentPage.value < (response.data.totalPages || 0);
    }
  } catch (error) {
    console.error("加载更多失败:", error);
    ElMessage.error("加载更多失败");
  } finally {
    loadingMore.value = false;
  }
};

// 返回首页
const backToTop = () => {
  isViewAllMode.value = false;
  allAttractions.value = [];
  currentPage.value = 1;
  hasMore.value = false;
};

// 清除搜索，返回热门景点// 清除搜索，返回热门景点
const clearSearch = () => {
  searchKeyword.value = "";
  isSearchMode.value = false;
  searchResults.value = [];
};

// 清除类型搜索，返回热门景点
const clearTypeSearch = () => {
  isTypeMode.value = false;
  selectedTypeName.value = "";
  currentTypeId.value = null;
  typeResults.value = [];
  currentPage.value = 1;
  hasMore.value = false;
};

onMounted(() => {
  fetchTopAttractions();
});
</script>

<style scoped>
.explore-page {
  min-height: calc(100vh - 75px);

  header {
    position: relative;
    width: 100%;
    height: 80vh;
    .head-image {
      width: 100%;
      height: 80vh;
    }
    .search {
      position: absolute;
      max-width: 56rem;
      z-index: 10;
      left: 50%;
      top: 50%;
      transform: translate(-50%, -50%);
      .searchTitle {
        color: rgba(255, 255, 255, 255);
        font-family: Noto Sans SC;
        font-size: 80px;
        font-weight: 900;
        line-height: 58.24px;
        letter-spacing: 0px;
        text-align: center;
      }
      :deep(.el-input__wrapper) {
        /* div.search-container */
        width: 700px;
        height: 67px;
        /* 自动布局 */
        display: flex;
        flex-direction: row;
        justify-content: flex-start;
        align-items: center;
        padding: 8px;
        box-sizing: border-box;
        border-radius: 48px;
        box-shadow: 0px 4px 20px 0px rgba(0, 0, 0, 0.2);
        background: rgba(255, 255, 255, 255);
      }
      :deep(.el-input__inner) {
        width: auto;
        color: rgba(176, 176, 176, 255);
        font-family: Noto Sans SC;
        font-size: 17px;
        font-weight: 400;
        line-height: 25px;
        letter-spacing: 0px;
        text-align: left;
      }
      :deep(.el-input__prefix-inner) {
        width: 59.19px;
        height: 51px;
      }
      :deep(.el-icon .el-input__icon) {
        margin-right: 0;
      }
      .searchButton {
        width: 123.13px;
        height: 51px;
        border-radius: 40px;
        background: rgba(0, 188, 212, 255);
        color: rgba(255, 255, 255, 255);
        font-family: Arial;
        font-size: 16.8px;
        font-weight: 500;
        line-height: 19px;
        letter-spacing: 0px;
        text-align: center;
      }
    }
    .overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(
        to bottom,
        rgba(15, 23, 42, 0.4),
        rgba(15, 23, 42, 0.2),
        rgb(248, 250, 252)
      );
    }
  }
  .content {
    width: 100%;
    height: auto;
    max-width: 80rem;
    margin-left: auto;
    margin-right: auto;
    padding-left: 1.5rem;
    padding-right: 1.5rem;
    margin-top: -5rem;
    position: relative;
    z-index: 20;
    padding-bottom: 5rem;
    .tagsCardFrame {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      width: 100%;
      height: auto;
      min-width: 1024px;
      gap: 24px;
      .tagsCard {
        width: 100%;
        height: 169px;
        max-width: 290px;
        box-sizing: border-box;
        padding: 24px;
        background: rgba(255, 255, 255, 0.7);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border: 1px solid rgba(255, 255, 255, 0.3);
        border-radius: 1.5rem;
        border-bottom: 4px solid #0ea5e9;
        cursor: pointer;
        transition: all 700ms;
        opacity: 1;
        transform: translateY(0);
        display: flex;
        flex-direction: column;
        align-items: center;
        text-align: center;
        gap: 12px;

        .icon {
          width: 48px;
          height: 48px;
        }

        .title {
          font-weight: bold;
          font-size: 18px;
          margin-bottom: 4px;
        }

        .total {
          color: #64748b;
          font-size: 14px;
          font-style: italic;
        }

        &:hover {
          box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1),
            0 10px 10px -5px rgba(0, 0, 0, 0.04);
          transform: scale(1.02);
        }
      }
    }

    .hotAttractions {
      margin-top: 80px;

      .header {
        display: flex;
        align-items: flex-end;
        justify-content: space-between;
        margin-bottom: 32px;

        .title-section {
          .main-title {
            font-size: 30px;
            font-weight: bold;
            color: #1e293b;
            margin-bottom: 8px;
          }

          .subtitle {
            color: #64748b;
            font-size: 14px;
          }
        }

        .view-all {
          display: flex;
          align-items: center;
          gap: 8px;
          color: #0ea5e9;
          font-weight: bold;
          background: none;
          border: none;
          cursor: pointer;
          transition: gap 0.3s;

          &:hover {
            gap: 12px;
          }
        }
      }

      .cards-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 32px;
      }

      .loading-state,
      .empty-state {
        text-align: center;
        padding: 60px 20px;
        color: #64748b;

        .is-loading {
          margin-bottom: 16px;
          color: #0ea5e9;
        }

        p {
          font-size: 16px;
          margin: 0;
        }
      }

      .empty-state p {
        color: #94a3b8;
      }

      .load-more-container {
        display: flex;
        justify-content: center;
        margin-top: 40px;
        padding-bottom: 20px;

        .load-more-btn {
          padding: 12px 48px;
          font-size: 16px;
          font-weight: bold;
          border-radius: 24px;
          background: #0ea5e9;
          color: white;
          border: none;
          cursor: pointer;
          transition: all 0.3s;

          &:hover {
            background: #0284c7;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(14, 165, 233, 0.3);
          }
        }
      }
    }
  }
}
</style>
