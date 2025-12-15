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
    <div class="profile-form" v-loading="dataLoading">
      <h3 class="form-section-title">基本信息</h3>

      <ElForm :model="profileForm" label-width="85px" label-position="left">
        <ElRow :gutter="20">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="用户名">
              <ElInput v-model="profileForm.username" :disabled="true" />
            </ElFormItem>
          </ElCol>
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="真实姓名">
              <ElInput v-model="profileForm.realName" :disabled="!isEditing" />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElRow :gutter="20">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="手机号码">
              <ElInput v-model="profileForm.phone" :disabled="!isEditing" />
            </ElFormItem>
          </ElCol>
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="性别">
              <ElSelect
                v-model="profileForm.gender"
                :disabled="!isEditing"
                style="width: 100%"
              >
                <ElOption label="保密" :value="0" />
                <ElOption label="男" :value="1" />
                <ElOption label="女" :value="2" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>
        <ElRow :gutter="20">
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
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="邮箱">
              <ElInput v-model="profileForm.email" :disabled="true" />
            </ElFormItem>
          </ElCol>
        </ElRow>
        <ElRow :gutter="20">
          <ElCol :xs="24">
            <ElFormItem label="常驻地址">
              <ElInput
                v-model="profileForm.residentAddress"
                :disabled="!isEditing"
                placeholder="请输入常驻地址"
              />
            </ElFormItem>
          </ElCol>
        </ElRow>
        <ElFormItem v-if="isEditing">
          <ElButton type="primary" @click="handleSave">保存修改</ElButton>
          <ElButton @click="handleCancel">取消</ElButton>
        </ElFormItem>
      </ElForm>
    </div>

    <!-- 偏好信息 -->
    <div class="profile-form" v-loading="preferenceLoading">
      <h3 class="form-section-title">旅行偏好</h3>

      <ElForm :model="preferenceForm" label-width="110px" label-position="left">
        <ElRow :gutter="20">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="偏好景点类型">
              <ElSelect
                v-model="preferenceForm.preferAttractionTypeId"
                :disabled="!isEditingPreference"
                placeholder="选择喜欢的景点类型"
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
          </ElCol>
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="出行人群">
              <ElSelect
                v-model="preferenceForm.travelCrowd"
                :disabled="!isEditingPreference"
                placeholder="选择出行人群"
                clearable
                style="width: 100%"
              >
                <ElOption label="独自出行" value="独自出行" />
                <ElOption label="情侣" value="情侣" />
                <ElOption label="亲子" value="亲子" />
                <ElOption label="朋友" value="朋友" />
                <ElOption label="家庭" value="家庭" />
              </ElSelect>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElRow :gutter="20">
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="预算下限 (元)">
              <ElInputNumber
                v-model="preferenceForm.budgetFloor"
                :disabled="!isEditingPreference"
                :min="0"
                :max="100000"
                :step="100"
                placeholder="最低预算"
                controls-position="right"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :xs="24" :sm="12">
            <ElFormItem label="预算上限 (元)">
              <ElInputNumber
                v-model="preferenceForm.budgetRange"
                :disabled="!isEditingPreference"
                :min="0"
                :max="100000"
                :step="100"
                placeholder="最高预算"
                controls-position="right"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElRow :gutter="20">
          <ElCol :xs="24">
            <ElFormItem label="偏好出行季节">
              <ElCheckboxGroup
                v-model="selectedSeasons"
                :disabled="!isEditingPreference"
              >
                <ElCheckbox value="春">春季</ElCheckbox>
                <ElCheckbox value="夏">夏季</ElCheckbox>
                <ElCheckbox value="秋">秋季</ElCheckbox>
                <ElCheckbox value="冬">冬季</ElCheckbox>
              </ElCheckboxGroup>
            </ElFormItem>
          </ElCol>
        </ElRow>

        <ElFormItem v-if="isEditingPreference">
          <ElButton type="primary" @click="handleSavePreference">
            保存偏好
          </ElButton>
          <ElButton @click="handleCancelPreference">取消</ElButton>
        </ElFormItem>
        <ElFormItem v-else>
          <ElButton type="primary" @click="handleEditPreference">
            编辑偏好
          </ElButton>
        </ElFormItem>
      </ElForm>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
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
  ElInputNumber,
  ElCheckboxGroup,
  ElCheckbox,
} from "element-plus";
import { Edit, Suitcase, Star, Calendar } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores";
import { useAttractionTypeStore } from "@/stores/attractionType";
import {
  getUserProfile,
  updateUserProfile,
  type UpdateProfileRequest,
} from "@/apis/auth";
import {
  getUserPreference,
  updateUserPreference,
  type UserPreferenceRequest,
} from "@/apis/userPreference";

const userStore = useUserStore();
const attractionTypeStore = useAttractionTypeStore();
const isEditing = ref(false);
const loading = ref(false);
const dataLoading = ref(true); // 添加数据加载状态

// 偏好相关状态
const isEditingPreference = ref(false);
const preferenceLoading = ref(true);
const selectedSeasons = ref<string[]>([]);

// 统计数据
const stats = reactive({
  orders: 12,
  favorites: 24,
  plans: 8,
});

// 个人资料表单
const profileForm = reactive({
  username: "",
  realName: "",
  email: "",
  phone: "",
  gender: 0 as number, // 0=保密, 1=男, 2=女
  birthday: null as Date | null,
  residentAddress: "",
});

// 偏好表单
const preferenceForm = reactive<UserPreferenceRequest>({
  preferAttractionTypeId: undefined,
  budgetFloor: undefined,
  budgetRange: undefined,
  travelCrowd: undefined,
  preferSeason: undefined,
});

// 备份原始数据
let originalForm = { ...profileForm };
let originalPreferenceForm = { ...preferenceForm };

// 加载用户偏好
const loadUserPreference = async () => {
  preferenceLoading.value = true;
  try {
    const response = await getUserPreference();
    if (response.code === 200 && response.data) {
      const data = response.data;
      preferenceForm.preferAttractionTypeId = data.preferAttractionTypeId;
      preferenceForm.budgetFloor = data.budgetFloor;
      preferenceForm.budgetRange = data.budgetRange;
      preferenceForm.travelCrowd = data.travelCrowd;
      preferenceForm.preferSeason = data.preferSeason;

      // 解析季节字符串为数组
      if (data.preferSeason) {
        selectedSeasons.value = data.preferSeason.split(",").filter((s) => s);
      } else {
        selectedSeasons.value = [];
      }

      // 保存原始数据
      originalPreferenceForm = { ...preferenceForm };
    }
  } catch (error: any) {
    console.error("加载用户偏好失败:", error);
    if (error.response?.status !== 404) {
      // 404 表示用户还没有偏好记录，不需要提示错误
      ElMessage.warning("加载偏好信息失败");
    }
  } finally {
    preferenceLoading.value = false;
  }
};

// 组件挂载时加载用户信息和偏好
onMounted(async () => {
  dataLoading.value = true;
  try {
    const response = await getUserProfile();
    console.log("获取用户资料响应:", response); // 调试日志

    // 后端返回的是 ApiResponse 格式，需要访问 data 属性
    const userData = response.data || response;

    if (userData) {
      profileForm.username = userData.username || "";
      profileForm.realName = userData.fullName || "";
      profileForm.email = userData.email || "";
      profileForm.phone = userData.phone || "";
      profileForm.gender = userData.gender ?? 0;
      profileForm.residentAddress = userData.residentAddress || "";

      // 解析生日字符串为 Date 对象
      if (userData.birthday) {
        profileForm.birthday = new Date(userData.birthday);
      }

      // 保存原始数据
      originalForm = { ...profileForm };

      console.log("表单数据已更新:", profileForm); // 调试日志
    }
  } catch (error: any) {
    console.error("加载用户信息失败:", error);
    if (error.response?.status === 401) {
      ElMessage.error("登录已过期，请重新登录");
      userStore.logout();
    } else {
      ElMessage.error("加载用户信息失败，请刷新页面重试");
    }
  } finally {
    dataLoading.value = false;
  }

  // 加载景点类型和用户偏好
  attractionTypeStore.fetchAttractionTypes().catch((error) => {
    console.error("加载景点类型失败:", error);
  });
  await loadUserPreference();
});

const handleEdit = () => {
  isEditing.value = true;
};

const handleSave = async () => {
  loading.value = true;

  try {
    // 构建更新请求数据
    const updateData: UpdateProfileRequest = {
      fullName: profileForm.realName || undefined,
      phone: profileForm.phone || undefined,
      gender: profileForm.gender,
      birthday: profileForm.birthday
        ? profileForm.birthday.toISOString().split("T")[0]
        : undefined,
      residentAddress: profileForm.residentAddress || undefined,
    };

    const response = await updateUserProfile(updateData);
    console.log("更新用户资料响应:", response); // 调试日志

    // 后端返回的是 ApiResponse 格式，需要访问 data 属性
    const userData = response.data || response;

    if (userData) {
      ElMessage.success("资料更新成功");
      isEditing.value = false;

      // 更新表单显示
      profileForm.username = userData.username;
      profileForm.realName = userData.fullName || "";
      profileForm.email = userData.email || "";
      profileForm.phone = userData.phone || "";
      profileForm.gender = userData.gender || 0;
      profileForm.birthday = userData.birthday
        ? new Date(userData.birthday)
        : null;
      profileForm.residentAddress = userData.residentAddress || "";

      // 更新 store 中的用户信息
      userStore.setUserInfo({
        userId: userData.userId,
        username: userData.username,
        email: userData.email || "",
        fullName: userData.fullName,
        phone: userData.phone,
        gender: userData.gender,
        birthday: userData.birthday,
        avatar: userData.avatar,
      });

      // 更新备份
      originalForm = { ...profileForm };
    } else {
      ElMessage.error("资料更新失败");
    }
  } catch (error: any) {
    console.error("保存用户资料失败:", error);

    // 处理不同的错误状态
    if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        ElMessage.error("登录已过期，请重新登录");
        userStore.logout();
      } else if (status === 400) {
        ElMessage.error(error.response.data?.message || "参数验证失败");
      } else {
        ElMessage.error("保存失败，请稍后重试");
      }
    } else {
      ElMessage.error("网络错误，请检查连接");
    }
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  // 恢复原始数据
  Object.assign(profileForm, originalForm);
  isEditing.value = false;
};

// 编辑偏好
const handleEditPreference = () => {
  isEditingPreference.value = true;
};

// 保存偏好
const handleSavePreference = async () => {
  loading.value = true;
  try {
    // 设置季节字符串
    preferenceForm.preferSeason = selectedSeasons.value.join(",") || undefined;

    const response = await updateUserPreference(preferenceForm);
    if (response.code === 200) {
      ElMessage.success("偏好设置保存成功");
      isEditingPreference.value = false;

      // 更新偏好数据
      if (response.data) {
        const data = response.data;
        preferenceForm.preferAttractionTypeId = data.preferAttractionTypeId;
        preferenceForm.budgetFloor = data.budgetFloor;
        preferenceForm.budgetRange = data.budgetRange;
        preferenceForm.travelCrowd = data.travelCrowd;
        preferenceForm.preferSeason = data.preferSeason;

        if (data.preferSeason) {
          selectedSeasons.value = data.preferSeason
            .split(",")
            .filter((s: string) => s);
        }
      }

      // 更新备份
      originalPreferenceForm = { ...preferenceForm };
    } else {
      ElMessage.error(response.message || "保存失败");
    }
  } catch (error: any) {
    console.error("保存偏好失败:", error);
    ElMessage.error(error?.message || "保存失败，请稍后重试");
  } finally {
    loading.value = false;
  }
};

// 取消编辑偏好
const handleCancelPreference = () => {
  // 恢复原始数据
  Object.assign(preferenceForm, originalPreferenceForm);

  // 恢复季节选择
  if (originalPreferenceForm.preferSeason) {
    selectedSeasons.value = originalPreferenceForm.preferSeason
      .split(",")
      .filter((s: string) => s);
  } else {
    selectedSeasons.value = [];
  }

  isEditingPreference.value = false;
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

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
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
  padding: 2px 15px;
  box-shadow: none !important;
  background-color: #3498db1a;
  border-radius: 50px;
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
:deep(.el-select__wrapper) {
  height: 34px;
  box-shadow: none !important;
  background-color: #3498db1a;
  border-radius: 50px;
  padding: 2px 15px;
}
:deep(.el-select__wrapper.is-disabled) {
  background-color: #f5f7fa;
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
