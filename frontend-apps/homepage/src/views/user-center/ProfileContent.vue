<template>
  <div class="profile-content">
    <!-- 页面头部 -->
    <div class="content-header">
      <h2 class="content-title">个人资料</h2>
      <ElButton type="primary" @click="handleEdit">
        <ElIcon><Edit /></ElIcon>
        编辑资料
      </ElButton>
    </div>

    <!-- 数据概览卡片 -->
    <div class="overview-grid">
      <div class="overview-card">
        <div class="overview-icon">
          <ElIcon><Suitcase /></ElIcon>
        </div>
        <div class="overview-number">{{ stats.orders }}</div>
        <div class="overview-label">已完成订单</div>
      </div>

      <div class="overview-card">
        <div class="overview-icon">
          <ElIcon><Star /></ElIcon>
        </div>
        <div class="overview-number">{{ stats.favorites }}</div>
        <div class="overview-label">收藏目的地</div>
      </div>

      <div class="overview-card">
        <div class="overview-icon">
          <ElIcon><Calendar /></ElIcon>
        </div>
        <div class="overview-number">{{ stats.plans }}</div>
        <div class="overview-label">旅行计划</div>
      </div>
    </div>

    <!-- 个人信息表单 -->
    <div class="profile-form">
      <h3 class="form-section-title">基本信息</h3>

      <ElForm :model="profileForm" label-width="120px" label-position="left">
        <ElRow :gutter="20">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="用户名">
              <ElInput v-model="profileForm.username" :disabled="!isEditing" />
            </ElFormItem>
          </ElCol>
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="真实姓名">
              <ElInput v-model="profileForm.realName" :disabled="!isEditing" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElFormItem label="电子邮箱">
          <ElInput v-model="profileForm.email" :disabled="!isEditing" />
        </ElFormItem>

        <ElFormItem label="手机号码">
          <ElInput v-model="profileForm.phone" :disabled="!isEditing" />
        </ElFormItem>

        <ElRow :gutter="20">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="性别">
              <ElSelect
                v-model="profileForm.gender"
                :disabled="!isEditing"
                style="width: 100%"
              >
                <ElOption label="男" value="male" />
                <ElOption label="女" value="female" />
                <ElOption label="保密" value="secret" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="出生日期">
              <ElDatePicker
                v-model="profileForm.birthday"
                type="date"
                :disabled="!isEditing"
                style="width: 100%"
                placeholder="选择日期"
              />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElFormItem label="个人简介">
          <ElInput
            v-model="profileForm.bio"
            type="textarea"
            :rows="4"
            :disabled="!isEditing"
            placeholder="介绍一下你自己..."
          />
        </ElFormItem>

        <ElFormItem v-if="isEditing">
          <ElButton type="primary" @click="handleSave">保存修改</ElButton>
          <ElButton @click="handleCancel">取消</ElButton>
        </ElFormItem>
      </ElForm>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import {
  ElButton,
  ElIcon,
  ElForm,
  ElFormItem,
  ElInput,
  ElSelect,
  ElOption,
  ElDatePicker,
  ElRow,
  ElCol,
  ElMessage,
} from "element-plus";
import { Edit, Suitcase, Star, Calendar } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";

const userStore = useUserStore();
const isEditing = ref(false);

// 统计数据
const stats = reactive({
  orders: 12,
  favorites: 24,
  plans: 8,
});

// 个人资料表单
const profileForm = reactive({
  username: userStore.username || "张明",
  realName: "张明",
  email: userStore.userInfo?.email || "zhangming@example.com",
  phone: "13800138000",
  gender: "male",
  birthday: "1990-05-15",
  bio: "热爱旅行、摄影和美食，足迹遍布五大洲30个国家。",
});

// 备份原始数据
const originalForm = { ...profileForm };

const handleEdit = () => {
  isEditing.value = true;
};

const handleSave = () => {
  // TODO: 调用API保存数据
  ElMessage.success("保存成功");
  isEditing.value = false;
  // 更新备份
  Object.assign(originalForm, profileForm);
};

const handleCancel = () => {
  // 恢复原始数据
  Object.assign(profileForm, originalForm);
  isEditing.value = false;
};
</script>

<style scoped>
.profile-content {
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

/* 概览卡片 */
.overview-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.overview-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(52, 152, 219, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
}

.overview-icon :deep(.el-icon) {
  color: #3498db;
  font-size: 25px;
}

.overview-number {
  font-size: 28px;
  font-weight: 700;
  color: #3498db;
  margin-bottom: 5px;
}

.overview-label {
  font-size: 16px;
  color: #95a5a6;
}

/* 表单区域 */
.profile-form {
  margin-top: 30px;
}

.form-section-title {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ecf0f1;
}

:deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
}

:deep(.el-input__wrapper) {
  font-size: 16px;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #3498db;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
}

:deep(.el-textarea__inner) {
  font-size: 16px;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-family: inherit;
  transition: all 0.3s ease;
}

:deep(.el-textarea__inner:hover) {
  border-color: #3498db;
}

:deep(.el-textarea__inner:focus) {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-button) {
  font-size: 15px;
  padding: 12px 25px;
  border-radius: 50px;
  font-weight: 600;
  transition: all 0.3s ease;
}

:deep(.el-button--primary) {
  background: #3498db;
  border-color: #3498db;
  box-shadow: 0 4px 15px rgba(52, 152, 219, 0.3);
}

:deep(.el-button--primary:hover) {
  background: #2980b9;
  border-color: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(52, 152, 219, 0.4);
}

@media (max-width: 1200px) {
  .overview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .content-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
}
</style>
