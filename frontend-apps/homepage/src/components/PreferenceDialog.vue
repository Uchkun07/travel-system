<template>
  <ElDialog
    v-model="visible"
    title="完善您的旅行偏好"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <div class="preference-dialog">
      <p class="dialog-tip">
        <ElIcon><InfoFilled /></ElIcon>
        告诉我们您的旅行偏好，我们将为您推荐更合适的目的地！
      </p>

      <ElForm
        ref="preferenceFormRef"
        :model="preferenceForm"
        label-width="120px"
        label-position="left"
      >
        <ElFormItem label="偏好景点类型">
          <ElSelect
            v-model="preferenceForm.preferAttractionTypeId"
            placeholder="选择您喜欢的景点类型"
            clearable
            style="width: 100%"
          >
            <ElOption
              v-for="type in attractionTypeStore.attractionTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </ElSelect>
        </ElFormItem>

        <ElFormItem label="预算范围">
          <div class="budget-range">
            <ElInputNumber
              v-model="preferenceForm.budgetFloor"
              :min="0"
              :max="100000"
              :step="100"
              placeholder="最低预算"
              controls-position="right"
              style="width: 48%"
            />
            <span class="budget-separator">至</span>
            <ElInputNumber
              v-model="preferenceForm.budgetRange"
              :min="0"
              :max="100000"
              :step="100"
              placeholder="最高预算"
              controls-position="right"
              style="width: 48%"
            />
          </div>
          <div class="budget-hint">单位：元/人</div>
        </ElFormItem>

        <ElFormItem label="出行人群">
          <ElRadioGroup v-model="preferenceForm.travelCrowd">
            <ElRadio value="独自出行">独自出行</ElRadio>
            <ElRadio value="情侣">情侣</ElRadio>
            <ElRadio value="亲子">亲子</ElRadio>
            <ElRadio value="朋友">朋友</ElRadio>
            <ElRadio value="家庭">家庭</ElRadio>
          </ElRadioGroup>
        </ElFormItem>

        <ElFormItem label="偏好出行季节">
          <ElCheckboxGroup v-model="selectedSeasons">
            <ElCheckbox value="春">春季</ElCheckbox>
            <ElCheckbox value="夏">夏季</ElCheckbox>
            <ElCheckbox value="秋">秋季</ElCheckbox>
            <ElCheckbox value="冬">冬季</ElCheckbox>
          </ElCheckboxGroup>
        </ElFormItem>
      </ElForm>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="handleSkip">稍后设置</ElButton>
        <ElButton type="primary" :loading="loading" @click="handleSubmit">
          保存偏好
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import {
  ElDialog,
  ElForm,
  ElFormItem,
  ElSelect,
  ElOption,
  ElInputNumber,
  ElRadioGroup,
  ElRadio,
  ElCheckboxGroup,
  ElCheckbox,
  ElButton,
  ElMessage,
  ElIcon,
} from "element-plus";
import { InfoFilled } from "@element-plus/icons-vue";
import {
  updateUserPreference,
  type UserPreferenceRequest,
} from "@/apis/userPreference";
import { useAttractionTypeStore } from "@/stores/attractionType";

const visible = defineModel<boolean>("visible", { default: false });
const emit = defineEmits<{
  close: [];
  saved: [];
}>();

const preferenceFormRef = ref();
const loading = ref(false);
const selectedSeasons = ref<string[]>([]);

// 使用 store
const attractionTypeStore = useAttractionTypeStore();

const preferenceForm = ref<UserPreferenceRequest>({
  preferAttractionTypeId: undefined,
  budgetFloor: undefined,
  budgetRange: undefined,
  travelCrowd: undefined,
  preferSeason: undefined,
});

// 将选中的季节数组转换为逗号分隔的字符串
const seasonsString = computed(() => {
  return selectedSeasons.value.join(",");
});

onMounted(() => {
  // 使用 store 加载景点类型（带缓存）
  attractionTypeStore.fetchAttractionTypes().catch((error) => {
    console.error("加载景点类型失败:", error);
    ElMessage.error("加载景点类型失败，请刷新重试");
  });
});

// 提交偏好设置
const handleSubmit = async () => {
  loading.value = true;
  try {
    // 设置季节字符串
    preferenceForm.value.preferSeason = seasonsString.value || undefined;

    const response = await updateUserPreference(preferenceForm.value);
    if (response.code === 200) {
      ElMessage.success("偏好设置保存成功");
      emit("saved");
      handleClose();
    } else {
      ElMessage.error(response.message || "保存失败");
    }
  } catch (error: any) {
    ElMessage.error(error?.message || "保存失败，请稍后重试");
    console.error("保存偏好失败:", error);
  } finally {
    loading.value = false;
  }
};

// 跳过设置
const handleSkip = () => {
  handleClose();
};

// 关闭对话框
const handleClose = () => {
  visible.value = false;
  // 重置表单
  preferenceForm.value = {
    preferAttractionTypeId: undefined,
    budgetFloor: undefined,
    budgetRange: undefined,
    travelCrowd: undefined,
    preferSeason: undefined,
  };
  selectedSeasons.value = [];
  emit("close");
};
</script>

<style scoped>
.preference-dialog {
  padding: 20px 0;
}

.dialog-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #e8f4ff;
  border-radius: 8px;
  color: #2d88ff;
  font-size: 14px;
  margin-bottom: 24px;
}

.dialog-tip :deep(.el-icon) {
  font-size: 18px;
}

.budget-range {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.budget-separator {
  color: #909399;
  font-size: 14px;
}

.budget-hint {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #2c3e50;
}

:deep(.el-radio) {
  margin-right: 16px;
}

:deep(.el-checkbox) {
  margin-right: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 响应式 */
@media (max-width: 768px) {
  .budget-range {
    flex-direction: column;
    gap: 8px;
  }

  .budget-range :deep(.el-input-number) {
    width: 100% !important;
  }

  .budget-separator {
    display: none;
  }
}
</style>
