<template>
  <div class="slideshow-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 轮播图 -->
    <swiper
      v-else-if="slideshows.length > 0"
      :modules="modules"
      :slides-per-view="1"
      :space-between="0"
      :loop="true"
      :autoplay="{
        delay: 5000,
        disableOnInteraction: false,
      }"
      :pagination="{
        clickable: true,
        dynamicBullets: true,
      }"
      :navigation="false"
      :effect="'fade'"
      :fadeEffect="{
        crossFade: true,
      }"
      class="slideshow-swiper"
      @slide-change="onSlideChange"
    >
      <swiper-slide v-for="item in slideshows" :key="item.slideshowId">
        <div class="slide-item" @click="handleSlideClick(item)">
          <!-- 图片 -->
          <div class="slide-image">
            <img :src="item.imageUrl" :alt="item.title" />
          </div>

          <!-- 内容覆盖层 -->
          <div class="slide-overlay">
            <div class="slide-content">
              <h2 class="slide-title">{{ item.title }}</h2>
              <p v-if="item.subtitle" class="slide-subtitle">
                {{ item.subtitle }}
              </p>
              <el-button
                type="primary"
                @click="handleSlideClick(item)"
                size="large"
                round
                class="explore-btn"
              >
                <el-icon class="mr-1"><ArrowRight /></el-icon>
                探索更多
              </el-button>
            </div>
          </div>
        </div>
      </swiper-slide>
    </swiper>

    <!-- 空状态 -->
    <el-empty v-else description="暂无轮播图" :image-size="200" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { Swiper, SwiperSlide } from "swiper/vue";
import { Navigation, Pagination, Autoplay, EffectFade } from "swiper/modules";
import { ElMessage, ElSkeleton, ElEmpty, ElButton, ElIcon } from "element-plus";
import { ArrowRight } from "@element-plus/icons-vue";
import {
  getActiveSlideshow,
  recordClick,
  type Slideshow,
} from "@/apis/slideshow";
import { useRouter } from "vue-router";

// 导入 Swiper 样式
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";
import "swiper/css/effect-fade";

// Swiper 模块
const modules = [Navigation, Pagination, Autoplay, EffectFade];

// 路由
const router = useRouter();

// 状态
const loading = ref(true);
const slideshows = ref<Slideshow[]>([]);
const currentSlide = ref(0);

/**
 * 加载轮播图数据
 */
const loadSlideshow = async () => {
  try {
    loading.value = true;
    const response = await getActiveSlideshow();

    if (response.code === 200 && response.data) {
      slideshows.value = response.data;
    } else {
      ElMessage.warning(response.message || "获取轮播图失败");
    }
  } catch (error: any) {
    console.error("加载轮播图失败:", error);
    ElMessage.error(error.message || "加载轮播图失败");
  } finally {
    loading.value = false;
  }
};

/**
 * 轮播图切换事件
 */
const onSlideChange = (swiper: any) => {
  currentSlide.value = swiper.realIndex;
};

/**
 * 点击轮播图
 */
const handleSlideClick = async (item: Slideshow) => {
  try {
    // 记录点击
    await recordClick(item.slideshowId);

    // 如果关联了景点,跳转到景点详情页
    if (item.attractionId) {
      router.push(`/attraction/${item.attractionId}`);
    }
  } catch (error) {
    console.error("记录点击失败:", error);
  }
};

// 组件挂载时加载数据
onMounted(() => {
  loadSlideshow();
});
</script>

<style scoped lang="scss">
.slideshow-container {
  width: 100%;
  height: 600px;
  position: relative;
  overflow: hidden;
  margin-left: calc(-50vw + 50%);

  .loading-container {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
  }
}

.slideshow-swiper {
  width: 100%;
  height: 100%;

  :deep(.swiper-pagination) {
    top: 20px;
  }

  :deep(.swiper-pagination-bullet) {
    width: 10px;
    height: 10px;
    background: #fff;
    opacity: 0.5;
    transition: all 0.3s;

    &.swiper-pagination-bullet-active {
      opacity: 1;
      width: 30px;
      border-radius: 5px;
    }
  }
}

.slide-item {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
  overflow: hidden;

  &:hover {
    .slide-image img {
      transform: scale(1.05);
    }

    .slide-overlay {
      background: rgba(0, 0, 0, 0.5);
    }
  }
}

.slide-image {
  width: 100%;
  height: 100%;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s ease;
  }
}

.slide-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
}

.slide-content {
  text-align: center;
  color: #fff;
  padding: 20px;
  max-width: 800px;
  animation: fadeInUp 0.8s ease;

  .slide-title {
    font-size: 48px;
    font-weight: bold;
    margin: 0 0 16px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    letter-spacing: 2px;
  }

  .slide-subtitle {
    font-size: 20px;
    margin: 0 0 32px;
    opacity: 0.9;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
    line-height: 1.6;
  }

  .explore-btn {
    padding: 14px 32px;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 1px;
    box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(64, 158, 255, 0.6);
    }

    .mr-1 {
      margin-right: 4px;
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .slideshow-container {
    height: 400px;
    border-radius: 8px;
  }

  .slide-content {
    .slide-title {
      font-size: 32px;
    }

    .slide-subtitle {
      font-size: 16px;
      margin-bottom: 24px;
    }

    .explore-btn {
      padding: 12px 24px;
      font-size: 14px;
    }
  }

  .slideshow-swiper {
    :deep(.swiper-button-prev),
    :deep(.swiper-button-next) {
      width: 36px;
      height: 36px;

      &:after {
        font-size: 16px;
      }
    }
  }
}

@media (max-width: 480px) {
  .slideshow-container {
    height: 300px;
  }

  .slide-content {
    .slide-title {
      font-size: 24px;
      margin-bottom: 12px;
    }

    .slide-subtitle {
      font-size: 14px;
      margin-bottom: 20px;
    }

    .explore-btn {
      padding: 10px 20px;
      font-size: 13px;
    }
  }
}
</style>
