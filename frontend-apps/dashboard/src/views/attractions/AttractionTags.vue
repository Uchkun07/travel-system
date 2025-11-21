<template>
  <div class="attraction-tags-container">
    <el-card>
      <div class="header">
        <h2>景点标签管理</h2>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标签ID">
          <el-input
            v-model.number="searchForm.tagId"
            placeholder="请输入标签ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="标签名称">
          <el-input
            v-model="searchForm.tagName"
            placeholder="请输入标签名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="primary" @click="handleAdd">添加标签</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 数据表格 -->
      <el-table :data="tags" border style="width: 100%">
        <el-table-column prop="tagId" label="标签ID" width="100" />
        <el-table-column prop="tagName" label="标签名称" width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)"
              >编辑</el-button
            >
            <el-button type="danger" size="small" @click="handleDelete(row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 添加/编辑标签对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="400px"
      @close="handleDialogClose"
    >
      <el-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="formData.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import {
  queryTags,
  createTag,
  updateTag,
  deleteTag,
  type AttractionTag,
  type CreateTagRequest,
  type UpdateTagRequest,
  type QueryTagsRequest,
} from "@/apis/attraction";

// 搜索表单
const searchForm = reactive<QueryTagsRequest>({
  tagId: undefined,
  tagName: "",
  status: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const tags = ref<AttractionTag[]>([]);
const total = ref(0);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();
const formData = reactive<CreateTagRequest & { tagId?: number }>({
  tagName: "",
  status: 1,
});

// 表单验证规则
const formRules: FormRules = {
  tagName: [{ required: true, message: "请输入标签名称", trigger: "blur" }],
};

// 加载标签列表
const loadTags = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await queryTags(params);
    const page = res?.data;
    tags.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载标签列表失败:", error);
    ElMessage.error("加载标签列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadTags();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    tagId: undefined,
    tagName: "",
    status: undefined,
  });
  handleSearch();
};

// 添加标签
const handleAdd = () => {
  dialogTitle.value = "添加标签";
  Object.assign(formData, {
    tagName: "",
    status: 1,
    tagId: undefined,
  });
  dialogVisible.value = true;
};

// 编辑标签
const handleEdit = (row: AttractionTag) => {
  dialogTitle.value = "编辑标签";
  Object.assign(formData, {
    tagId: row.tagId,
    tagName: row.tagName,
    status: row.status,
  });
  dialogVisible.value = true;
};

// 删除标签
const handleDelete = (row: AttractionTag) => {
  ElMessageBox.confirm(`确定要删除标签"${row.tagName}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteTag(row.tagId);
        ElMessage.success("删除成功");
        loadTags();
      } catch (error) {
        console.error("删除失败:", error);
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {});
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.tagId) {
          // 编辑
          const params: UpdateTagRequest = {
            tagId: formData.tagId,
            tagName: formData.tagName,
            status: formData.status,
          };
          await updateTag(params);
          ElMessage.success("更新成功");
        } else {
          // 添加
          await createTag(formData);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadTags();
      } catch (error) {
        console.error("操作失败:", error);
        ElMessage.error("操作失败");
      }
    }
  });
};

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields();
};

// 分页大小改变
const handleSizeChange = () => {
  loadTags();
};

// 页码改变
const handleCurrentChange = () => {
  loadTags();
};

// 初始化
onMounted(() => {
  loadTags();
});
</script>

<style scoped>
.attraction-tags-container {
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
