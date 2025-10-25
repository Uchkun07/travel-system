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

    <!-- 收藏列表 -->
    <div v-if="filteredFavorites.length > 0" class="favorites-grid">
      <div
        v-for="item in filteredFavorites"
        :key="item.id"
        class="favorite-card"
      >
        <div class="favorite-image">
          <img :src="item.image" :alt="item.title" />
          <div class="favorite-badge">
            <ElIcon><Star /></ElIcon>
          </div>
        </div>
        <div class="favorite-content">
          <h4 class="favorite-title">{{ item.title }}</h4>
          <div class="favorite-location">
            <ElIcon><Location /></ElIcon>
            <span>{{ item.location }}</span>
          </div>
          <p class="favorite-description">{{ item.description }}</p>
          <div class="favorite-footer">
            <div class="favorite-price">
              <span class="price-label">起</span>
              <span class="price-value">¥{{ item.price }}</span>
            </div>
            <div class="favorite-actions">
              <ElButton size="small" @click="handleView(item)">
                查看详情
              </ElButton>
              <ElButton
                size="small"
                type="danger"
                plain
                @click="handleRemove(item.id)"
              >
                <ElIcon><Delete /></ElIcon>
              </ElButton>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <ElEmpty v-else description="还没有收藏任何目的地" :image-size="200">
      <ElButton type="primary" @click="handleExplore"> 去探索 </ElButton>
    </ElEmpty>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import {
  ElInput,
  ElIcon,
  ElButton,
  ElEmpty,
  ElMessage,
  ElMessageBox,
} from "element-plus";
import { Search, Star, Location, Delete } from "@element-plus/icons-vue";
import { useRouter } from "vue-router";

const router = useRouter();
const searchKeyword = ref("");

// 收藏数据（模拟数据）
const favorites = ref([
  {
    id: 1,
    title: "马尔代夫海岛秘境",
    location: "马尔代夫",
    description: "碧海蓝天，水清沙幼，尽享海岛度假时光...",
    price: 8999,
    image:
      "https://images.unsplash.com/photo-1514282401047-d79a71a590e8?w=800&q=80",
  },
  {
    id: 2,
    title: "巴黎艺术文化之都",
    location: "法国巴黎",
    description: "漫步塞纳河畔，领略浪漫之都的艺术魅力...",
    price: 12999,
    image:
      "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?w=800&q=80",
  },
  {
    id: 3,
    title: "瑞士阿尔卑斯山",
    location: "瑞士",
    description: "雪山环绕，湖光山色，体验欧洲屋脊的壮美...",
    price: 15999,
    image:
      "https://images.unsplash.com/photo-1531366936337-7c912a4589a7?w=800&q=80",
  },
  {
    id: 4,
    title: "京都古韵之旅",
    location: "日本京都",
    description: "千年古都，樱花烂漫，体验日式传统文化...",
    price: 6999,
    image:
      "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=800&q=80",
  },
  {
    id: 5,
    title: "巴厘岛热带风情",
    location: "印度尼西亚",
    description: "海滩、寺庙、梯田，感受热带岛屿的多元魅力...",
    price: 5999,
    image:
      "https://images.unsplash.com/photo-1537996194471-e657df975ab4?w=800&q=80",
  },
  {
    id: 6,
    title: "新西兰南岛自然奇观",
    location: "新西兰",
    description: "冰川、峡湾、星空，探索纯净之地的自然美景...",
    price: 18999,
    image:
      "https://images.unsplash.com/photo-1507699622108-4be3abd695ad?w=800&q=80",
  },
]);

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

// 查看详情
const handleView = (item: any) => {
  ElMessage.info(`查看 ${item.title} 详情`);
  // TODO: 跳转到详情页
};

// 移除收藏
const handleRemove = async (id: number) => {
  try {
    await ElMessageBox.confirm("确定要取消收藏吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const index = favorites.value.findIndex((item) => item.id === id);
    if (index > -1) {
      favorites.value.splice(index, 1);
      ElMessage.success("已取消收藏");
    }
  } catch {
    // 用户取消操作
  }
};

// 去探索
const handleExplore = () => {
  router.push("/explore");
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

/* 收藏网格 */
.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
}

.favorite-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.favorite-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.favorite-image {
  height: 180px;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.favorite-card:hover .favorite-image img {
  transform: scale(1.05);
}

.favorite-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  background: rgba(255, 215, 0, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.favorite-badge :deep(.el-icon) {
  font-size: 18px;
  color: #8b6914;
}

.favorite-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.favorite-title {
  font-size: 20px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
  line-height: 1.3;
}

.favorite-location {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #95a5a6;
  font-size: 15px;
  margin-bottom: 10px;
}

.favorite-location :deep(.el-icon) {
  font-size: 16px;
}

.favorite-description {
  color: #7f8c8d;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 15px;
  flex: 1;
}

.favorite-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
  margin-top: auto;
}

.favorite-price {
  display: flex;
  align-items: baseline;
  gap: 5px;
}

.price-label {
  font-size: 14px;
  color: #3498db;
  font-weight: 600;
}

.price-value {
  font-size: 20px;
  font-weight: 700;
  color: #3498db;
}

.favorite-actions {
  display: flex;
  gap: 10px;
}

:deep(.el-button) {
  font-size: 14px;
  padding: 8px 15px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-button--small) {
  padding: 8px 15px;
}

:deep(.el-input__wrapper) {
  font-size: 16px;
  padding: 12px 15px;
  border-radius: 8px;
}

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

  .favorite-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .favorite-actions {
    width: 100%;
  }

  .favorite-actions :deep(.el-button) {
    flex: 1;
  }
}
</style>
