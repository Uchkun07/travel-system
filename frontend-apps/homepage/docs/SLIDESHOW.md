# 轮播图组件使用说明

## 组件路径

`src/components/recommend-attraction/slideshow.vue`

## 功能特点

### ✨ 核心功能

- ✅ 自动轮播(5 秒间隔)
- ✅ 淡入淡出切换效果
- ✅ 左右导航按钮
- ✅ 底部分页指示器
- ✅ 点击跳转到景点详情
- ✅ 自动记录点击统计
- ✅ 响应式设计(手机/平板/桌面)
- ✅ 骨架屏加载状态
- ✅ 空状态提示

### 🎨 视觉效果

- 图片悬停放大效果
- 渐变遮罩层
- 文字淡入动画
- 按钮悬停效果
- 圆角阴影设计

## 使用方法

### 1. 在页面中引入组件

```vue
<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <div class="slideshow-section">
      <Slideshow />
    </div>

    <!-- 其他内容 -->
    <div class="content">
      <!-- ... -->
    </div>
  </div>
</template>

<script setup lang="ts">
import Slideshow from "@/components/recommend-attraction/slideshow.vue";
</script>

<style scoped>
.slideshow-section {
  margin-bottom: 40px;
}
</style>
```

### 2. 在路由页面中使用

```typescript
// src/views/home/Home.vue
<template>
  <div class="home-container">
    <!-- 轮播图 -->
    <section class="hero-section">
      <div class="container">
        <Slideshow />
      </div>
    </section>

    <!-- 推荐景点 -->
    <section class="attractions-section">
      <!-- ... -->
    </section>
  </div>
</template>

<script setup lang="ts">
import Slideshow from '@/components/recommend-attraction/slideshow.vue'
</script>

<style scoped>
.hero-section {
  padding: 40px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>
```

## 组件配置

### Props (无需配置)

组件会自动从后端获取数据,无需传递任何 props。

### API 依赖

组件依赖以下 API 接口:

- `GET /slideshow/active` - 获取启用的轮播图
- `POST /slideshow/click/{id}` - 记录点击统计

## 数据格式

### Slideshow 接口

```typescript
interface Slideshow {
  slideshowId: number; // 轮播图ID
  title: string; // 标题
  subtitle?: string; // 副标题
  imageUrl: string; // 图片URL
  attractionId?: number; // 关联景点ID
  displayOrder: number; // 显示顺序
  status: number; // 状态(0=禁用,1=启用)
  startTime?: string; // 开始时间
  endTime?: string; // 结束时间
  clickCount: number; // 点击次数
  createTime: string; // 创建时间
  updateTime: string; // 更新时间
}
```

## Swiper 配置

### 当前配置

```javascript
{
  slidesPerView: 1,           // 每页显示1张
  spaceBetwee: 0,            // 间距0
  loop: true,                 // 循环播放
  autoplay: {
    delay: 5000,              // 5秒自动切换
    disableOnInteraction: false
  },
  pagination: {
    clickable: true,          // 可点击分页器
    dynamicBullets: true      // 动态分页点
  },
  navigation: true,           // 显示导航按钮
  effect: 'fade',             // 淡入淡出效果
  fadeEffect: {
    crossFade: true
  }
}
```

### 自定义配置

如需修改配置,可以编辑组件中的 Swiper 属性:

```vue
<swiper
  :slides-per-view="1"
  :autoplay="{ delay: 3000 }"  <!-- 改为3秒 -->
  :effect="'slide'"            <!-- 改为滑动效果 -->
  <!-- 其他配置 -->
>
```

## 样式定制

### 修改尺寸

```scss
.slideshow-container {
  height: 600px; // 修改高度
  border-radius: 16px; // 修改圆角
}
```

### 修改按钮样式

```scss
:deep(.swiper-button-prev),
:deep(.swiper-button-next) {
  background: rgba(255, 255, 255, 0.9); // 白色背景
  color: #333; // 深色图标
}
```

### 修改分页器样式

```scss
:deep(.swiper-pagination-bullet) {
  background: #409eff; // 主题色
  width: 12px;
  height: 12px;
}
```

## 响应式断点

| 屏幕尺寸      | 高度  | 标题大小 | 副标题大小 |
| ------------- | ----- | -------- | ---------- |
| 桌面 (>768px) | 500px | 48px     | 20px       |
| 平板 (≤768px) | 400px | 32px     | 16px       |
| 手机 (≤480px) | 300px | 24px     | 14px       |

## 注意事项

1. **图片尺寸**: 建议使用 1920x500 或更大尺寸的图片
2. **图片格式**: 支持 JPG、PNG、WebP 等格式
3. **加载性能**: 图片会自动适配容器大小
4. **点击行为**: 点击轮播图会跳转到关联的景点详情页
5. **路由配置**: 确保 `/attraction/:id` 路由已配置

## 依赖包

组件依赖以下 npm 包:

```json
{
  "swiper": "^11.x.x",
  "vue": "^3.x.x",
  "vue-router": "^4.x.x",
  "element-plus": "^2.x.x",
  "@element-plus/icons-vue": "^2.x.x"
}
```

## 故障排查

### 轮播图不显示

1. 检查后端 API 是否正常返回数据
2. 检查图片 URL 是否可访问
3. 检查控制台是否有错误信息

### 点击无法跳转

1. 确认轮播图数据中有 `attractionId` 字段
2. 检查路由配置是否正确
3. 查看控制台是否有路由错误

### 样式异常

1. 确认 Swiper CSS 已正确导入
2. 检查是否有全局样式冲突
3. 清除浏览器缓存重新加载

## 进阶定制

### 添加更多切换效果

```vue
<!-- Cube 效果 -->
<swiper :effect="'cube'" :cube-effect="{ shadow: true }">

<!-- Flip 效果 -->
<swiper :effect="'flip'">

<!-- Coverflow 效果 -->
<swiper :effect="'coverflow'" :coverflow-effect="{ depth: 100 }">
```

### 添加视频支持

```vue
<div class="slide-image">
  <video v-if="item.videoUrl" autoplay muted loop>
    <source :src="item.videoUrl" type="video/mp4">
  </video>
  <img v-else :src="item.imageUrl" :alt="item.title">
</div>
```

### 添加进度条

```vue
<swiper
  :modules="[Navigation, Pagination, Autoplay, EffectFade, Scrollbar]"
  :scrollbar="{ draggable: true }"
>
```

## 示例截图说明

组件包含以下视觉元素:

- 🖼️ 大图背景
- 📝 居中标题和副标题
- 🔘 圆形导航按钮(左右)
- 🔵 底部分页指示器
- 🎯 "探索更多"按钮
- 🌟 悬停动画效果
