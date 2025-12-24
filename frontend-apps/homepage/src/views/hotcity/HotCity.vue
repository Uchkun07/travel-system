<template>
  <div class="header">
    <div class="searchFrame">
      <h1 class="searchTitle">探索世界，发现热门旅行目的地</h1>
      <p class="subTitle">
        发现最受欢迎的旅行城市，获得专业旅行建议，计划您的完美旅程
      </p>
      <ElInput
        calss="searchInput"
        v-model="request.cityName"
        placeholder="搜索目的城市..."
        :prefix-icon="Search"
        @input="debounceHandleSearch"
      >
        <template #suffix>
          <ElButton
            class="searchButton"
            @click="handleSearch"
            type="primary"
            plain
            >搜索</ElButton
          >
        </template>
      </ElInput>
    </div>
  </div>
  <div class="cardFrame">
    <div class="filterFrame"><h1 class="frameTitle">热门旅游城市</h1></div>

    <!-- 首次加载提示 -->
    <div v-if="initialLoading" class="loading-text">加载中...</div>

    <!-- 城市列表 -->
    <div v-else-if="cityCards.length === 0" class="placeholder-text">
      暂无城市数据
    </div>
    <div v-else class="cityCardList">
      <cityCard
        v-for="(card, index) in cityCards"
        :key="index"
        :cityCard="card"
      />
    </div>

    <!-- 加载更多指示器 -->
    <div v-if="loadingMore" class="loading-more">
      <div class="spinner"></div>
      <span>加载更多城市...</span>
    </div>

    <!-- 没有更多数据提示 -->
    <div v-if="!hasMore && cityCards.length > 0" class="no-more-data">
      <span>已加载全部城市</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElInput, ElButton } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import cityCard from "@/components/hotcity/cityCard.vue";
import { ref, onMounted, onUnmounted } from "vue";
import {
  getCityCards,
  type CityQueryRequest,
  type CityCard,
} from "@/apis/city";

const request = ref<CityQueryRequest>({
  pageNum: 1,
  pageSize: 9,
  cityName: undefined,
  country: undefined,
  status: undefined,
});

const cityCards = ref<CityCard[]>([]); // 城市列表数据
const initialLoading = ref(false); // 首次加载
const loadingMore = ref(false); // 加载更多
const total = ref(0); // 总数
const hasMore = ref(true); // 是否还有更多数据
const isLoadingData = ref(false); // 防止重复请求

// 加载城市数据
const fetchCityCards = async (append: boolean = false) => {
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
    const response = await getCityCards(request.value);

    if (response.code === 200 && response.data) {
      const newCities = response.data.records;
      total.value = response.data.total;

      if (append) {
        // 追加模式：添加到现有列表
        cityCards.value = [...cityCards.value, ...newCities];
      } else {
        // 初始加载：替换列表
        cityCards.value = newCities;
      }

      // 检查是否还有更多数据
      hasMore.value = cityCards.value.length < total.value;

      // 如果还有数据，页码+1准备下次加载
      if (hasMore.value) {
        request.value.pageNum++;
      }
    }
  } catch (error) {
    console.error("Failed to fetch city cards:", error);
  } finally {
    initialLoading.value = false;
    loadingMore.value = false;
    isLoadingData.value = false;
  }
};

const handleSearch = () => {
  request.value.pageNum = 1;
  cityCards.value = []; // 清空列表
  hasMore.value = true; // 重置状态
  fetchCityCards(false);
};

let debounceTimer: ReturnType<typeof setTimeout> | null = null;
const debounceHandleSearch = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }
  debounceTimer = setTimeout(() => {
    handleSearch();
  }, 400);
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
  // 如果正在加载或没有更多数据，直接返回
  if (isLoadingData.value || !hasMore.value) return;

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;

  // 预加载策略：距离底部 500px 时开始加载
  const threshold = 500;
  const distanceToBottom = documentHeight - (scrollTop + windowHeight);

  if (distanceToBottom < threshold) {
    fetchCityCards(true); // 追加模式加载
  }
};

// 创建防抖版本的滚动处理函数
const debouncedHandleScroll = debounce(handleScroll, 200);

onMounted(() => {
  fetchCityCards(false); // 初始加载
  window.addEventListener("scroll", debouncedHandleScroll, { passive: true });
});

onUnmounted(() => {
  window.removeEventListener("scroll", debouncedHandleScroll);
  if (scrollTimeout) {
    clearTimeout(scrollTimeout);
  }
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }
});
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 384px;
  background: linear-gradient(
    168.69deg,
    rgba(26, 115, 232, 1),
    rgba(13, 71, 161, 1) 100%
  );
  .searchFrame {
    width: 800px;
    height: 256px;
    .searchTitle {
      color: rgba(255, 255, 255, 255);
      font-family: Noto Sans SC;
      font-size: 44.8px;
      font-weight: 700;
      line-height: 58.24px;
      letter-spacing: 0px;
      text-align: center;
    }
    .subTitle {
      color: rgba(255, 255, 255, 255);
      font-family: Noto Sans SC;
      font-size: 20px;
      font-weight: 300;
      line-height: 32px;
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
}

.cardFrame {
  padding: 0 240px;
  margin-bottom: 100px;
  .filterFrame {
    width: 100%;
    height: 45px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin: 40px 0 80px 0;
    .frameTitle {
      color: rgba(32, 33, 36, 255);
      font-family: Noto Sans SC;
      font-size: 28px;
      font-weight: 700;
      line-height: 44.8px;
      letter-spacing: 0px;
      text-align: left;
    }
  }
  .cityCardList {
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    gap: 32px;
  }

  .loading-text,
  .placeholder-text {
    width: 100%;
    text-align: center;
    padding: 60px 20px;
    color: #999;
    font-size: 1.1rem;
  }

  .loading-more {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 40px 20px;
    color: rgba(0, 188, 212, 255);
    font-size: 1rem;
    animation: fadeIn 0.3s ease;
  }

  .spinner {
    width: 24px;
    height: 24px;
    border: 3px solid #e0f7fa;
    border-top-color: rgba(0, 188, 212, 255);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  .no-more-data {
    width: 100%;
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
</style>
