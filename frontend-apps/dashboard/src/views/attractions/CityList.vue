<template>
  <div class="city-list-container">
    <el-card>
      <div class="header">
        <h2>城市管理</h2>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="城市ID">
          <el-input
            v-model.number="searchForm.cityId"
            placeholder="请输入城市ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="城市名称">
          <el-input
            v-model="searchForm.cityName"
            placeholder="请输入城市名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="省份代码">
          <el-input
            v-model="searchForm.provinceCode"
            placeholder="请输入省份代码"
            clearable
          />
        </el-form-item>
        <el-form-item label="级别">
          <el-select
            v-model="searchForm.level"
            placeholder="请选择级别"
            clearable
          >
            <el-option label="一线城市" :value="1" />
            <el-option label="二线城市" :value="2" />
            <el-option label="三线城市" :value="3" />
            <el-option label="四线城市" :value="4" />
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
          <el-button type="primary" @click="handleAdd">添加城市</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 数据表格 -->
      <el-table :data="cities" border style="width: 100%">
        <el-table-column prop="cityId" label="城市ID" width="80" />
        <el-table-column prop="cityName" label="城市名称" width="150" />
        <el-table-column prop="provinceCode" label="省份代码" width="120" />
        <el-table-column prop="cityCode" label="城市代码" width="120" />
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
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
      <Pagination
        :total="total"
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total-pages="Math.ceil(total / pagination.pageSize)"
        @page-change="handlePageChange"
      />
    </el-card>

    <!-- 添加/编辑城市对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        :model="formData"
        :rules="formRules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="城市名称" prop="cityName">
          <el-input v-model="formData.cityName" placeholder="请输入城市名称" />
        </el-form-item>
        <el-form-item label="省份代码" prop="provinceCode">
          <el-input
            v-model="formData.provinceCode"
            placeholder="请输入省份代码"
          />
        </el-form-item>
        <el-form-item label="城市代码" prop="cityCode">
          <el-input v-model="formData.cityCode" placeholder="请输入城市代码" />
        </el-form-item>
        <el-form-item label="级别" prop="level">
          <el-select
            v-model="formData.level"
            placeholder="请选择级别"
            style="width: 100%"
          >
            <el-option label="一线城市" :value="1" />
            <el-option label="二线城市" :value="2" />
            <el-option label="三线城市" :value="3" />
            <el-option label="四线城市" :value="4" />
          </el-select>
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
import Pagination from "@/components/common/Pagination.vue";
import { ref, reactive, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from "element-plus";
import {
  queryCities,
  createCity,
  updateCity,
  deleteCity,
  type City,
  type CreateCityRequest,
  type UpdateCityRequest,
  type QueryCitiesRequest,
} from "@/apis/attraction";

// 搜索表单
const searchForm = reactive<QueryCitiesRequest>({
  cityId: undefined,
  cityName: "",
  provinceCode: "",
  level: undefined,
  status: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const cities = ref<City[]>([]);
const total = ref(0);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();
const formData = reactive<CreateCityRequest & { cityId?: number }>({
  cityName: "",
  provinceCode: "",
  cityCode: "",
  level: 3,
  sortOrder: 0,
  status: 1,
});

// 表单验证规则
const formRules: FormRules = {
  cityName: [{ required: true, message: "请输入城市名称", trigger: "blur" }],
  provinceCode: [
    { required: true, message: "请输入省份代码", trigger: "blur" },
  ],
  cityCode: [{ required: true, message: "请输入城市代码", trigger: "blur" }],
  level: [{ required: true, message: "请选择级别", trigger: "change" }],
};

// 获取级别文本
const getLevelText = (level: number) => {
  const levelMap: Record<number, string> = {
    1: "一线城市",
    2: "二线城市",
    3: "三线城市",
    4: "四线城市",
  };
  return levelMap[level] || "未知";
};

// 获取级别标签类型
const getLevelType = (level: number) => {
  const typeMap: Record<number, string> = {
    1: "danger",
    2: "warning",
    3: "success",
    4: "info",
  };
  return typeMap[level] || "";
};

// 加载城市列表
const loadCities = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await queryCities(params);
    const page = res?.data;
    cities.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载城市列表失败:", error);
    ElMessage.error("加载城市列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadCities();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    cityId: undefined,
    cityName: "",
    provinceCode: "",
    level: undefined,
    status: undefined,
  });
  handleSearch();
};

// 添加城市
const handleAdd = () => {
  dialogTitle.value = "添加城市";
  Object.assign(formData, {
    cityName: "",
    provinceCode: "",
    cityCode: "",
    level: 3,
    sortOrder: 0,
    status: 1,
    cityId: undefined,
  });
  dialogVisible.value = true;
};

// 编辑城市
const handleEdit = (row: City) => {
  dialogTitle.value = "编辑城市";
  Object.assign(formData, {
    cityId: row.cityId,
    cityName: row.cityName,
    provinceCode: row.provinceCode,
    cityCode: row.cityCode,
    level: row.level,
    sortOrder: row.sortOrder,
    status: row.status,
  });
  dialogVisible.value = true;
};

// 删除城市
const handleDelete = (row: City) => {
  ElMessageBox.confirm(`确定要删除城市"${row.cityName}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteCity(row.cityId);
        ElMessage.success("删除成功");
        loadCities();
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
        if (formData.cityId) {
          // 编辑
          const params: UpdateCityRequest = {
            cityId: formData.cityId,
            cityName: formData.cityName,
            provinceCode: formData.provinceCode,
            cityCode: formData.cityCode,
            level: formData.level,
            sortOrder: formData.sortOrder,
            status: formData.status,
          };
          await updateCity(params);
          ElMessage.success("更新成功");
        } else {
          // 添加
          await createCity(formData);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadCities();
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

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  pagination.pageNum = page;
  pagination.pageSize = pageSize;
  loadCities();
};

// 初始化
onMounted(() => {
  loadCities();
});
</script>

<style scoped>
.city-list-container {
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
