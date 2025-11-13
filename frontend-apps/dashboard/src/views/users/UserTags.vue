<template>
  <div class="user-tags-container">
    <el-card>
      <div class="header">
        <h2>用户标签管理</h2>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标签ID">
          <el-input
            v-model.number="searchForm.tagDictId"
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
        <el-form-item label="标签编码">
          <el-input
            v-model="searchForm.tagCode"
            placeholder="请输入标签编码"
            clearable
          />
        </el-form-item>
        <el-form-item label="标签级别">
          <el-select
            v-model="searchForm.tagLevel"
            placeholder="请选择级别"
            clearable
          >
            <el-option label="1级" :value="1" />
            <el-option label="2级" :value="2" />
            <el-option label="3级" :value="3" />
            <el-option label="4级" :value="4" />
            <el-option label="5级" :value="5" />
          </el-select>
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

      <!-- 数据表格 -->
      <el-table :data="tagDicts" border style="width: 100%">
        <el-table-column prop="tagDictId" label="标签ID" width="100" />
        <el-table-column prop="tagName" label="标签名称" width="150" />
        <el-table-column prop="tagCode" label="标签编码" width="150" />
        <el-table-column prop="tagLevel" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.tagLevel)"
              >{{ row.tagLevel }}级</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="描述"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="iconUrl" label="图标" width="80">
          <template #default="{ row }">
            <el-image
              v-if="row.iconUrl"
              :src="row.iconUrl"
              fit="cover"
              style="width: 40px; height: 40px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column
          prop="triggerCondition"
          label="触发条件"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        label-width="120px"
      >
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="formData.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签编码" prop="tagCode">
          <el-input
            v-model="formData.tagCode"
            placeholder="请输入标签编码（唯一）"
          />
        </el-form-item>
        <el-form-item label="标签级别" prop="tagLevel">
          <el-select
            v-model="formData.tagLevel"
            placeholder="请选择级别"
            style="width: 100%"
          >
            <el-option label="1级" :value="1" />
            <el-option label="2级" :value="2" />
            <el-option label="3级" :value="3" />
            <el-option label="4级" :value="4" />
            <el-option label="5级" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入标签描述"
          />
        </el-form-item>
        <el-form-item label="图标URL" prop="iconUrl">
          <el-input v-model="formData.iconUrl" placeholder="请输入图标URL" />
        </el-form-item>
        <el-form-item label="图标预览" v-if="formData.iconUrl">
          <el-image
            :src="formData.iconUrl"
            fit="cover"
            style="width: 60px; height: 60px; border-radius: 4px"
          />
        </el-form-item>
        <el-form-item label="触发条件" prop="triggerCondition">
          <el-input
            v-model="formData.triggerCondition"
            type="textarea"
            :rows="3"
            placeholder="请输入触发条件（JSON格式）"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="formData.sortOrder"
            :min="0"
            style="width: 100%"
          />
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
  queryUserTagDicts,
  createUserTagDict,
  updateUserTagDict,
  deleteUserTagDict,
  type UserTagDict,
  type CreateUserTagDictRequest,
  type UpdateUserTagDictRequest,
  type QueryUserTagDictsRequest,
} from "@/apis/user";

// 搜索表单
const searchForm = reactive<QueryUserTagDictsRequest>({
  tagDictId: undefined,
  tagName: "",
  tagCode: "",
  tagLevel: undefined,
  status: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const tagDicts = ref<UserTagDict[]>([]);
const total = ref(0);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();
const formData = reactive<CreateUserTagDictRequest & { tagDictId?: number }>({
  tagName: "",
  tagCode: "",
  tagLevel: 1,
  description: "",
  iconUrl: "",
  triggerCondition: "",
  sortOrder: 0,
  status: 1,
});

// 表单验证规则
const formRules: FormRules = {
  tagName: [{ required: true, message: "请输入标签名称", trigger: "blur" }],
  tagCode: [{ required: true, message: "请输入标签编码", trigger: "blur" }],
  tagLevel: [{ required: true, message: "请选择标签级别", trigger: "change" }],
};

// 获取级别标签类型
const getLevelType = (level: number) => {
  const typeMap: Record<number, string> = {
    1: "danger",
    2: "warning",
    3: "success",
    4: "info",
    5: "",
  };
  return typeMap[level] || "";
};

// 加载标签字典列表
const loadTagDicts = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await queryUserTagDicts(params);
    const page = res?.data;
    tagDicts.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载标签字典列表失败:", error);
    ElMessage.error("加载标签字典列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadTagDicts();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    tagDictId: undefined,
    tagName: "",
    tagCode: "",
    tagLevel: undefined,
    status: undefined,
  });
  handleSearch();
};

// 添加标签
const handleAdd = () => {
  dialogTitle.value = "添加用户标签";
  Object.assign(formData, {
    tagName: "",
    tagCode: "",
    tagLevel: 1,
    description: "",
    iconUrl: "",
    triggerCondition: "",
    sortOrder: 0,
    status: 1,
    tagDictId: undefined,
  });
  dialogVisible.value = true;
};

// 编辑标签
const handleEdit = (row: UserTagDict) => {
  dialogTitle.value = "编辑用户标签";
  Object.assign(formData, {
    tagDictId: row.tagDictId,
    tagName: row.tagName,
    tagCode: row.tagCode,
    tagLevel: row.tagLevel,
    description: row.description,
    iconUrl: row.iconUrl,
    triggerCondition: row.triggerCondition,
    sortOrder: row.sortOrder,
    status: row.status,
  });
  dialogVisible.value = true;
};

// 删除标签
const handleDelete = (row: UserTagDict) => {
  ElMessageBox.confirm(`确定要删除标签"${row.tagName}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteUserTagDict(row.tagDictId);
        ElMessage.success("删除成功");
        loadTagDicts();
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
        if (formData.tagDictId) {
          // 编辑
          const params: UpdateUserTagDictRequest = {
            tagDictId: formData.tagDictId,
            tagName: formData.tagName,
            tagCode: formData.tagCode,
            tagLevel: formData.tagLevel,
            description: formData.description,
            iconUrl: formData.iconUrl,
            triggerCondition: formData.triggerCondition,
            sortOrder: formData.sortOrder,
            status: formData.status,
          };
          await updateUserTagDict(params);
          ElMessage.success("更新成功");
        } else {
          // 添加
          await createUserTagDict(formData);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadTagDicts();
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
  loadTagDicts();
};

// 页码改变
const handleCurrentChange = () => {
  loadTagDicts();
};

// 初始化
onMounted(() => {
  loadTagDicts();
});
</script>

<style scoped>
.user-tags-container {
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
</style>
