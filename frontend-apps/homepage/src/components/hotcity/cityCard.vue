<template>
  <div class="container">
    <div class="image">
      <img :src="cityCard.cityUrl" alt="cityCard.cityName" />
    </div>
    <div class="info">
      <div class="name">
        <h1 class="nameTitle">{{ cityCard.cityName }}</h1>
        <span class="location">{{ cityCard.country }}</span>
      </div>
      <div class="description">{{ cityCard.description }}</div>
      <div class="dataFrame">
        <div class="dataLFrame">
          <span class="data">{{ cityCard.averageTemperature }}℃</span
          ><span class="dataName">平均气温</span>
        </div>
        <div class="dataLFrame">
          <span class="data">{{ cityCard.attractionCount }}个</span
          ><span class="dataName">景点数量</span>
        </div>
        <div class="dataLFrame">
          <span class="data">{{ cityCard.popularity }}</span
          ><span class="dataName">城市热度</span>
        </div>
      </div>
      <ElButton>探索{{ cityCard.cityName }}</ElButton>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ElButton } from "element-plus";
import { type CityCard } from "@/apis/city";
const props = defineProps<{
  cityCard: CityCard;
}>();
</script>
<style lang="scss" scoped>
.container {
  max-width: 442px;
  min-width: 442px;
  height: auto;
  border-radius: 1rem;
  box-shadow: 0px 4px 20px 0px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}
.image {
  width: 100%;
  height: 240px;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
.info {
  width: 100%;
  max-height: 347px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1.5rem;
  box-sizing: border-box;
  .name {
    width: 100%;
    height: 67px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: start;
    .nameTitle {
      font-size: 1.5rem;
      font-weight: 800;
      color: #333333;
      margin: 0;
    }
    .location {
      font-size: 1rem;
      font-weight: 500;
      color: #888888;
    }
  }
  .description {
    color: rgba(95, 99, 104, 255);
    font-family: Noto Sans SC;
    font-size: 1rem;
    font-weight: 400;
    line-height: 24px;
    letter-spacing: 0px;
    text-align: left;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 3; /* 显示三行 */
    line-clamp: 3;
    -webkit-box-orient: vertical;
  }
  .dataFrame {
    width: 100%;
    height: 56px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    .dataLFrame {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      .data {
        color: rgba(26, 115, 232, 255);
        font-family: Noto Sans SC;
        font-size: 1.25rem;
        font-weight: 700;
        line-height: 32px;
        letter-spacing: 0px;
        text-align: center;
      }
      .dataName {
        color: rgba(150, 150, 150, 255);
        font-family: Noto Sans SC;
        font-size: 0.875rem;
        font-weight: 400;
        line-height: 22px;
        letter-spacing: 0px;
        text-align: center;
      }
    }
  }
  .el-button {
    width: 100%;
    height: 48px;
    background: rgba(26, 115, 232, 1);
    border-radius: 24px;
    color: #ffffff;
    font-family: Noto Sans SC;
    font-size: 1rem;
    font-weight: 600;
    line-height: 24px;
    letter-spacing: 0px;
    text-align: center;
  }
}
</style>
