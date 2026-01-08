<template>
  <div class="city-list-container">
    <el-card>
      <div class="header">
        <h2>城市管理</h2>
        <el-button type="primary" @click="handleAdd">添加城市</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="城市名称">
          <el-input
            v-model="searchForm.cityName"
            placeholder="请输入城市名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="所属国家">
          <el-input
            v-model="searchForm.country"
            placeholder="请输入国家名称"
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
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 数据表格 -->
      <el-table :data="cities" border style="width: 100%">
        <el-table-column prop="cityId" label="城市ID" width="80" />
        <el-table-column prop="cityName" label="城市名称" width="150" />
        <el-table-column prop="country" label="所属国家" width="120" />
        <el-table-column prop="cityUrl" label="城市图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.cityUrl"
              :src="getImageUrl(row.cityUrl)"
              :preview-src-list="[getImageUrl(row.cityUrl)]"
              fit="cover"
              style="width: 60px; height: 60px"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="description"
          label="城市简介"
          width="200"
          show-overflow-tooltip
        />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
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
        <el-form-item label="所属国家" prop="country">
          <el-input v-model="formData.country" placeholder="请输入国家名称" />
        </el-form-item>
        <el-form-item label="城市图片">
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :limit="1"
            :file-list="fileList"
            list-type="picture-card"
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                支持 jpg/png/gif/webp 格式，文件大小不超过 5MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="城市简介" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入城市简介"
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
import Pagination from "@/components/common/Pagination.vue";
import { ref, reactive, onMounted } from "vue";
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
  type UploadFile,
  type UploadUserFile,
} from "element-plus";
import { Plus } from "@element-plus/icons-vue";
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
import { uploadCityImage } from "@/apis/upload";

// 图片URL转换工具函数
const getImageUrl = (url?: string): string => {
  if (!url) return "";
  // 如果已经是完整URL，直接返回
  if (url.startsWith("http://") || url.startsWith("https://")) {
    return url;
  }
  // 否则拼接baseURL
  const baseURL =
    import.meta.env.VITE_API_BASE_URL || "http://8.146.237.23:8080";
  return `${baseURL}${url.startsWith("/") ? url : "/" + url}`;
};

// 搜索表单
const searchForm = reactive<QueryCitiesRequest>({
  cityName: "",
  country: "",
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
  country: "",
  cityUrl: "",
  description: "",
  sortOrder: 0,
  status: 1,
});

// 文件上传
const uploadedFile = ref<File | null>(null);
const fileList = ref<UploadUserFile[]>([]);

// 表单验证规则
const formRules: FormRules = {
  cityName: [{ required: true, message: "请输入城市名称", trigger: "blur" }],
  country: [{ required: true, message: "请输入所属国家", trigger: "blur" }],
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
    cityName: "",
    country: "",
    status: undefined,
  });
  handleSearch();
};

// 处理图片变化
const handleImageChange = async (file: UploadFile) => {
  if (file.raw) {
    // 验证文件大小
    const isLt5M = file.raw.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      ElMessage.error("图片大小不能超过 5MB!");
      fileList.value = [];
      return;
    }

    // 验证文件类型
    const allowedTypes = [
      "image/jpeg",
      "image/jpg",
      "image/png",
      "image/gif",
      "image/webp",
    ];
    if (!allowedTypes.includes(file.raw.type)) {
      ElMessage.error("只支持 jpg/png/gif/webp 格式的图片!");
      fileList.value = [];
      return;
    }

    uploadedFile.value = file.raw;

    // 立即上传图片
    try {
      const res = await uploadCityImage(file.raw);
      if (res?.data?.fileUrl) {
        formData.cityUrl = res.data.fileUrl;
        ElMessage.success("图片上传成功");
      }
    } catch (error) {
      console.error("图片上传失败:", error);
      ElMessage.error("图片上传失败");
      fileList.value = [];
      uploadedFile.value = null;
    }
  }
};

// 处理图片移除
const handleImageRemove = () => {
  uploadedFile.value = null;
  fileList.value = [];
  formData.cityUrl = "";
};

// 添加城市
const handleAdd = () => {
  dialogTitle.value = "添加城市";
  Object.assign(formData, {
    cityName: "",
    country: "",
    cityUrl: "",
    description: "",
    sortOrder: 0,
    status: 1,
    cityId: undefined,
  });
  uploadedFile.value = null;
  fileList.value = [];
  dialogVisible.value = true;
};

// 编辑城市
const handleEdit = (row: City) => {
  dialogTitle.value = "编辑城市";
  Object.assign(formData, {
    cityId: row.cityId,
    cityName: row.cityName,
    country: row.country,
    cityUrl: row.cityUrl,
    description: row.description,
    sortOrder: row.sortOrder,
    status: row.status,
  });
  uploadedFile.value = null;
  fileList.value = [];
  // 如果有图片URL，显示在上传列表中
  if (row.cityUrl) {
    fileList.value = [
      {
        name: "city-image",
        url: getImageUrl(row.cityUrl),
      },
    ];
  }
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
            country: formData.country,
            cityUrl: formData.cityUrl,
            description: formData.description,
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
  uploadedFile.value = null;
  fileList.value = [];
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

.upload-demo {
  width: 100%;
}

.el-upload__tip {
  margin-top: 5px;
  color: #999;
  font-size: 12px;
}
</style>
