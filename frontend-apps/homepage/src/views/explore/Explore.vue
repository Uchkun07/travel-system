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
          calss="searchInput"
          placeholder="搜索景点或者标签..."
          :prefix-icon="Search"
        >
          <template #suffix>
            <ElButton class="searchButton" type="primary" plain>搜索</ElButton>
          </template>
        </ElInput>
      </div>
      <div class="overlay"></div>
    </header>
    <div class="content">
      <div class="tagsCardFrame">
        <div class="tagsCard">
          <span class="icon"></span>
          <span class="title"></span>
          <span class="total"></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElInput, ElButton } from "element-plus";
import { Search } from "@element-plus/icons-vue";
</script>

<style scoped>
.explore-page {
  min-height: calc(100vh - 75px);

  header {
    position: relative;
    width: 100%;
    height: 85vh;
    .head-image {
      width: 100%;
      height: 85vh;
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
    height: 1000px;
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
        &:hover {
          box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1),
            0 10px 10px -5px rgba(0, 0, 0, 0.04);
        }
      }
    }
  }
}
</style>
