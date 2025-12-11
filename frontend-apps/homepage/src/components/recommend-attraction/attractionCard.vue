<template>
  <div class="destination-card" @click="handleCardClick">
    <div class="card-image">
      <img :src="attraction.image" :alt="attraction.title" />
      <div v-if="attraction.badge" class="card-badge">
        {{ attraction.badge }}
      </div>
      <div class="card-favorite" @click.stop="toggleFavorite">
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
import { computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useCollectionStore } from "@/stores/collection";
import { useUserStore } from "@/stores/user";
import heart from "@/assets/svgs/heart.vue";

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

const router = useRouter();
const collectionStore = useCollectionStore();
const userStore = useUserStore();

// 计算收藏状态
const isFavorite = computed(() => {
  const id = Number(props.attraction.id);
  return Number.isFinite(id) ? collectionStore.isCollected(id) : false;
});

// 点击卡片跳转到详情页
const handleCardClick = () => {
  const id = Number(props.attraction.id);
  if (Number.isFinite(id)) {
    router.push(`/attraction/${id}`);
  }
};

// 切换收藏
const toggleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录后再收藏");
    return;
  }

  const id = Number(props.attraction.id);
  if (!Number.isFinite(id)) return;

  const success = await collectionStore.toggleCollection(id);
  if (success) {
    emit("favorite", props.attraction.id, collectionStore.isCollected(id));
  }
};

const formatPrice = (price: number): string => {
  return price.toLocaleString("zh-CN");
};
</script>

<!-- 样式保持不变 -->

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
  font-size: 1rem;
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
