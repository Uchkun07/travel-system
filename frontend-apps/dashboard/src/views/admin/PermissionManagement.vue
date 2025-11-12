<template>
  <div class="permission-management-container">
    <el-card>
      <div class="header">
        <h2>权限管理</h2>
        <el-button type="primary" @click="openPermissionDialog()"
          >添加权限</el-button
        >
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="权限名称">
          <el-input
            v-model="searchForm.permissionName"
            placeholder="请输入权限名称"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input
            v-model="searchForm.permissionCode"
            placeholder="请输入权限编码"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-input
            v-model="searchForm.resourceType"
            placeholder="请输入资源类型"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="是否敏感">
          <el-select
            v-model="searchForm.isSensitive"
            placeholder="请选择"
            clearable
          >
            <el-option label="是" :value="1"></el-option>
            <el-option label="否" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="permissions" stripe style="width: 100%">
        <el-table-column
          prop="permissionId"
          label="ID"
          width="80"
        ></el-table-column>
        <el-table-column
          prop="permissionName"
          label="权限名称"
        ></el-table-column>
        <el-table-column
          prop="permissionCode"
          label="权限编码"
        ></el-table-column>
        <el-table-column prop="resourceType" label="资源类型"></el-table-column>
        <el-table-column prop="resourcePath" label="资源路径"></el-table-column>
        <el-table-column prop="isSensitive" label="是否敏感" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isSensitive === 1 ? 'danger' : 'info'">
              {{ row.isSensitive === 1 ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="sortOrder"
          label="排序"
          width="80"
        ></el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openPermissionDialog(row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click="handleDeletePermission(row.permissionId)"
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

    <!-- 权限编辑/创建弹窗 -->
    <el-dialog
      v-model="permissionDialogVisible"
      :title="isEditMode ? '编辑权限' : '添加权限'"
      width="600px"
    >
      <el-form :model="permissionForm" label-width="100px">
        <el-form-item label="权限名称">
          <el-input
            v-model="permissionForm.permissionName"
            placeholder="请输入权限名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input
            v-model="permissionForm.permissionCode"
            placeholder="例如: USER:VIEW"
          ></el-input>
        </el-form-item>
        <el-form-item label="资源类型">
          <el-input
            v-model="permissionForm.resourceType"
            placeholder="例如: 用户管理"
          ></el-input>
        </el-form-item>
        <el-form-item label="资源路径">
          <el-input
            v-model="permissionForm.resourcePath"
            placeholder="例如: /admin/user/**"
          ></el-input>
        </el-form-item>
        <el-form-item label="是否敏感">
          <el-switch
            v-model="permissionForm.isSensitive"
            :active-value="1"
            :inactive-value="0"
          ></el-switch>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="permissionForm.sortOrder"
            :min="0"
          ></el-input-number>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermission">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import {
  queryPermissions,
  createPermission,
  updatePermission,
  deletePermission,
  type AdminPermission,
  type CreatePermissionRequest,
  type UpdatePermissionRequest,
} from "@/apis/permission";
import { ElMessage, ElMessageBox } from "element-plus";

const permissions = ref<AdminPermission[]>([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});

const searchForm = reactive({
  permissionName: undefined,
  permissionCode: undefined,
  resourceType: undefined,
  isSensitive: undefined as number | undefined,
});

const permissionDialogVisible = ref(false);
const isEditMode = ref(false);
const permissionForm = reactive<Partial<AdminPermission>>({
  permissionId: undefined,
  permissionName: "",
  permissionCode: "",
  resourceType: "",
  resourcePath: "",
  isSensitive: 0,
  sortOrder: 0,
});

// 获取权限列表
const fetchPermissions = async () => {
  try {
    const params = {
      ...queryParams,
      ...searchForm,
    };
    const res = await queryPermissions(params);
    const page = res?.data;
    permissions.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("获取权限列表失败", error);
    ElMessage.error("获取权限列表失败");
  }
};

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchPermissions();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    permissionName: "",
    permissionCode: "",
    resourceType: "",
    isSensitive: undefined,
  });
  queryParams.pageNum = 1;
  fetchPermissions();
};

// 分页
const handlePageChange = (page: number) => {
  queryParams.pageNum = page;
  fetchPermissions();
};

// 打开权限编辑/创建弹窗
const openPermissionDialog = (permission?: AdminPermission) => {
  if (permission) {
    isEditMode.value = true;
    Object.assign(permissionForm, permission);
  } else {
    isEditMode.value = false;
    Object.assign(permissionForm, {
      permissionId: undefined,
      permissionName: "",
      permissionCode: "",
      resourceType: "",
      resourcePath: "",
      isSensitive: 0,
      sortOrder: 0,
    });
  }
  permissionDialogVisible.value = true;
};

// 保存权限
const handleSavePermission = async () => {
  try {
    if (isEditMode.value) {
      await updatePermission(permissionForm as UpdatePermissionRequest);
      ElMessage.success("更新成功");
    } else {
      await createPermission(permissionForm as CreatePermissionRequest);
      ElMessage.success("创建成功");
    }
    permissionDialogVisible.value = false;
    fetchPermissions();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 删除权限
const handleDeletePermission = (permissionId: number) => {
  ElMessageBox.confirm("确定要删除这个权限吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await deletePermission(permissionId);
      ElMessage.success("删除成功");
      fetchPermissions();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

onMounted(() => {
  fetchPermissions();
});
</script>

<style scoped>
.permission-management-container {
  padding: 20px;
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
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
