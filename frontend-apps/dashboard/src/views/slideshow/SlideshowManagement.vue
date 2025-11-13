<template>
  <div class="slideshow-management-container">
    <el-card>
      <div class="header">
        <h2>轮播图管理</h2>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入标题"
            clearable
          />
        </el-form-item>
        <el-form-item label="关联景点">
          <el-select
            v-model="searchForm.attractionId"
            placeholder="请选择景点"
            clearable
            filterable
          >
            <el-option
              v-for="attraction in attractions"
              :key="attraction.attractionId"
              :label="attraction.name"
              :value="attraction.attractionId"
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
          <el-button type="primary" @click="handleAdd">添加轮播图</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="slideshows" border style="width: 100%">
        <el-table-column prop="slideshowId" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="180" />
        <el-table-column prop="subtitle" label="副标题" width="180" />
        <el-table-column prop="imageUrl" label="图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              :preview-src-list="[row.imageUrl]"
              fit="cover"
              style="width: 80px; height: 50px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="attractionId" label="关联景点ID" width="120" />
        <el-table-column prop="displayOrder" label="显示顺序" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? "启用" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="clickCount" label="点击量" width="100" />
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

    <!-- 添加/编辑轮播图对话框 -->
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="副标题" prop="subtitle">
          <el-input v-model="formData.subtitle" placeholder="请输入副标题" />
        </el-form-item>
        <el-form-item label="图片URL" prop="imageUrl">
          <el-input v-model="formData.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="图片预览">
          <el-image
            v-if="formData.imageUrl"
            :src="formData.imageUrl"
            fit="cover"
            style="width: 200px; height: 120px; border-radius: 4px"
          />
        </el-form-item>
        <el-form-item label="关联景点" prop="attractionId">
          <el-select
            v-model="formData.attractionId"
            placeholder="请选择关联景点（可选）"
            clearable
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="attraction in attractions"
              :key="attraction.attractionId"
              :label="attraction.name"
              :value="attraction.attractionId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="显示顺序" prop="displayOrder">
          <el-input-number
            v-model="formData.displayOrder"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
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
  querySlideshows,
  createSlideshow,
  updateSlideshow,
  deleteSlideshow,
  type Slideshow,
  type CreateSlideshowRequest,
  type UpdateSlideshowRequest,
  type QuerySlideshowsRequest,
} from "@/apis/slideshow";
import {
  queryAttractions,
  type AttractionListResponse,
} from "@/apis/attraction";

// 搜索表单
const searchForm = reactive<QuerySlideshowsRequest>({
  title: "",
  status: undefined,
  attractionId: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const slideshows = ref<Slideshow[]>([]);
const total = ref(0);
const attractions = ref<AttractionListResponse[]>([]);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("");
const formRef = ref<FormInstance>();
const formData = reactive<CreateSlideshowRequest & { slideshowId?: number }>({
  title: "",
  subtitle: "",
  imageUrl: "",
  attractionId: undefined,
  displayOrder: 0,
  status: 1,
  startTime: undefined,
  endTime: undefined,
});

// 表单验证规则
const formRules: FormRules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  imageUrl: [{ required: true, message: "请输入图片URL", trigger: "blur" }],
};

// 加载景点列表（用于下拉选择）
const loadAttractions = async () => {
  try {
    const res = await queryAttractions({ pageNum: 1, pageSize: 1000 });
    const page = res?.data;
    attractions.value = page?.records ?? [];
  } catch (error) {
    console.error("加载景点列表失败:", error);
  }
};

// 加载轮播图列表
const loadSlideshows = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await querySlideshows(params);
    const page = res?.data;
    slideshows.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载轮播图列表失败:", error);
    ElMessage.error("加载轮播图列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadSlideshows();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    title: "",
    status: undefined,
    attractionId: undefined,
  });
  handleSearch();
};

// 添加轮播图
const handleAdd = () => {
  dialogTitle.value = "添加轮播图";
  Object.assign(formData, {
    title: "",
    subtitle: "",
    imageUrl: "",
    attractionId: undefined,
    displayOrder: 0,
    status: 1,
    startTime: undefined,
    endTime: undefined,
    slideshowId: undefined,
  });
  dialogVisible.value = true;
};

// 编辑轮播图
const handleEdit = (row: Slideshow) => {
  dialogTitle.value = "编辑轮播图";
  Object.assign(formData, {
    slideshowId: row.slideshowId,
    title: row.title,
    subtitle: row.subtitle,
    imageUrl: row.imageUrl,
    attractionId: row.attractionId,
    displayOrder: row.displayOrder,
    status: row.status,
    startTime: row.startTime,
    endTime: row.endTime,
  });
  dialogVisible.value = true;
};

// 删除轮播图
const handleDelete = (row: Slideshow) => {
  ElMessageBox.confirm(`确定要删除轮播图"${row.title}"吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteSlideshow(row.slideshowId);
        ElMessage.success("删除成功");
        loadSlideshows();
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
        if (formData.slideshowId) {
          // 编辑
          const params: UpdateSlideshowRequest = {
            slideshowId: formData.slideshowId,
            title: formData.title,
            subtitle: formData.subtitle,
            imageUrl: formData.imageUrl,
            attractionId: formData.attractionId,
            displayOrder: formData.displayOrder,
            status: formData.status,
            startTime: formData.startTime,
            endTime: formData.endTime,
          };
          await updateSlideshow(params);
          ElMessage.success("更新成功");
        } else {
          // 添加
          await createSlideshow(formData);
          ElMessage.success("创建成功");
        }
        dialogVisible.value = false;
        loadSlideshows();
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
  loadSlideshows();
};

// 页码改变
const handleCurrentChange = () => {
  loadSlideshows();
};

// 初始化
onMounted(() => {
  loadAttractions();
  loadSlideshows();
});
</script>

<style scoped>
.slideshow-management-container {
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
