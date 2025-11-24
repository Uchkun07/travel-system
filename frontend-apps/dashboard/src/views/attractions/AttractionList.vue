<template>
  <div class="attraction-list-container">
    <el-card>
      <div class="header">
        <h2>景点列表</h2>
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
          <el-button type="primary" @click="handleAdd">添加景点</el-button>
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
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述"
          />
        </el-form-item>
        <el-form-item label="开放时间" prop="openingHours">
          <el-input
            v-model="formData.openingHours"
            placeholder="例如：09:00-18:00"
          />
        </el-form-item>
        <el-form-item label="门票价格" prop="ticketPrice">
          <el-input-number
            v-model="formData.ticketPrice"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="formData.contact" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="经度" prop="longitude">
          <el-input-number
            v-model="formData.longitude"
            :precision="6"
            :min="-180"
            :max="180"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="纬度" prop="latitude">
          <el-input-number
            v-model="formData.latitude"
            :precision="6"
            :min="-90"
            :max="90"
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
} from "element-plus";
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
  name: "",
  typeId: undefined as any,
  cityId: undefined as any,
  address: "",
  description: "",
  openingHours: "",
  ticketPrice: 0,
  contact: "",
  longitude: undefined,
  latitude: undefined,
  status: 1,
});

// 标签管理对话框
const tagDialogVisible = ref(false);
const selectedTags = ref<number[]>([]);
const currentAttractionId = ref<number>(0);

// 表单验证规则
const formRules: FormRules = {
  name: [{ required: true, message: "请输入景点名称", trigger: "blur" }],
  typeId: [{ required: true, message: "请选择景点类型", trigger: "change" }],
  cityId: [{ required: true, message: "请选择城市", trigger: "change" }],
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

// 添加景点
const handleAdd = () => {
  dialogTitle.value = "添加景点";
  Object.assign(formData, {
    name: "",
    typeId: undefined,
    cityId: undefined,
    address: "",
    description: "",
    openingHours: "",
    ticketPrice: 0,
    contact: "",
    longitude: undefined,
    latitude: undefined,
    status: 1,
    attractionId: undefined,
  });
  dialogVisible.value = true;
};

// 编辑景点
const handleEdit = (row: AttractionListResponse) => {
  dialogTitle.value = "编辑景点";
  Object.assign(formData, {
    attractionId: row.attractionId,
    name: row.name,
    typeId: row.typeId,
    cityId: row.cityId,
    status: row.status,
    address: "",
    description: "",
    openingHours: "",
    ticketPrice: 0,
    contact: "",
    longitude: undefined,
    latitude: undefined,
  });
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
        if (formData.attractionId) {
          // 编辑
          const params: UpdateAttractionRequest = {
            attractionId: formData.attractionId,
            name: formData.name,
            typeId: formData.typeId,
            cityId: formData.cityId,
            address: formData.address,
            description: formData.description,
            openingHours: formData.openingHours,
            ticketPrice: formData.ticketPrice,
            contact: formData.contact,
            longitude: formData.longitude,
            latitude: formData.latitude,
            status: formData.status,
          };
          await updateAttraction(params);
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
</style>
