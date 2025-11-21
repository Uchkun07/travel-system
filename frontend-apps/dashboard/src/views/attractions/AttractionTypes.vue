<template>
  <div class="attraction-types-container">
    <el-card>
      <div class="header">
        <h2>景点类型管理</h2>
        <el-button type="primary" @click="openTypeDialog()">添加类型</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型名称">
          <el-input
            v-model="searchForm.typeName"
            placeholder="请输入类型名称"
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
      <el-table :data="types" stripe style="width: 100%">
        <el-table-column prop="typeId" label="ID" width="80"></el-table-column>
        <el-table-column prop="typeName" label="类型名称"></el-table-column>
        <el-table-column
          prop="sortOrder"
          label="排序"
          width="100"
        ></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
        ></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openTypeDialog(row)"
              >编辑</el-button
            >
            <el-button
              size="small"
              type="danger"
              @click="handleDeleteType(row.typeId)"
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

    <!-- 类型编辑/创建弹窗 -->
    <el-dialog
      v-model="typeDialogVisible"
      :title="isEditMode ? '编辑类型' : '添加类型'"
      width="500px"
    >
      <el-form :model="typeForm" label-width="80px">
        <el-form-item label="类型名称">
          <el-input
            v-model="typeForm.typeName"
            placeholder="请输入类型名称"
          ></el-input>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number
            v-model="typeForm.sortOrder"
            :min="0"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="typeForm.status"
            :active-value="1"
            :inactive-value="0"
          ></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveType">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue";
import {
  queryAttractionTypes,
  createAttractionType,
  updateAttractionType,
  deleteAttractionType,
  type AttractionType,
  type CreateAttractionTypeRequest,
  type UpdateAttractionTypeRequest,
} from "@/apis/attraction";
import { ElMessage, ElMessageBox } from "element-plus";

const types = ref<AttractionType[]>([]);
const total = ref(0);
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
});

const searchForm = reactive({
  typeName: "",
  status: undefined as number | undefined,
});

const typeDialogVisible = ref(false);
const isEditMode = ref(false);
const typeForm = reactive<Partial<AttractionType>>({
  typeId: undefined,
  typeName: "",
  sortOrder: 0,
  status: 1,
});

// 获取类型列表
const fetchTypes = async () => {
  try {
    const params = {
      ...queryParams,
      ...searchForm,
    };
    const res = await queryAttractionTypes(params);
    const page = res?.data;
    types.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("获取景点类型列表失败", error);
    ElMessage.error("获取景点类型列表失败");
  }
};

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchTypes();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    typeName: "",
    status: undefined,
  });
  queryParams.pageNum = 1;
  fetchTypes();
};

// 分页
const handlePageChange = (page: number) => {
  queryParams.pageNum = page;
  fetchTypes();
};

// 打开类型编辑/创建弹窗
const openTypeDialog = (type?: AttractionType) => {
  if (type) {
    isEditMode.value = true;
    Object.assign(typeForm, type);
  } else {
    isEditMode.value = false;
    Object.assign(typeForm, {
      typeId: undefined,
      typeName: "",
      sortOrder: 0,
      status: 1,
    });
  }
  typeDialogVisible.value = true;
};

// 保存类型
const handleSaveType = async () => {
  try {
    if (isEditMode.value) {
      await updateAttractionType(typeForm as UpdateAttractionTypeRequest);
      ElMessage.success("更新成功");
    } else {
      await createAttractionType(typeForm as CreateAttractionTypeRequest);
      ElMessage.success("创建成功");
    }
    typeDialogVisible.value = false;
    fetchTypes();
  } catch (error) {
    ElMessage.error("操作失败");
  }
};

// 删除类型
const handleDeleteType = (typeId: number) => {
  ElMessageBox.confirm("确定要删除这个景点类型吗?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(async () => {
    try {
      await deleteAttractionType(typeId);
      ElMessage.success("删除成功");
      fetchTypes();
    } catch (error) {
      ElMessage.error("删除失败");
    }
  });
};

onMounted(() => {
  fetchTypes();
});
</script>

<style scoped>
.attraction-types-container {
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
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
