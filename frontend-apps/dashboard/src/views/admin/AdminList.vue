<template>
  <div class="admin-list-container">
    <el-card>
      <div class="header">
        <h2>管理员列表</h2>
        <el-button type="primary" @click="openAdminDialog()"
          >添加管理员</el-button
        >
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.fullName"
            placeholder="请输入姓名"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 表格 -->
      <el-table :data="admins" stripe style="width: 100%">
        <el-table-column prop="adminId" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="fullName" label="姓名"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="lastLoginTime"
          label="最后登录时间"
          width="180"
        ></el-table-column>
        <el-table-column label="操作" width="380" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openAdminDialog(row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="warning"
              @click="openPasswordDialog(row)"
              >修改密码</el-button
            >
            <el-button size="small" type="primary" @click="openRoleDialog(row)"
              >分配角色</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click="handleDeleteAdmin(row.adminId)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        background
        layout="prev, pager, next, total"
        :total="total"
        :page-size="queryParams.pageSize"
        :current-page="queryParams.pageNum"
        @current-change="handlePageChange"
        class="pagination-container"
      ></el-pagination>
    </el-card>

    <!-- 管理员编辑/创建弹窗 -->
    <el-dialog
      v-model="adminDialogVisible"
      :title="isEditMode ? '编辑管理员' : '添加管理员'"
      width="500px"
    >
      <el-form :model="adminForm" label-width="80px">
        <el-form-item label="用户名" v-if="!isEditMode">
          <el-input
            v-model="adminForm.username"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEditMode">
          <el-input
            v-model="adminForm.password"
            type="password"
            placeholder="请输入密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="adminForm.fullName"
            placeholder="请输入姓名"
          ></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="adminForm.email"
            placeholder="请输入邮箱"
          ></el-input>
        </el-form-item>
        <el-form-item label="电话">
          <el-input
            v-model="adminForm.phone"
            placeholder="请输入电话"
          ></el-input>
        </el-form-item>
        <el-form-item label="状态" v-if="isEditMode">
          <el-switch
            v-model="adminForm.status"
            :active-value="1"
            :inactive-value="0"
          ></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adminDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveAdmin">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码(6-30位)"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePassword">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox
          v-for="role in allRoles"
          :key="role.roleId"
          :label="role.roleId"
        >
          {{ role.roleName }} - {{ role.roleDesc }}
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import {
  queryAdmins,
  createAdmin,
  updateAdmin,
  deleteAdmin,
  updatePassword,
  getAdminRoles,
  bindRolesToAdmin,
  getAllRoles,
  type Admin,
  type AdminRole,
  type CreateAdminRequest,
  type UpdateAdminRequest,
  type UpdatePasswordRequest,
} from "@/apis/auth";
import { ElMessage, ElMessageBox } from "element-plus";

const admins = ref<Admin[]>([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});

const searchForm = reactive({
  username: "",
  fullName: "",
  email: "",
  phone: "",
  status: undefined as number | undefined,
});

const adminDialogVisible = ref(false);
const isEditMode = ref(false);
const adminForm = reactive<Partial<Admin & { password: string }>>({
  adminId: undefined,
  username: "",
  password: "",
  fullName: "",
  email: "",
  phone: "",
  status: 1,
});

const passwordDialogVisible = ref(false);
const passwordForm = reactive({
  adminId: 0,
  newPassword: "",
});

const roleDialogVisible = ref(false);
const allRoles = ref<AdminRole[]>([]);
const selectedRoleIds = ref<number[]>([]);
const currentAdminId = ref<number | null>(null);

// 获取管理员列表
const fetchAdmins = async () => {
  try {
    const params = {
      ...queryParams,
      ...searchForm,
    };
    const res = await queryAdmins(params);
    const page = res?.data;
    admins.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("获取管理员列表失败", error);
    ElMessage.error("获取管理员列表失败");
  }
};

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchAdmins();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: "",
    fullName: "",
    email: "",
    phone: "",
    status: undefined,
  });
  queryParams.pageNum = 1;
  fetchAdmins();
};

// 分页
const handlePageChange = (page: number) => {
  queryParams.pageNum = page;
  fetchAdmins();
};

// 打开管理员编辑/创建弹窗
const openAdminDialog = (admin?: Admin) => {
  if (admin) {
    isEditMode.value = true;
    Object.assign(adminForm, {
      adminId: admin.adminId,
      fullName: admin.fullName,
      email: admin.email,
      phone: admin.phone,
      status: admin.status,
    });
  } else {
    isEditMode.value = false;
    Object.assign(adminForm, {
      adminId: undefined,
      username: "",
      password: "",
      fullName: "",
      email: "",
      phone: "",
      status: 1,
    });
  }
  adminDialogVisible.value = true;
};

// 保存管理员
const handleSaveAdmin = async () => {
  try {
    if (isEditMode.value) {
      await updateAdmin(adminForm as UpdateAdminRequest);
      ElMessage.success("更新成功");
    } else {
      await createAdmin(adminForm as CreateAdminRequest);
      ElMessage.success("创建成功");
    }
    adminDialogVisible.value = false;
    fetchAdmins();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 删除管理员
const handleDeleteAdmin = (adminId: number) => {
  ElMessageBox.confirm("确定要删除这个管理员吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await deleteAdmin(adminId);
      ElMessage.success("删除成功");
      fetchAdmins();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 打开修改密码弹窗
const openPasswordDialog = (admin: Admin) => {
  passwordForm.adminId = admin.adminId;
  passwordForm.newPassword = "";
  passwordDialogVisible.value = true;
};

// 保存密码
const handleSavePassword = async () => {
  if (
    !passwordForm.newPassword ||
    passwordForm.newPassword.length < 6 ||
    passwordForm.newPassword.length > 30
  ) {
    ElMessage.warning("密码长度必须在6-30位之间");
    return;
  }
  try {
    await updatePassword(passwordForm as UpdatePasswordRequest);
    ElMessage.success("密码修改成功");
    passwordDialogVisible.value = false;
  } catch (error) {
    ElMessage.error("密码修改失败");
  }
};

// 获取所有角色
const fetchAllRoles = async () => {
  try {
    const res = await getAllRoles();
    allRoles.value = res?.data ?? [];
  } catch (error) {
    console.error("获取角色列表失败", error);
  }
};

// 打开分配角色弹窗
const openRoleDialog = async (admin: Admin) => {
  currentAdminId.value = admin.adminId;
  try {
    const res = await getAdminRoles(admin.adminId);
    const roles = res?.data ?? [];
    selectedRoleIds.value = roles.map((role: AdminRole) => role.roleId);
    roleDialogVisible.value = true;
  } catch (error) {
    console.error("获取管理员角色失败", error);
    ElMessage.error("获取管理员角色失败");
  }
};

// 保存角色分配
const handleSaveRoles = async () => {
  if (currentAdminId.value === null) return;
  try {
    await bindRolesToAdmin({
      adminId: currentAdminId.value,
      roleIds: selectedRoleIds.value,
    });
    ElMessage.success("角色分配成功");
    roleDialogVisible.value = false;
  } catch (error) {
    ElMessage.error("角色分配失败");
  }
};

onMounted(() => {
  fetchAdmins();
  fetchAllRoles();
});
</script>

<style scoped>
.admin-list-container {
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
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
