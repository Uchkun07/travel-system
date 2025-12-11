<template>
  <div class="recommend-attraction-page">
    <!-- 轮播图区域 -->
    <section class="hero-section">
      <Slideshow />
    </section>

    <!-- 推荐景点区域 -->
    <section class="attractions-section">
      <div class="section-header">
        <h2 class="section-title">热门景点推荐</h2>
        <p class="section-subtitle">发现最受欢迎的旅游目的地</p>
        <div v-if="userStore.isLoggedIn" class="collection-info">
          已收藏 {{ collectionStore.collectionCount }} 个景点
        </div>
      </div>

      <!-- 景点列表 -->
      <div v-if="initialLoading" class="loading-text">加载中...</div>
      <div v-else-if="attractions.length === 0" class="placeholder-text">
        暂无景点数据
      </div>
      <div v-else class="attraction-card">
        <attractionCard
          v-for="attraction in attractions"
          :key="attraction.id"
          :attraction="attraction"
          @favorite="handleFavorite"
        />
      </div>

      <!-- 加载更多指示器 -->
      <div v-if="loadingMore" class="loading-more">
        <div class="spinner"></div>
        <span>加载更多景点...</span>
      </div>

      <!-- 没有更多数据提示 -->
      <div v-if="!hasMore && attractions.length > 0" class="no-more-data">
        <span>已加载全部景点</span>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from "vue";
import { ElMessage } from "element-plus";
import Slideshow from "@/components/recommend-attraction/slideshow.vue";
import attractionCard from "@/components/recommend-attraction/attractionCard.vue";
import {
  getAttractionCards,
  type AttractionCard,
  type AttractionQueryRequest,
} from "@/apis/attraction";
import { useCollectionStore } from "@/stores/collection";
import { useUserStore } from "@/stores/user";

// 组件所需的景点数据格式
interface AttractionCardData {
  id: number | string;
  title: string;
  location: string;
  description: string;
  image: string;
  price: number;
  rating: number;
  badge?: string;
}

// 景点数据
const attractions = ref<AttractionCardData[]>([]);
const initialLoading = ref(false); // 首次加载
const loadingMore = ref(false); // 加载更多
const currentPage = ref(1);
const pageSize = ref(20); // 优化单次加载数量
const total = ref(0);
const hasMore = ref(true); // 是否还有更多数据
const isLoadingData = ref(false); // 防止重复请求

// Store
const collectionStore = useCollectionStore();
const userStore = useUserStore();

// 数据转换：将后端数据转换为组件需要的格式
const transformAttraction = (
  attraction: AttractionCard
): AttractionCardData => ({
  id: attraction.attractionId,
  title: attraction.name,
  location: attraction.location,
  description: attraction.description || "暂无描述",
  image: attraction.imageUrl || "/img/default-attraction.png",
  price: attraction.ticketPrice || 0,
  rating: attraction.averageRating || 0,
  badge: attraction.type,
});

// 加载景点数据
const loadAttractions = async (append: boolean = false) => {
  // 防止重复请求
  if (isLoadingData.value) return;

  // 检查是否还有更多数据
  if (append && !hasMore.value) return;

  isLoadingData.value = true;

  if (append) {
    loadingMore.value = true;
  } else {
    initialLoading.value = true;
  }

  try {
    const params: AttractionQueryRequest = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    };

    const response = await getAttractionCards(params);

    if (response.code === 200 && response.data) {
      const newAttractions = response.data.records.map(transformAttraction);
      total.value = response.data.total;

      if (append) {
        // 追加模式:添加到现有列表
        attractions.value = [...attractions.value, ...newAttractions];
      } else {
        // 初始加载:替换列表
        attractions.value = newAttractions;
      }

      // 检查是否还有更多数据
      hasMore.value = attractions.value.length < total.value;

      // 如果还有数据,页码+1准备下次加载
      if (hasMore.value) {
        currentPage.value++;
      }
    }

    // 如果用户已登录，初始化收藏列表
    if (userStore.isLoggedIn && !collectionStore.initialized) {
      await collectionStore.initializeCollections();
    }
  } catch (error: any) {
    ElMessage.error("加载景点数据失败: " + (error?.message || "未知错误"));
    console.error("加载景点失败:", error);
  } finally {
    initialLoading.value = false;
    loadingMore.value = false;
    isLoadingData.value = false;
  }
};

// 处理收藏事件
const handleFavorite = (id: number | string, isFavorite: boolean) => {
  console.log(`景点 ${id} 收藏状态: ${isFavorite}`);
};

// 防抖函数
let scrollTimeout: number | null = null;
const debounce = (func: Function, delay: number) => {
  return (...args: any[]) => {
    if (scrollTimeout) {
      clearTimeout(scrollTimeout);
    }
    scrollTimeout = window.setTimeout(() => {
      func(...args);
    }, delay);
  };
};

// 滚动处理函数
const handleScroll = () => {
  // 如果正在加载或没有更多数据,直接返回
  if (isLoadingData.value || !hasMore.value) return;

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;

  // 预加载策略:距离底部 300px 时开始加载
  const threshold = 300;
  const distanceToBottom = documentHeight - (scrollTop + windowHeight);

  if (distanceToBottom < threshold) {
    loadAttractions(true); // 追加模式加载
  }
};

// 创建防抖版本的滚动处理函数
const debouncedHandleScroll = debounce(handleScroll, 200);

// 监听登录状态变化
watch(
  () => userStore.isLoggedIn,
  (newVal) => {
    if (newVal && attractions.value.length > 0) {
      // 用户登录后初始化收藏列表
      collectionStore.initializeCollections();
    }
  }
);

// 组件挂载时加载数据并添加滚动监听
onMounted(() => {
  loadAttractions(false); // 初始加载
  window.addEventListener("scroll", debouncedHandleScroll, { passive: true });
});

// 组件卸载时移除滚动监听
onUnmounted(() => {
  window.removeEventListener("scroll", debouncedHandleScroll);
  if (scrollTimeout) {
    clearTimeout(scrollTimeout);
  }
});
</script>

<style scoped>
.recommend-attraction-page {
  min-height: calc(100vh - 75px);
}

.attractions-section {
  padding: 60px 18rem;
  background: #f8f9fa;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-title {
  font-size: 2.5rem;
  color: #2d88ff;
  margin-bottom: 12px;
  font-weight: 600;
}

.section-subtitle {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 8px;
}

.collection-info {
  font-size: 1rem;
  color: #999;
  background: #e8f4ff;
  padding: 8px 16px;
  border-radius: 20px;
  display: inline-block;
}

.attraction-card {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.loading-text,
.placeholder-text {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 1.1rem;
}

.loading-more {
  grid-column: 1 / -1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
  color: #2d88ff;
  font-size: 1rem;
  animation: fadeIn 0.3s ease;
}

.spinner {
  width: 24px;
  height: 24px;
  border: 3px solid #e8f4ff;
  border-top-color: #2d88ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.no-more-data {
  grid-column: 1 / -1;
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 0.95rem;

  span {
    display: inline-block;
    padding: 8px 24px;
    background: #f5f5f5;
    border-radius: 20px;
    position: relative;

    &::before,
    &::after {
      content: "";
      position: absolute;
      top: 50%;
      width: 60px;
      height: 1px;
      background: linear-gradient(to right, transparent, #ddd, transparent);
    }

    &::before {
      right: 100%;
      margin-right: 16px;
    }

    &::after {
      left: 100%;
      margin-left: 16px;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .hero-section,
  .attractions-section {
    padding-left: 10rem;
    padding-right: 10rem;
  }
}

@media (max-width: 1024px) {
  .hero-section,
  .attractions-section {
    padding-left: 4rem;
    padding-right: 4rem;
  }

  .section-title {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .hero-section,
  .attractions-section {
    padding-left: 2rem;
    padding-right: 2rem;
  }

  .hero-section {
    padding-top: 20px;
    padding-bottom: 40px;
  }

  .attractions-section {
    padding-top: 40px;
    padding-bottom: 40px;
  }

  .section-title {
    font-size: 1.8rem;
  }

  .section-subtitle {
    font-size: 1rem;
  }

  .attraction-card {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .hero-section,
  .attractions-section {
    padding-left: 1rem;
    padding-right: 1rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .section-subtitle {
    font-size: 0.9rem;
  }
}
</style>
