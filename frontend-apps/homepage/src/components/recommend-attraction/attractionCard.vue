<template>
  <div class="destination-card">
    <div class="card-image">
      <img :src="attraction.image" :alt="attraction.title" />
      <div v-if="attraction.badge" class="card-badge">
        {{ attraction.badge }}
      </div>
      <div class="card-favorite" @click="toggleFavorite">
        <heart
          :fill="isFavorite ? '#e74c3c' : 'none'"
          :stroke="isFavorite ? 'none' : '#3498db'"
          width="24"
          height="24"
        />
      </div>
    </div>
    <div class="card-content">
      <h3 class="card-title">{{ attraction.title }}</h3>
      <div class="card-location">
        <i class="fas fa-map-marker-alt"></i>
        {{ attraction.location }}
      </div>
      <p class="card-description">
        {{ attraction.description }}
      </p>
      <div class="card-footer">
        <div class="card-price">¥{{ formatPrice(attraction.price) }}</div>
        <div class="card-rating">
          <i class="fas fa-star"></i>
          <span>{{ attraction.rating }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import heart from "@/assets/svgs/heart.vue";
import {
  collectAttraction,
  uncollectAttraction,
  checkCollectionStatus,
} from "@/apis/attraction";

interface Attraction {
  id: number | string;
  title: string;
  location: string;
  description: string;
  image: string;
  price: number;
  rating: number;
  badge?: string;
}

interface Props {
  attraction: Attraction;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  favorite: [id: number | string, isFavorite: boolean];
}>();

const isFavorite = ref(false);

onMounted(async () => {
  try {
    const id = Number(props.attraction.id);
    if (!Number.isFinite(id)) return;
    const res = await checkCollectionStatus(id);
    if (res && res.code === 200 && res.data) {
      isFavorite.value = !!res.data.collected;
    }
  } catch (e) {
    // 忽略初始化失败，不打断渲染
  }
});

const toggleFavorite = async () => {
  const id = Number(props.attraction.id);
  if (!Number.isFinite(id)) return;
  try {
    if (isFavorite.value) {
      const res = await uncollectAttraction(id);
      if (res.code === 200) {
        isFavorite.value = false;
        emit("favorite", props.attraction.id, isFavorite.value);
        ElMessage.success(res.message || "已取消收藏");
      } else {
        ElMessage.error(res.message || "取消收藏失败");
      }
    } else {
      const res = await collectAttraction(id);
      if (res.code === 200) {
        isFavorite.value = true;
        emit("favorite", props.attraction.id, isFavorite.value);
        ElMessage.success(res.message || "收藏成功");
      } else {
        ElMessage.error(res.message || "收藏失败");
      }
    }
  } catch (error: any) {
    // 全局拦截器会处理401等，这里兜底提示
    ElMessage.error(error?.message || "操作失败");
  }
};

const formatPrice = (price: number): string => {
  return price.toLocaleString("zh-CN");
};
</script>

<style scoped>
/* 目的地卡片 */
.destination-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.destination-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 30px rgba(0, 0, 0, 0.15);
}

.card-image {
  height: 13.75rem;
  width: 100%;
  position: relative;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.destination-card:hover .card-image img {
  transform: scale(1.05);
}

.card-badge {
  position: absolute;
  top: 0.9375rem;
  right: 0.9375rem;
  background: #e74c3c;
  color: white;
  padding: 0.3125rem 0.9375rem;
  border-radius: 50px;
  font-size: 1.2rem;
  font-weight: 600;
  z-index: 2;
}

.card-favorite {
  position: absolute;
  top: 0.9375rem;
  left: 0.9375rem;
  width: 2.1875rem;
  height: 2.1875rem;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  transition: all 0.3s ease;
}

.card-favorite:hover {
  transform: scale(1.1);
}

.card-favorite i {
  color: #3498db;
  font-size: 1.125rem;
  transition: all 0.3s ease;
}

.card-favorite i.fas {
  color: #e74c3c;
}

.card-favorite:hover i {
  transform: scale(1.2);
}

.card-content {
  padding: 1.5625rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 1.375rem;
  margin-top: 0px;
  margin-bottom: 0.625rem;
  font-weight: 700;
  line-height: 1.2;
  color: #2c3e50;
}

.card-location {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #95a5a6;
  margin-bottom: 0.625rem;
  font-size: 1rem;
}

.card-location i {
  color: #3498db;
}

.card-description {
  margin-top: 0;
  margin-bottom: 1rem;
  flex: 1;
  color: #555;
  font-size: 1rem;
  line-height: 1.6;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 0.9375rem;
  border-top: 1px solid #eee;
}

.card-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: #3498db;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 0.3125rem;
  background: #fcf2e9;
  padding: 0.3125rem 0.75rem;
  border-radius: 50px;
  font-size: 0.875rem;
}

.card-rating i {
  color: #ff9500;
  font-size: 1rem;
}

.card-rating span {
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-image {
    height: 20rem;
  }

  .card-content {
    padding: 2rem;
  }

  .card-title {
    font-size: 1.25rem;
  }

  .card-price {
    font-size: 1.25rem;
  }
}

@media (max-width: 576px) {
  .card-image {
    height: 18rem;
  }

  .card-content {
    padding: 1.5rem;
  }

  .card-title {
    font-size: 1.125rem;
  }
}
</style>
