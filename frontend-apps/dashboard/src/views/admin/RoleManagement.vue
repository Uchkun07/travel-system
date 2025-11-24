<template>
  <div class="role-management-container">
    <el-card>
      <div class="header">
        <h2>角色管理</h2>
        <el-button type="primary" @click="openRoleDialog()">添加角色</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.roleName"
            placeholder="请输入角色名称"
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
      <el-table :data="roles" stripe style="width: 100%">
        <el-table-column prop="roleId" label="ID" width="80"></el-table-column>
        <el-table-column prop="roleName" label="角色名称"></el-table-column>
        <el-table-column prop="roleDesc" label="角色描述"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button size="small" @click="openRoleDialog(row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click="handleDeleteRole(row.roleId)"
              >删除</el-button
            >
            <el-button
              size="small"
              type="warning"
              @click="openPermissionDialog(row)"
              >分配权限</el-button
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

    <!-- 角色编辑/创建弹窗 -->
    <el-dialog
      v-model="roleDialogVisible"
      :title="isEditMode ? '编辑角色' : '添加角色'"
      width="500px"
    >
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色名称">
          <el-input
            v-model="roleForm.roleName"
            placeholder="请输入角色名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input
            v-model="roleForm.roleDesc"
            placeholder="请输入角色描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="roleForm.status"
            :active-value="1"
            :inactive-value="0"
          ></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog v-model="permissionDialogVisible" title="分配权限" width="600px">
      <el-tree
        ref="permissionTreeRef"
        :data="allPermissions"
        :props="{ label: 'permissionName' }"
        show-checkbox
        node-key="permissionId"
        :default-checked-keys="currentRolePermissions"
      ></el-tree>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions"
          >保存</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import Pagination from "@/components/common/Pagination.vue";
import { ref, onMounted, reactive } from "vue";
import {
  queryRoles,
  createRole,
  updateRole,
  deleteRole,
  getAllPermissions,
  getRolePermissions,
  bindPermissionsToRole,
  type AdminRole,
  type CreateRoleRequest,
  type UpdateRoleRequest,
  type AdminPermission,
} from "@/apis/auth";
import { ElMessage, ElMessageBox } from "element-plus";
import type { ElTree } from "element-plus";

const roles = ref<AdminRole[]>([]);
const total = ref(0);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

const searchForm = reactive({
  roleName: "",
  status: undefined as number | undefined,
});

const roleDialogVisible = ref(false);
const isEditMode = ref(false);
const roleForm = reactive<Partial<AdminRole>>({
  roleId: undefined,
  roleName: "",
  roleDesc: "",
  status: 1,
});

const permissionDialogVisible = ref(false);
const allPermissions = ref<AdminPermission[]>([]);
const currentRolePermissions = ref<number[]>([]);
const currentRoleId = ref<number | null>(null);
const permissionTreeRef = ref<InstanceType<typeof ElTree>>();

// 获取角色列表
const fetchRoles = async () => {
  try {
    const params = {
      ...pagination,
      ...searchForm,
    };
    const res = await queryRoles(params);
    const page = res?.data;
    roles.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("获取角色列表失败", error);
    ElMessage.error("获取角色列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  fetchRoles();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    roleName: "",
    status: undefined,
  });
  pagination.pageNum = 1;
  fetchRoles();
};

// 分页
const handlePageChange = (page: number) => {
  pagination.pageNum = page;
  fetchRoles();
};

// 打开角色编辑/创建弹窗
const openRoleDialog = (role?: AdminRole) => {
  if (role) {
    isEditMode.value = true;
    Object.assign(roleForm, role);
  } else {
    isEditMode.value = false;
    Object.assign(roleForm, {
      roleId: undefined,
      roleName: "",
      roleDesc: "",
      status: 1,
    });
  }
  roleDialogVisible.value = true;
};

// 保存角色
const handleSaveRole = async () => {
  try {
    if (isEditMode.value) {
      await updateRole(roleForm as UpdateRoleRequest);
      ElMessage.success("更新成功");
    } else {
      await createRole(roleForm as CreateRoleRequest);
      ElMessage.success("创建成功");
    }
    roleDialogVisible.value = false;
    fetchRoles();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 删除角色
const handleDeleteRole = (roleId: number) => {
  ElMessageBox.confirm("确定要删除这个角色吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await deleteRole(roleId);
      ElMessage.success("删除成功");
      fetchRoles();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

// 获取所有权限
const fetchAllPermissions = async () => {
  try {
    const res = await getAllPermissions();
    allPermissions.value = res?.data ?? [];
  } catch (error) {
    console.error("获取所有权限失败", error);
  }
};

// 打开分配权限弹窗
const openPermissionDialog = async (role: AdminRole) => {
  currentRoleId.value = role.roleId;
  try {
    const res = await getRolePermissions(role.roleId);
    const permissions = res?.data ?? [];
    currentRolePermissions.value = permissions.map(
      (p: AdminPermission) => p.permissionId
    );
    permissionDialogVisible.value = true;
  } catch (error) {
    console.error("获取角色权限失败", error);
    ElMessage.error("获取角色权限失败");
  }
};

// 保存权限分配
const handleSavePermissions = async () => {
  if (currentRoleId.value === null) return;
  const checkedKeys =
    (permissionTreeRef.value?.getCheckedKeys() as number[]) || [];
  try {
    await bindPermissionsToRole({
      roleId: currentRoleId.value,
      permissionIds: checkedKeys,
    });
    ElMessage.success("权限分配成功");
    permissionDialogVisible.value = false;
  } catch (error) {
    ElMessage.error("权限分配失败");
  }
};

onMounted(() => {
  fetchRoles();
  fetchAllPermissions();
});
</script>

<style scoped>
.role-management-container {
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
</style>
