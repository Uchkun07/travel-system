<!-- components/common/DwPagination.vue -->
<template>
  <div class="pagination-container">
    <div class="pagination-info">
      共 {{ total }} 条，每页 {{ internalPageSize }} 条
    </div>
    <ElPagination
      class="pagination"
      :page-size="internalPageSize"
      :current-page="internalCurrentPage"
      :page-count="totalPages"
      background
      layout="prev, pager, next"
      @current-change="handleCurrentChange"
    />
    <div class="pagination-jump">
      <span class="jump-text">跳至</span>
      <ElInput
        v-model.number="jumpPageInput"
        class="jump-input"
        size="small"
        @keyup.enter="handleJump"
        :min="1"
        :max="totalPages"
        type="number"
      />
      <span class="page-text">页</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElInput, ElPagination } from "element-plus";
import { ref, toRefs, watch } from "vue";

interface Props {
  total: number; // 数据总条数
  totalPages: number; // 总页数
  currentPage: number; // 当前页码
  pageSize: number; // 每页显示的数据条数
}

const props = defineProps<Props>();
const { total, totalPages, currentPage, pageSize } = toRefs(props);

const emit = defineEmits<{
  "update:currentPage": [value: number]; // 页码变化事件，参数为新的页码
  "update:pageSize": [value: number]; // 每页数据条数变化事件，参数为新的每页数据条数
  "page-change": [page: number, pageSize: number]; // 分页变化事件，参数为新的页码和每页数据条数
}>();

// 使用内部变量来管理状态
const internalCurrentPage = ref(currentPage.value);
const internalPageSize = ref(pageSize.value);
const jumpPageInput = ref(currentPage.value);

// 监听 currentPage 属性的变化：
watch(currentPage, (newPage) => {
  internalCurrentPage.value = newPage;
  jumpPageInput.value = newPage;
});
// 监听 pageSize 属性的变化：
watch(pageSize, (newSize) => {
  internalPageSize.value = newSize;
});

/** 处理当前页码变化 */
const handleCurrentChange = (newPage: number) => {
  internalCurrentPage.value = newPage;
  jumpPageInput.value = newPage;
  emit("update:currentPage", newPage);
  emit("page-change", newPage, internalPageSize.value);
};

/** 处理跳转页码 */
const handleJump = () => {
  // 验证跳转页码的有效性
  let targetPage = jumpPageInput.value;
  const maxPage = Math.max(1, totalPages.value || 1);
  if (!targetPage || targetPage < 1) {
    targetPage = 1;
  } else if (targetPage > maxPage) {
    targetPage = maxPage;
  }

  // 更新页码
  internalCurrentPage.value = targetPage;
  jumpPageInput.value = targetPage;
  emit("update:currentPage", targetPage);
  emit("page-change", targetPage, internalPageSize.value);
};
</script>

<style scoped lang="scss">
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  margin-bottom: 36px;
  gap: 10px;
}

.pagination-info {
  display: flex;
  width: 166.047px;
  height: 30px;
  flex-direction: column;
  justify-content: center;
  color: #000;
  text-align: center;
  font-family: Inter;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
}

.pagination-jump {
  display: flex;
  align-items: center;
}

.jump-text,
.page-text {
  color: #000;
  font-family: Inter;
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: normal;
  margin: 0 8px;
}

.jump-input {
  width: 60px;
  height: 28px;
}

.jump-input :deep(.el-input__wrapper) {
  border-radius: 4px;
}

.jump-input :deep(.el-input__inner) {
  height: 28px;
  text-align: center;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>
