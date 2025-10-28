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
      </div>

      <!-- 景点列表占位 -->
      <div class="attractions-grid">
        <div class="placeholder-text">
          景点列表组件待添加...
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import Slideshow from '@/components/recommend-attraction/slideshow.vue'
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
}

.attractions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.placeholder-text {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 1.1rem;
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

  .attractions-grid {
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
