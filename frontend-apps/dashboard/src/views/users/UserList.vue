<template>
  <div class="user-list-container">
    <el-card>
      <div class="header">
        <h2>用户列表</h2>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户ID">
          <el-input
            v-model.number="searchForm.userId"
            placeholder="请输入用户ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input
            v-model="searchForm.nickname"
            placeholder="请输入昵称"
            clearable
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 数据表格 -->
      <el-table :data="users" border style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="avatarUrl" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar
              v-if="row.avatarUrl"
              :src="getFullAvatarUrl(row.avatarUrl)"
              :size="40"
            />
            <el-avatar v-else :size="40">{{
              row.username?.charAt(0).toUpperCase()
            }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="手机号" width="130">
          <template #default="{ row }">
            {{ row.profile?.phone || "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleViewDetail(row)"
              >详情</el-button
            >
            <el-button type="info" size="small" @click="handleManageTags(row)"
              >管理标签</el-button
            >
            <el-button type="warning" size="small" @click="handleViewStats(row)"
              >统计</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Pagination
        :total="total"
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total-pages="Math.ceil(total / pagination.pageSize)"
        @page-change="handlePageChange"
      />
    </el-card>

    <!-- 用户详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="用户详情" width="700px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{
          currentUser.userId
        }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{
          currentUser.username
        }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{
          currentUser.nickname || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{
          currentUser.email || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? "正常" : "禁用" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{
          currentUser.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="最后登录">{{
          currentUser.lastLoginTime || "-"
        }}</el-descriptions-item>

        <el-descriptions-item label="头像" :span="2">
          <el-avatar
            v-if="currentUser.avatarUrl"
            :src="getFullAvatarUrl(currentUser.avatarUrl)"
            :size="60"
          />
        </el-descriptions-item>

        <el-descriptions-item label="基本信息" :span="2">
          <div v-if="currentUser.profile" class="profile-info">
            <p>
              <strong>姓名:</strong> {{ currentUser.profile.fullName || "-" }}
            </p>
            <p><strong>手机:</strong> {{ currentUser.profile.phone || "-" }}</p>
            <p>
              <strong>性别:</strong>
              {{ getGenderText(currentUser.profile.gender) }}
            </p>
            <p>
              <strong>生日:</strong> {{ currentUser.profile.birthday || "-" }}
            </p>
            <p>
              <strong>地址:</strong>
              {{ currentUser.profile.residentAddress || "-" }}
            </p>
          </div>
          <span v-else>暂无信息</span>
        </el-descriptions-item>

        <el-descriptions-item label="偏好设置" :span="2">
          <div v-if="currentUser.preference" class="preference-info">
            <p>
              <strong>偏好景点类型ID:</strong>
              {{ currentUser.preference.preferAttractionTypeId || "-" }}
            </p>
            <p>
              <strong>预算下限:</strong>
              {{ currentUser.preference.budgetFloor || "-" }}
            </p>
            <p>
              <strong>预算范围:</strong>
              {{ currentUser.preference.budgetRange || "-" }}
            </p>
            <p>
              <strong>旅行人群:</strong>
              {{ currentUser.preference.travelCrowd || "-" }}
            </p>
            <p>
              <strong>偏好季节:</strong>
              {{ currentUser.preference.preferSeason || "-" }}
            </p>
          </div>
          <span v-else>暂无信息</span>
        </el-descriptions-item>

        <el-descriptions-item label="用户标签" :span="2">
          <el-tag
            v-for="tag in currentUser.tags"
            :key="tag.tagDictId"
            style="margin-right: 8px; margin-bottom: 8px"
            type="info"
          >
            {{ tag.tagName }}
          </el-tag>
          <span v-if="!currentUser.tags || currentUser.tags.length === 0"
            >暂无标签</span
          >
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 管理标签对话框 -->
    <el-dialog v-model="tagDialogVisible" title="管理用户标签" width="500px">
      <div class="tag-management">
        <el-checkbox-group v-model="selectedTags">
          <el-checkbox
            v-for="tag in allTagDicts"
            :key="tag.tagDictId"
            :label="tag.tagDictId"
            :disabled="tag.status === 0"
          >
            {{ tag.tagName }}
            <el-tag
              v-if="tag.status === 0"
              type="danger"
              size="small"
              style="margin-left: 8px"
            >
              已禁用
            </el-tag>
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTags">保存</el-button>
      </template>
    </el-dialog>

    <!-- 统计信息对话框 -->
    <el-dialog v-model="statsDialogVisible" title="用户统计信息" width="500px">
      <el-descriptions :column="1" border v-if="currentStats">
        <el-descriptions-item label="收藏数量">{{
          currentStats.collectCount
        }}</el-descriptions-item>
        <el-descriptions-item label="浏览数量">{{
          currentStats.browsingCount
        }}</el-descriptions-item>
        <el-descriptions-item label="规划数量">{{
          currentStats.planningCount
        }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{
          currentStats.createTime
        }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{
          currentStats.updateTime
        }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无统计数据" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import Pagination from "@/components/common/Pagination.vue";
import { ref, reactive, onMounted } from "vue";
import { ElCard, ElMessage, type FormInstance } from "element-plus";
import {
  queryUsers,
  getUserDetail,
  getUserCountTable,
  getUserTags,
  batchBindUserTags,
  batchUnbindUserTags,
  getAllActiveUserTagDicts,
  type UserDetailResponse,
  type QueryUsersRequest,
  type UserCountTable,
  type UserTagDict,
} from "@/apis/user";

// 搜索表单
const searchForm = reactive<QueryUsersRequest>({
  userId: undefined,
  username: "",
  nickname: "",
  email: "",
  phone: "",
  status: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const users = ref<UserDetailResponse[]>([]);
const total = ref<number>(0);
const currentUser = ref<UserDetailResponse | null>(null);
const currentStats = ref<UserCountTable | null>(null);
const allTagDicts = ref<UserTagDict[]>([]);

// 对话框
const detailDialogVisible = ref(false);
const tagDialogVisible = ref(false);
const statsDialogVisible = ref(false);
const selectedTags = ref<number[]>([]);
const currentUserId = ref<number>(0);

// 获取完整头像 URL
const getFullAvatarUrl = (avatarUrl?: string) => {
  if (!avatarUrl) return "";
  if (avatarUrl.startsWith("http")) return avatarUrl;
  const baseUrl =
    import.meta.env.VITE_API_BASE_URL || "http://8.146.237.23:8080";
  return `${baseUrl}${avatarUrl}`;
};

// 获取性别文本
const getGenderText = (gender?: number) => {
  if (gender === undefined || gender === null) return "-";
  const genderMap: Record<number, string> = {
    0: "未知",
    1: "男",
    2: "女",
  };
  return genderMap[gender] || "-";
};

// 加载所有标签字典
const loadAllTagDicts = async () => {
  try {
    const res = await getAllActiveUserTagDicts();
    allTagDicts.value = res?.data ?? [];
  } catch (error) {
    console.error("加载标签字典失败:", error);
  }
};

// 加载用户列表
const loadUsers = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await queryUsers(params);
    const page = res?.data;
    users.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载用户列表失败:", error);
    ElMessage.error("加载用户列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadUsers();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    userId: undefined,
    username: "",
    nickname: "",
    email: "",
    phone: "",
    status: undefined,
  });
  handleSearch();
};

// 查看详情
const handleViewDetail = async (row: UserDetailResponse) => {
  try {
    const res = await getUserDetail(row.userId);
    currentUser.value = res?.data ?? null;
    detailDialogVisible.value = true;
  } catch (error) {
    console.error("加载用户详情失败:", error);
    ElMessage.error("加载用户详情失败");
  }
};

// 管理标签
const handleManageTags = async (row: UserDetailResponse) => {
  currentUserId.value = row.userId;
  try {
    const res = await getUserTags(row.userId);
    const tags = res?.data ?? [];
    selectedTags.value = tags.map((tag) => tag.tagDictId);
    tagDialogVisible.value = true;
  } catch (error) {
    console.error("加载用户标签失败:", error);
    ElMessage.error("加载用户标签失败");
  }
};

// 保存标签
const handleSaveTags = async () => {
  try {
    const res = await getUserTags(currentUserId.value);
    const currentTags = res?.data ?? [];
    const currentTagIds = currentTags.map((tag) => tag.tagDictId);

    // 计算需要绑定和解绑的标签
    const tagsToAdd = selectedTags.value.filter(
      (id) => !currentTagIds.includes(id)
    );
    const tagsToRemove = currentTagIds.filter(
      (id) => !selectedTags.value.includes(id)
    );

    // 绑定新标签
    if (tagsToAdd.length > 0) {
      await batchBindUserTags({
        userId: currentUserId.value,
        tagDictIds: tagsToAdd,
      });
    }

    // 解绑标签
    if (tagsToRemove.length > 0) {
      await batchUnbindUserTags({
        userId: currentUserId.value,
        tagDictIds: tagsToRemove,
      });
    }

    ElMessage.success("保存成功");
    tagDialogVisible.value = false;
    loadUsers();
  } catch (error) {
    console.error("保存标签失败:", error);
    ElMessage.error("保存标签失败");
  }
};

// 查看统计
const handleViewStats = async (row: UserDetailResponse) => {
  try {
    const res = await getUserCountTable(row.userId);
    currentStats.value = res?.data ?? null;
    statsDialogVisible.value = true;
  } catch (error) {
    console.error("加载统计信息失败:", error);
    ElMessage.error("加载统计信息失败");
  }
};

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  pagination.pageNum = page;
  pagination.pageSize = pageSize;
  loadUsers();
};

// 初始化
onMounted(() => {
  loadAllTagDicts();
  loadUsers();
});
</script>

<style scoped>
.user-list-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.profile-info p,
.preference-info p {
  margin: 8px 0;
}

.tag-management {
  max-height: 400px;
  overflow-y: auto;
}

.tag-management .el-checkbox {
  display: block;
  margin: 10px 0;
}
</style>
