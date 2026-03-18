<template>
  <div class="routeline-page">
    <div class="content-wrapper">
      <!-- 左侧：规划表单 -->
      <div class="form-section">
        <h2 class="section-title">基本信息</h2>
        <ElForm
          :model="planForm"
          label-width="100px"
          label-position="top"
          class="plan-form"
        >
          <ElFormItem label="出发地" required>
            <ElInput
              v-model="planForm.departure"
              placeholder="请输入出发地"
              clearable
            />
          </ElFormItem>

          <ElFormItem label="预算(元)" required>
            <ElInputNumber
              v-model="planForm.budget"
              :min="0"
              :step="100"
              placeholder="请输入预算"
              controls-position="right"
              style="width: 100%"
            />
          </ElFormItem>

          <ElFormItem label="出发日期" required>
            <ElDatePicker
              v-model="planForm.departureDate"
              type="date"
              placeholder="选择出发日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </ElFormItem>

          <ElFormItem label="出行方式" required>
            <ElRadioGroup v-model="planForm.travelMode">
              <ElRadioButton value="自驾">
                <i class="fa-solid fa-car"></i> 自驾
              </ElRadioButton>
              <ElRadioButton value="火车">
                <i class="fa-solid fa-train"></i> 火车
              </ElRadioButton>
              <ElRadioButton value="飞机">
                <i class="fa-solid fa-plane"></i> 飞机
              </ElRadioButton>
            </ElRadioGroup>
          </ElFormItem>

          <ElFormItem label="出行人群" required>
            <ElRadioGroup v-model="planForm.travelGroup">
              <ElRadioButton value="独自">独自</ElRadioButton>
              <ElRadioButton value="亲子">亲子</ElRadioButton>
              <ElRadioButton value="家庭">家庭</ElRadioButton>
              <ElRadioButton value="朋友">朋友</ElRadioButton>
            </ElRadioGroup>
          </ElFormItem>

          <ElFormItem label="出行偏好" required>
            <ElRadioGroup v-model="planForm.travelPreference">
              <ElRadioButton value="经济">经济</ElRadioButton>
              <ElRadioButton value="适中">适中</ElRadioButton>
              <ElRadioButton value="舒适">舒适</ElRadioButton>
            </ElRadioGroup>
          </ElFormItem>

          <ElFormItem>
            <ElButton
              type="primary"
              size="large"
              :disabled="!canPlan"
              :loading="planning"
              @click="handlePlan"
              style="width: 100%"
            >
              <ElIcon v-if="!planning"><Guide /></ElIcon>
              {{ planning ? "规划中..." : "开始规划路线" }}
            </ElButton>
          </ElFormItem>
        </ElForm>
      </div>

      <!-- 右侧：收藏列表 -->
      <div class="collection-section">
        <div class="section-header">
          <h2 class="section-title">我的收藏景点</h2>
          <div class="header-right">
            <span class="selected-count"
              >已选择 {{ selectedAttractions.length }} 个景点</span
            >
            <ElButton
              v-if="collectionList.length > 0"
              type="primary"
              @click="toggleSelectAll"
              class="select-all-btn"
            >
              {{ isAllSelected ? "取消全选" : "全选" }}
            </ElButton>
          </div>
        </div>

        <div v-if="loading" class="loading-wrapper">
          <ElIcon class="is-loading" :size="40"><Loading /></ElIcon>
          <p>加载中...</p>
        </div>

        <div v-else-if="collectionList.length === 0" class="empty-state">
          <ElEmpty description="您还没有收藏任何景点">
            <ElButton type="primary" @click="goToExplore">去探索景点</ElButton>
          </ElEmpty>
        </div>

        <div v-else class="collection-list">
          <div
            v-for="attraction in collectionList"
            :key="attraction.attractionId"
            class="collection-item"
            :class="{ selected: isSelected(attraction.attractionId) }"
            @click="toggleSelect(attraction.attractionId)"
          >
            <div class="item-checkbox">
              <ElCheckbox :model-value="isSelected(attraction.attractionId)" />
            </div>
            <div class="item-image">
              <img
                :src="getImageUrl(attraction.imageUrl)"
                :alt="attraction.name"
              />
            </div>
            <div class="item-info">
              <h3 class="item-name">{{ attraction.name }}</h3>
              <p class="item-location">
                <ElIcon><Location /></ElIcon>
                {{ attraction.location || "未知地点" }}
              </p>
              <p class="item-description">{{ attraction.description }}</p>
              <div class="item-meta">
                <span class="item-type">{{ attraction.type }}</span>
                <span class="item-price" v-if="attraction.ticketPrice">
                  ¥{{ attraction.ticketPrice }}
                </span>
                <span class="item-rating" v-if="attraction.averageRating">
                  <ElIcon><Star /></ElIcon>
                  {{ attraction.averageRating.toFixed(1) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElDatePicker,
  ElRadioGroup,
  ElRadio,
  ElButton,
  ElCheckbox,
  ElIcon,
  ElEmpty,
  ElMessage,
} from "element-plus";
import {
  Van,
  Position,
  MostlyCloudy,
  Guide,
  Loading,
  Location,
  Star,
} from "@element-plus/icons-vue";
import {
  getCollectedAttractionIds,
  getAttractionsByIds,
  type AttractionCard,
} from "@/apis/attraction";
import { planRoute } from "@/apis/routePlan";

const router = useRouter();
const baseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// 表单数据
const planForm = ref({
  departure: "",
  budget: undefined as number | undefined,
  departureDate: "",
  travelMode: "自驾",
  travelGroup: "独自",
  travelPreference: "适中",
});

// 收藏列表
const collectionList = ref<AttractionCard[]>([]);
const selectedAttractions = ref<number[]>([]);
const loading = ref(false);
const planning = ref(false);

// 是否可以规划
const canPlan = computed(() => {
  return (
    planForm.value.departure.trim() !== "" &&
    planForm.value.budget !== undefined &&
    planForm.value.budget > 0 &&
    planForm.value.departureDate !== "" &&
    selectedAttractions.value.length > 0
  );
});

// 获取收藏列表
const fetchCollectionList = async () => {
  loading.value = true;
  try {
    // 获取收藏的景点ID列表
    const idsResponse = await getCollectedAttractionIds();
    if (
      idsResponse.code === 200 &&
      idsResponse.data &&
      idsResponse.data.length > 0
    ) {
      // 根据ID列表获取景点详情
      const attractionsResponse = await getAttractionsByIds(idsResponse.data);
      if (attractionsResponse.code === 200 && attractionsResponse.data) {
        collectionList.value = attractionsResponse.data;
      }
    } else {
      collectionList.value = [];
    }
  } catch (error) {
    console.error("获取收藏列表失败:", error);
    ElMessage.error("获取收藏列表失败");
    collectionList.value = [];
  } finally {
    loading.value = false;
  }
};

// 切换选择状态
const toggleSelect = (attractionId: number) => {
  const index = selectedAttractions.value.indexOf(attractionId);
  if (index > -1) {
    selectedAttractions.value.splice(index, 1);
  } else {
    selectedAttractions.value.push(attractionId);
  }
};

// 检查是否已选择
const isSelected = (attractionId: number) => {
  return selectedAttractions.value.includes(attractionId);
};

// 是否全选
const isAllSelected = computed(() => {
  return (
    collectionList.value.length > 0 &&
    selectedAttractions.value.length === collectionList.value.length
  );
});

// 切换全选状态
const toggleSelectAll = () => {
  if (isAllSelected.value) {
    // 取消全选
    selectedAttractions.value = [];
  } else {
    // 全选
    selectedAttractions.value = collectionList.value.map(
      (attraction) => attraction.attractionId,
    );
  }
};

// 获取图片URL
const getImageUrl = (imageUrl: string) => {
  if (!imageUrl) return "";
  return imageUrl.startsWith("http") ? imageUrl : `${baseUrl}${imageUrl}`;
};

// 开始规划路线
const handlePlan = async () => {
  if (!canPlan.value) {
    ElMessage.warning("请完整填写所有必填项并至少选择一个景点");
    return;
  }

  planning.value = true;
  try {
    const res = await planRoute({
      departure: planForm.value.departure,
      budget: planForm.value.budget!,
      departureDate: planForm.value.departureDate,
      travelMode: planForm.value.travelMode,
      travelGroup: planForm.value.travelGroup,
      travelPreference: planForm.value.travelPreference,
      attractionIds: selectedAttractions.value,
    });

    if (res.code !== 200 || !res.data) {
      ElMessage.error(res.message || "路线规划失败，请稍后重试");
      return;
    }

    // 将结果通过路由 state 传递给结果页（避免 URL 过长）
    router.push({
      name: "RouteResult",
      state: { result: JSON.stringify(res.data) },
    });
  } catch (e) {
    console.error("路线规划请求失败", e);
    ElMessage.error("网络异常，请稍后重试");
  } finally {
    planning.value = false;
  }
};

// 跳转到探索页面
const goToExplore = () => {
  router.push("/explore");
};

onMounted(() => {
  fetchCollectionList();
});
</script>

<style scoped lang="scss">
.routeline-page {
  padding: 40px 18rem;
  min-height: calc(100vh - 75px);
  background: #f8f9fa;

  .content-wrapper {
    display: flex;
    gap: 32px;
    align-items: flex-start;
  }

  .form-section {
    flex: 0 0 400px;
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    position: sticky;
    top: 20px;
    height: 586px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;

    .section-title {
      font-size: 1.5rem;
      font-weight: 600;
      color: #202124;
      margin: 0 0 20px 0;
    }

    .plan-form {
      :deep(.el-form-item) {
        margin: 0 0 11px;
      }
      :deep(.el-form-item__label) {
        font-weight: 500;
        color: #5f6368;
      }

      :deep(.el-radio) {
        margin-right: 20px;
        margin-bottom: 12px;

        .el-icon {
          margin-right: 4px;
        }
      }

      :deep(.el-radio-group) {
        display: flex;
        width: 100%;
        justify-content: space-between;
        gap: 8px;
      }

      :deep(.el-radio-button__inner) {
        border: 1px solid #e5e7eb;
        border-radius: 1rem;
      }

      :deep(.el-button--primary) {
        background: #1a73e8;
        border-color: #1a73e8;
        height: 48px;
        font-size: 16px;
        font-weight: 600;

        &:hover {
          background: #1557b0;
        }
      }
    }
  }

  .collection-section {
    flex: 1;
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    height: fit-content;
    display: flex;
    flex-direction: column;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      flex-shrink: 0;

      .section-title {
        font-size: 1.5rem;
        font-weight: 600;
        color: #202124;
        margin: 0;
      }

      .header-right {
        display: flex;
        align-items: center;
        gap: 16px;

        .selected-count {
          color: #1a73e8;
          font-weight: 500;
          font-size: 0.95rem;
        }

        .select-all-btn {
          color: #ffffff;
          padding: 8px 16px;
          font-size: 1rem;
          background: #1a73e8;
          border-color: #1a73e8;
          border-radius: 20px;

          &:hover {
            background: #1557b0;
            border-color: #1557b0;
          }
        }
      }
    }

    .loading-wrapper {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 60px 20px;
      color: #5f6368;
      flex: 1;

      .el-icon {
        margin-bottom: 12px;
        color: #1a73e8;
      }
    }

    .empty-state {
      padding: 40px 20px;
      flex: 1;
    }

    .collection-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      overflow-y: auto;
      height: 487px;
      padding-right: 8px;

      /* 自定义滚动条样式 */
      &::-webkit-scrollbar {
        width: 8px;
      }

      &::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 4px;
      }

      &::-webkit-scrollbar-thumb {
        background: #c1c1c1;
        border-radius: 4px;

        &:hover {
          background: #a8a8a8;
        }
      }

      .collection-item {
        width: 400px;
        display: flex;
        gap: 16px;
        padding: 16px;
        box-sizing: border-box;
        border: 2px solid #e8eaed;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          border-color: #1a73e8;
          box-shadow: 0 2px 8px rgba(26, 115, 232, 0.1);
        }

        &.selected {
          border-color: #1a73e8;
          background: #f1f7ff;
        }

        .item-checkbox {
          display: flex;
          align-items: center;
        }

        .item-image {
          flex: 0 0 120px;
          height: 120px;
          border-radius: 8px;
          overflow: hidden;

          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }

        .item-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: space-between;

          .item-name {
            font-size: 1.1rem;
            font-weight: 600;
            color: #202124;
            margin: 0 0 8px 0;
          }

          .item-location {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 0.9rem;
            color: #5f6368;
            margin: 0 0 8px 0;

            .el-icon {
              color: #1a73e8;
            }
          }

          .item-description {
            font-size: 0.9rem;
            color: #5f6368;
            margin: 0 0 12px 0;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .item-meta {
            display: flex;
            gap: 16px;
            align-items: center;

            .item-type {
              padding: 4px 12px;
              background: #e8f0fe;
              color: #1a73e8;
              border-radius: 12px;
              font-size: 0.85rem;
              font-weight: 500;
            }

            .item-price {
              color: #ea4335;
              font-weight: 600;
              font-size: 0.95rem;
            }

            .item-rating {
              display: flex;
              align-items: center;
              gap: 4px;
              color: #f9ab00;
              font-weight: 500;

              .el-icon {
                font-size: 1rem;
              }
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 1400px) {
  .routeline-page {
    padding: 40px 4rem;
  }
}

@media (max-width: 1024px) {
  .routeline-page {
    .content-wrapper {
      flex-direction: column;
    }

    .form-section {
      flex: 1;
      width: 100%;
      position: static;
    }
  }
}
</style>
