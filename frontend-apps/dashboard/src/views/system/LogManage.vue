<template>
  <div class="log-manage-container">
    <el-card>
      <div class="header">
        <h2>操作日志</h2>
      </div>

      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作者">
          <el-input
            v-model="searchForm.operatorName"
            placeholder="请输入操作者姓名或用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select
            v-model="searchForm.operationType"
            placeholder="请选择操作类型"
            clearable
          >
            <el-option label="创建" value="创建" />
            <el-option label="修改" value="修改" />
            <el-option label="修改密码" value="修改密码" />
            <el-option label="删除" value="删除" />
            <el-option label="批量删除" value="批量删除" />
            <el-option label="绑定" value="绑定" />
            <el-option label="解绑" value="解绑" />
            <el-option label="登录" value="登录" />
            <el-option label="登出" value="登出" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作对象">
          <el-select
            v-model="searchForm.operationObject"
            placeholder="请选择操作对象"
            clearable
          >
            <el-option label="管理员" value="管理员" />
            <el-option label="角色" value="角色" />
            <el-option label="权限" value="权限" />
            <el-option label="角色权限" value="角色权限" />
            <el-option label="管理员角色" value="管理员角色" />
            <el-option label="操作日志" value="操作日志" />
            <el-option label="城市" value="城市" />
            <el-option label="景点" value="景点" />
            <el-option label="轮播图" value="轮播图" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button
            type="danger"
            @click="handleBatchDelete"
            :disabled="selectedLogs.length === 0"
          >
            批量删除
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <!-- 数据表格 -->
      <el-table
        :data="logs"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="operationLogId" label="日志ID" width="80" />
        <el-table-column prop="adminId" label="管理员ID" width="100" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)" size="small">
              {{ row.operationType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationObject" label="操作对象" width="150" />
        <el-table-column prop="objectId" label="对象ID" width="100" />
        <el-table-column
          prop="operationContent"
          label="操作内容"
          width="250"
          show-overflow-tooltip
        />
        <el-table-column prop="operationIp" label="IP地址" width="150" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
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

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">
          {{ currentLog?.operationLogId }}
        </el-descriptions-item>
        <el-descriptions-item label="管理员ID">
          {{ currentLog?.adminId }}
        </el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag
            :type="getOperationTypeTag(currentLog?.operationType || '')"
            size="small"
          >
            {{ currentLog?.operationType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="操作对象">
          {{ currentLog?.operationObject }}
        </el-descriptions-item>
        <el-descriptions-item label="对象ID">
          {{ currentLog?.objectId }}
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">
          {{ currentLog?.operationIp }}
        </el-descriptions-item>
        <el-descriptions-item label="操作时间">
          {{ currentLog?.operationTime }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ currentLog?.createTime }}
        </el-descriptions-item>
        <el-descriptions-item label="操作内容" :span="2">
          {{ currentLog?.operationContent }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 管理员详细信息 -->
      <el-divider content-position="left">
        <span style="font-weight: 600; color: #606266">
          <i class="el-icon-user" style="margin-right: 5px"></i>
          操作者信息
        </span>
      </el-divider>
      <el-descriptions :column="2" border v-if="adminDetail">
        <el-descriptions-item label="用户名">
          {{ adminDetail.username }}
        </el-descriptions-item>
        <el-descriptions-item label="姓名">
          {{ adminDetail.fullName }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ adminDetail.phone || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ adminDetail.email || "未设置" }}
        </el-descriptions-item>
      </el-descriptions>
      <div v-else style="text-align: center; padding: 20px; color: #909399">
        <i class="el-icon-loading"></i> 正在加载管理员信息...
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import Pagination from "@/components/common/Pagination.vue";
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  queryOperationLogs,
  deleteOperationLog,
  batchDeleteOperationLogs,
  getAdminDetail,
  type OperationLog,
  type QueryOperationLogsRequest,
  type Admin,
} from "@/apis/auth";

// 搜索表单
const searchForm = reactive<QueryOperationLogsRequest>({
  operatorName: undefined,
  operationType: undefined,
  operationObject: undefined,
});

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
});

// 数据
const logs = ref<OperationLog[]>([]);
const total = ref(0);
const selectedLogs = ref<OperationLog[]>([]);

// 详情对话框
const detailVisible = ref(false);
const currentLog = ref<OperationLog | null>(null);
const adminDetail = ref<Admin | null>(null);

// 加载日志列表
const loadLogs = async () => {
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await queryOperationLogs(params);
    const page = res?.data;
    logs.value = page?.records ?? [];
    total.value = page?.total ?? 0;
  } catch (error) {
    console.error("加载日志列表失败:", error);
    ElMessage.error("加载日志列表失败");
  }
};

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1;
  loadLogs();
};

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    operatorName: undefined,
    operationType: undefined,
    operationObject: undefined,
  });
  handleSearch();
};

// 查看详情
const handleViewDetail = async (row: OperationLog) => {
  currentLog.value = row;
  adminDetail.value = null; // 重置管理员详情
  detailVisible.value = true;

  // 加载管理员详情
  try {
    const res = await getAdminDetail(row.adminId);
    adminDetail.value = res?.data ?? null;
  } catch (error) {
    console.error("加载管理员详情失败:", error);
    ElMessage.warning("加载管理员详情失败");
  }
};

// 删除日志
const handleDelete = (row: OperationLog) => {
  ElMessageBox.confirm(`确定要删除该操作日志吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteOperationLog(row.operationLogId);
        ElMessage.success("删除成功");
        loadLogs();
      } catch (error) {
        console.error("删除失败:", error);
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {});
};

// 选择变化
const handleSelectionChange = (selection: OperationLog[]) => {
  selectedLogs.value = selection;
};

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedLogs.value.length} 条日志吗？`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(async () => {
      try {
        const ids = selectedLogs.value.map((log) => log.operationLogId);
        await batchDeleteOperationLogs(ids);
        ElMessage.success("批量删除成功");
        loadLogs();
      } catch (error) {
        console.error("批量删除失败:", error);
        ElMessage.error("批量删除失败");
      }
    })
    .catch(() => {});
};

// 分页变化
const handlePageChange = (page: number, pageSize: number) => {
  pagination.pageNum = page;
  pagination.pageSize = pageSize;
  loadLogs();
};

// 获取操作类型标签
const getOperationTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    创建: "success",
    修改: "warning",
    修改密码: "warning",
    删除: "danger",
    批量删除: "danger",
    绑定: "primary",
    解绑: "info",
    登录: "primary",
    登出: "",
  };
  return tagMap[type] || "info";
};

// 初始化
onMounted(() => {
  loadLogs();
});
</script>

<style scoped>
.log-manage-container {
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

.json-content {
  max-height: 200px;
  overflow-y: auto;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  word-break: break-all;
  white-space: pre-wrap;
  font-family: "Courier New", monospace;
  font-size: 12px;
}
</style>
