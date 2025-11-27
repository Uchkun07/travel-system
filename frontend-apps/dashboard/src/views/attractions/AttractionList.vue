<template>
  <div class="attraction-list-container">
    <el-card>
      <div class="header">
        <h2>景点列表</h2>
        <el-button type="primary" @click="handleAdd">添加景点</el-button>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="景点ID">
          <el-input
            v-model.number="searchForm.attractionId"
            placeholder="请输入景点ID"
            clearable
          />
        </el-form-item>
        <el-form-item label="景点名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入景点名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="景点类型">
          <el-select
            v-model="searchForm.typeId"
            placeholder="请选择景点类型"
            clearable
          >
            <el-option
              v-for="type in attractionTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="城市">
          <el-select
            v-model="searchForm.cityId"
            placeholder="请选择城市"
            clearable
            filterable
          >
            <el-option
              v-for="city in cities"
              :key="city.cityId"
              :label="city.cityName"
              :value="city.cityId"
            />
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
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 数据表格 -->
      <el-table :data="attractions" border style="width: 100%">
        <el-table-column prop="attractionId" label="景点ID" width="80" />
        <el-table-column prop="name" label="景点名称" width="180" />
        <el-table-column prop="typeName" label="景点类型" width="120" />
        <el-table-column prop="cityName" label="城市" width="120" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="favoriteCount" label="收藏数" width="100" />
        <el-table-column prop="popularityScore" label="人气指数" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)"
              >编辑</el-button
            >
            <el-button type="info" size="small" @click="handleManageTags(row)"
              >管理标签</el-button
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

    <!-- 添加/编辑景点对话框 -->
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
        label-width="100px"
      >
        <el-form-item label="景点名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入景点名称" />
        </el-form-item>
        <el-form-item label="副标题" prop="subtitle">
          <el-input v-model="formData.subtitle" placeholder="请输入副标题" />
        </el-form-item>
        <el-form-item label="景点类型" prop="typeId">
          <el-select
            v-model="formData.typeId"
            placeholder="请选择景点类型"
            style="width: 100%"
          >
            <el-option
              v-for="type in attractionTypes"
              :key="type.typeId"
              :label="type.typeName"
              :value="type.typeId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="城市" prop="cityId">
          <el-select
            v-model="formData.cityId"
            placeholder="请选择城市"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="city in cities"
              :key="city.cityId"
              :label="city.cityName"
              :value="city.cityId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number
            v-model="formData.longitude"
            :precision="6"
            :min="-180"
            :max="180"
            placeholder="范围：-180~180"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number
            v-model="formData.latitude"
            :precision="6"
            :min="-90"
            :max="90"
            placeholder="范围：-90~90"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="主图">
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :on-change="handleMainImageChange"
            :on-remove="handleMainImageRemove"
            :limit="1"
            :file-list="mainImageFileList"
            list-type="picture-card"
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                上传景点主图，支持 jpg/png/gif/webp 格式，文件大小不超过 5MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="多图展示">
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :on-change="handleMultiImageChange"
            :on-remove="handleMultiImageRemove"
            :limit="9"
            :file-list="multiImageFileList"
            list-type="picture-card"
            accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
            multiple
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                上传景点多图，最多9张，支持 jpg/png/gif/webp
                格式，文件大小不超过 5MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="建议游览时间" prop="estimatedPlayTime">
          <el-input-number
            v-model="formData.estimatedPlayTime"
            :min="0"
            placeholder="单位：分钟"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="门票价格" prop="ticketPrice">
          <el-input-number
            v-model="formData.ticketPrice"
            :min="0"
            :precision="2"
            placeholder="单位：元"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="门票说明" prop="ticketDescription">
          <el-input
            v-model="formData.ticketDescription"
            type="textarea"
            :rows="2"
            placeholder="请输入门票说明"
          />
        </el-form-item>
        <el-form-item label="开放时间" prop="openingHours">
          <el-input
            v-model="formData.openingHours"
            placeholder="例如：09:00-18:00"
          />
        </el-form-item>
        <el-form-item label="最佳观光季节" prop="bestSeason">
          <el-input
            v-model="formData.bestSeason"
            placeholder="例如：春季、秋季"
          />
        </el-form-item>
        <el-form-item label="历史底蕴" prop="historicalContext">
          <el-input
            v-model="formData.historicalContext"
            type="textarea"
            :rows="3"
            placeholder="请输入历史底蕴介绍"
          />
        </el-form-item>
        <el-form-item label="安全提示" prop="safetyTips">
          <el-input
            v-model="formData.safetyTips"
            type="textarea"
            :rows="2"
            placeholder="请输入安全提示"
          />
        </el-form-item>
        <el-form-item label="官方网站" prop="officialWebsite">
          <el-input
            v-model="formData.officialWebsite"
            placeholder="请输入官方网站URL"
          />
        </el-form-item>
        <el-form-item label="附近美食" prop="nearbyFood">
          <el-input
            v-model="formData.nearbyFood"
            type="textarea"
            :rows="2"
            placeholder="请输入附近美食信息（JSON格式）"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="formData.auditStatus">
            <el-radio :label="1">待审核</el-radio>
            <el-radio :label="2">已通过</el-radio>
            <el-radio :label="3">已驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 管理标签对话框 -->
    <el-dialog v-model="tagDialogVisible" title="管理景点标签" width="500px">
      <div class="tag-management">
        <el-checkbox-group v-model="selectedTags">
          <el-checkbox
            v-for="tag in allTags"
            :key="tag.tagId"
            :label="tag.tagId"
            :disabled="tag.status === 0"
          >
            {{ tag.tagName }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="tagDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveTags">保存</el-button>
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
  queryAttractions,
  createAttraction,
  updateAttraction,
  deleteAttraction,
  getAttractionTags,
  batchBindTags,
  batchUnbindTags,
  type AttractionListResponse,
  type CreateAttractionRequest,
  type UpdateAttractionRequest,
  type QueryAttractionsRequest,
  queryAttractionTypes,
  type AttractionType,
  queryCities,
  type City,
  getAllTags,
  type AttractionTag,
} from "@/apis/attraction";
import { uploadAttractionImage } from "@/apis/upload";

// 搜索表单
const searchForm = reactive<QueryAttractionsRequest>({
  attractionId: undefined,
  name: "",
  typeId: undefined,
  cityId: undefined,
  status: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const attractions = ref<AttractionListResponse[]>([]);
const total = ref(0);
const attractionTypes = ref<AttractionType[]>([]);
const cities = ref<City[]>([]);
const allTags = ref<AttractionTag[]>([]);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();
const formData = reactive<CreateAttractionRequest & { attractionId?: number }>({
  attractionId: undefined,
  name: "",
  subtitle: "",
  typeId: undefined as any,
  cityId: undefined as any,
  address: "",
  latitude: undefined,
  longitude: undefined,
  mainImageUrl: "",
  multiImageUrls: "",
  estimatedPlayTime: undefined,
  ticketPrice: undefined,
  ticketDescription: "",
  openingHours: "",
  bestSeason: "",
  historicalContext: "",
  safetyTips: "",
  officialWebsite: "",
  nearbyFood: "",
  status: 1,
  auditStatus: 1,
});

// 文件上传
const mainImageFile = ref<File | null>(null);
const mainImageFileList = ref<UploadUserFile[]>([]);
const multiImageFiles = ref<File[]>([]);
const multiImageFileList = ref<UploadUserFile[]>([]);

// 标签管理对话框
const tagDialogVisible = ref(false);
const selectedTags = ref<number[]>([]);
const currentAttractionId = ref<number>(0);

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: "请输入景点名称", trigger: "blur" }],
  typeId: [{ required: true, message: "请选择景点类型", trigger: "change" }],
  cityId: [{ required: true, message: "请选择城市", trigger: "change" }],
  latitude: [
    {
      validator: (rule, value, callback) => {
        if (
          value !== undefined &&
          value !== null &&
          (value < -90 || value > 90)
        ) {
          callback(new Error("纬度范围：-90~90"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  longitude: [
    {
      validator: (rule, value, callback) => {
        if (
          value !== undefined &&
          value !== null &&
          (value < -180 || value > 180)
        ) {
          callback(new Error("经度范围：-180~180"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

// 加载景点类型列表
const loadAttractionTypes = async () => {
  try {
    const res = await queryAttractionTypes();
    const page = res?.data;
    attractionTypes.value = page?.records ?? [];
  } catch (error) {
    console.error("加载景点类型失败:", error);
  }
};

// 加载城市列表
const loadCities = async () => {
  try {
    const res = await queryCities();
    const page = res?.data;
    cities.value = page?.records ?? [];
  } catch (error) {
    console.error("加载城市列表失败:", error);
  }
};

// 加载所有标签
const loadAllTags = async () => {
  try {
    const res = await getAllTags();
    allTags.value = res?.data ?? [];
  } catch (error) {
    console.error("加载标签列表失败:", error);
  }
};

// 加载景点列表
const loadAttractions = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await queryAttractions(params);
    const page = res?.data;
    attractions.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载景点列表失败:", error);
    ElMessage.error("加载景点列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadAttractions();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    attractionId: undefined,
    name: "",
    typeId: undefined,
    cityId: undefined,
    status: undefined,
  });
  handleSearch();
};

// 处理主图变化
const handleMainImageChange = (file: UploadFile) => {
  if (file.raw) {
    // 验证文件大小
    const isLt5M = file.raw.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      ElMessage.error("图片大小不能超过 5MB!");
      mainImageFileList.value = [];
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
      mainImageFileList.value = [];
      return;
    }

    mainImageFile.value = file.raw;
  }
};

// 处理主图移除
const handleMainImageRemove = () => {
  mainImageFile.value = null;
  mainImageFileList.value = [];
};

// 处理多图变化
const handleMultiImageChange = (file: UploadFile) => {
  if (file.raw) {
    // 验证文件大小
    const isLt5M = file.raw.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      ElMessage.error("图片大小不能超过 5MB!");
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
      return;
    }

    multiImageFiles.value.push(file.raw);
  }
};

// 处理多图移除
const handleMultiImageRemove = (file: UploadFile) => {
  const index = multiImageFileList.value.findIndex((f) => f.uid === file.uid);
  if (index > -1) {
    multiImageFiles.value.splice(index, 1);
  }
};

// 添加景点
const handleAdd = () => {
  dialogTitle.value = "添加景点";
  Object.assign(formData, {
    attractionId: undefined,
    name: "",
    subtitle: "",
    typeId: undefined,
    cityId: undefined,
    address: "",
    latitude: undefined,
    longitude: undefined,
    mainImageUrl: "",
    multiImageUrls: "",
    estimatedPlayTime: undefined,
    ticketPrice: undefined,
    ticketDescription: "",
    openingHours: "",
    bestSeason: "",
    historicalContext: "",
    safetyTips: "",
    officialWebsite: "",
    nearbyFood: "",
    status: 1,
    auditStatus: 1,
  });
  mainImageFile.value = null;
  mainImageFileList.value = [];
  multiImageFiles.value = [];
  multiImageFileList.value = [];
  dialogVisible.value = true;
};

// 编辑景点
const handleEdit = (row: AttractionListResponse) => {
  dialogTitle.value = "编辑景点";
  Object.assign(formData, {
    attractionId: row.attractionId,
    name: row.name,
    subtitle: "",
    typeId: row.typeId,
    cityId: row.cityId,
    address: "",
    latitude: undefined,
    longitude: undefined,
    mainImageUrl: "",
    multiImageUrls: "",
    estimatedPlayTime: undefined,
    ticketPrice: undefined,
    ticketDescription: "",
    openingHours: "",
    bestSeason: "",
    historicalContext: "",
    safetyTips: "",
    officialWebsite: "",
    nearbyFood: "",
    status: row.status,
    auditStatus: 1,
  });
  mainImageFile.value = null;
  mainImageFileList.value = [];
  multiImageFiles.value = [];
  multiImageFileList.value = [];
  dialogVisible.value = true;
};

// 删除景点
const handleDelete = (row: AttractionListResponse) => {
  ElMessageBox.confirm(`确定要删除景点"${row.name}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAttraction(row.attractionId);
        ElMessage.success("删除成功");
        loadAttractions();
      } catch (error) {
        console.error("删除失败:", error);
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {});
};

// 管理标签
const handleManageTags = async (row: AttractionListResponse) => {
  currentAttractionId.value = row.attractionId;
  try {
    const res = await getAttractionTags(row.attractionId);
    const tags = res?.data ?? [];
    selectedTags.value = tags.map((tag) => tag.tagId);
    tagDialogVisible.value = true;
  } catch (error) {
    console.error("加载景点标签失败:", error);
    ElMessage.error("加载景点标签失败");
  }
};

// 保存标签
const handleSaveTags = async () => {
  try {
    const res = await getAttractionTags(currentAttractionId.value);
    const currentTags = res?.data ?? [];
    const currentTagIds = currentTags.map((tag) => tag.tagId);

    // 计算需要绑定和解绑的标签
    const tagsToAdd = selectedTags.value.filter(
      (id) => !currentTagIds.includes(id)
    );
    const tagsToRemove = currentTagIds.filter(
      (id) => !selectedTags.value.includes(id)
    );

    // 绑定新标签
    if (tagsToAdd.length > 0) {
      await batchBindTags({
        attractionId: currentAttractionId.value,
        tagIds: tagsToAdd,
      });
    }

    // 解绑标签
    if (tagsToRemove.length > 0) {
      await batchUnbindTags({
        attractionId: currentAttractionId.value,
        tagIds: tagsToRemove,
      });
    }

    ElMessage.success("保存成功");
    tagDialogVisible.value = false;
  } catch (error) {
    console.error("保存标签失败:", error);
    ElMessage.error("保存标签失败");
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 1. 先上传主图
        if (mainImageFile.value) {
          const mainImageRes = await uploadAttractionImage(mainImageFile.value);
          formData.mainImageUrl = mainImageRes?.data?.fileUrl || "";
        }

        // 2. 上传多图
        if (multiImageFiles.value.length > 0) {
          const uploadPromises = multiImageFiles.value.map((file) =>
            uploadAttractionImage(file)
          );
          const results = await Promise.all(uploadPromises);
          const urls = results
            .map((res) => res?.data?.fileUrl)
            .filter((url) => url);
          formData.multiImageUrls = JSON.stringify(urls);
        }

        // 3. 提交表单数据
        if (formData.attractionId) {
          // 编辑
          await updateAttraction(formData as UpdateAttractionRequest);
          ElMessage.success("更新成功");
        } else {
          // 添加
          await createAttraction(formData);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadAttractions();
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
  mainImageFile.value = null;
  mainImageFileList.value = [];
  multiImageFiles.value = [];
  multiImageFileList.value = [];
};

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  pagination.pageNum = page;
  pagination.pageSize = pageSize;
  loadAttractions();
};

// 初始化
onMounted(() => {
  loadAttractionTypes();
  loadCities();
  loadAllTags();
  loadAttractions();
});
</script>

<style scoped>
.attraction-list-container {
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

.tag-management {
  max-height: 400px;
  overflow-y: auto;
}

.tag-management .el-checkbox {
  display: block;
  margin: 10px 0;
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
