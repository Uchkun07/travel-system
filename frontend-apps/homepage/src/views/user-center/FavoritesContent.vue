<template>
  <div class="favorites-content">
    <!-- 页面头部 -->
    <div class="content-header">
      <h2 class="content-title">我的收藏</h2>
      <div class="header-actions">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索收藏的目的地..."
          :prefix-icon="Search"
          style="width: 300px"
        />
      </div>
    </div>

    <!-- 收藏统计 -->
    <div class="favorites-stats">
      <div class="stat-item">
        <ElIcon><Star /></ElIcon>
        <span>共 {{ favorites.length }} 个收藏</span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <ElIcon class="is-loading"><Loading /></ElIcon>
      <p>加载中...</p>
    </div>

    <!-- 收藏列表 - 使用 attractionCard 组件 -->
    <div v-else-if="filteredFavorites.length > 0" class="favorites-grid">
      <attractionCard
        v-for="attraction in filteredFavorites"
        :key="attraction.id"
        :attraction="attraction"
        @favorite="handleFavorite"
      />
    </div>

    <!-- 空状态 -->
    <ElEmpty v-else description="还没有收藏任何目的地" :image-size="200">
      <ElButton type="primary" @click="handleExplore"> 去探索 </ElButton>
    </ElEmpty>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { ElInput, ElIcon, ElButton, ElEmpty, ElMessage } from "element-plus";
import { Search, Star, Loading } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";
import attractionCard from "@/components/recommend-attraction/attractionCard.vue";
import { getAttractionsByIds, type AttractionCard } from "@/apis/attraction";
import { useCollectionStore } from "@/stores/collection";

// 组件所需的景点数据格式（与 attractionCard 组件匹配）
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

const router = useRouter();
const collectionStore = useCollectionStore();
const searchKeyword = ref("");
const loading = ref(false);

// 收藏的景点ID列表
const collectedIds = ref<number[]>([]);
// 所有景点列表
const allAttractions = ref<AttractionCard[]>([]);

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

// 收藏的景点列表（转换后的格式）
const favorites = computed(() => {
  return allAttractions.value
    .filter((attraction) =>
      collectedIds.value.includes(attraction.attractionId)
    )
    .map(transformAttraction);
});

// 过滤后的收藏列表
const filteredFavorites = computed(() => {
  if (!searchKeyword.value) return favorites.value;

  const keyword = searchKeyword.value.toLowerCase();
  return favorites.value.filter(
    (item) =>
      item.title.toLowerCase().includes(keyword) ||
      item.location.toLowerCase().includes(keyword)
  );
});

// 加载收藏数据
const loadFavorites = async () => {
  loading.value = true;
  try {
    // 确保 collection store 已初始化
    if (!collectionStore.initialized) {
      await collectionStore.initializeCollections();
    }

    // 直接从store中获取收藏的景点ID列表（已持久化）
    collectedIds.value = Array.from(collectionStore.collectedIds);

    // 如果有收藏的景点，批量获取景点详情
    if (collectedIds.value.length > 0) {
      const attractionsResponse = await getAttractionsByIds(collectedIds.value);
      if (attractionsResponse.code === 200 && attractionsResponse.data) {
        allAttractions.value = attractionsResponse.data;
      }
    } else {
      allAttractions.value = [];
    }
  } catch (error: any) {
    console.error("加载收藏数据失败:", error);
    if (error.response?.status === 401) {
      ElMessage.error("请先登录");
      router.push("/");
    } else {
      ElMessage.error("加载收藏数据失败");
    }
  } finally {
    loading.value = false;
  }
};

// 组件挂载时加载数据
onMounted(() => {
  loadFavorites();
});

// 处理收藏事件（由 attractionCard 组件触发）
const handleFavorite = (id: number | string, isFavorite: boolean) => {
  // 当用户取消收藏时，从列表中移除
  if (!isFavorite) {
    const numId = Number(id);
    const index = collectedIds.value.indexOf(numId);
    if (index > -1) {
      collectedIds.value.splice(index, 1);
    }
  }
};

// 去探索
const handleExplore = () => {
  router.push("/");
};
</script>

<style scoped>
.favorites-content {
  width: 100%;
}

/* 内容头部 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.content-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 收藏统计 */
.favorites-stats {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px 20px;
  margin-bottom: 30px;
  display: flex;
  gap: 20px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #2c3e50;
  font-weight: 600;
}

.stat-item :deep(.el-icon) {
  font-size: 20px;
  color: #3498db;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #95a5a6;
}

.loading-container :deep(.el-icon) {
  font-size: 48px;
  margin-bottom: 16px;
  color: #3498db;
}

.loading-container p {
  font-size: 16px;
  margin: 0;
}

/* 收藏网格 - 使用与推荐景点页面相同的布局 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-header {
    flex-direction: column;
    gap: 1.5rem;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions :deep(.el-input) {
    width: 100% !important;
  }

  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>
