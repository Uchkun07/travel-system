// stores/collection.ts
import { defineStore } from "pinia";
import { ref, computed } from "vue";
import {
  getCollectedAttractionIds,
  collectAttraction,
  uncollectAttraction,
} from "@/apis/attraction";
import { ElMessage } from "element-plus";

export const useCollectionStore = defineStore(
  "collection",
  () => {
    const collectedIds = ref<Set<number>>(new Set());
    const initialized = ref(false);
    const loading = ref(false);

    // 计算属性：检查是否收藏
    const isCollected = computed(
      () => (id: number) => collectedIds.value.has(id)
    );

    // 获取收藏数量
    const collectionCount = computed(() => collectedIds.value.size);

    // 初始化收藏列表
    const initializeCollections = async () => {
      if (initialized.value || loading.value) return;

      loading.value = true;
      try {
        const response = await getCollectedAttractionIds();
        if (response.code === 200) {
          collectedIds.value = new Set(response.data);
          initialized.value = true;
          console.log("收藏列表初始化完成，收藏数量:", response.data.length);
        }
      } catch (error) {
        console.error("初始化收藏列表失败:", error);
        // 初始化失败不影响页面渲染
      } finally {
        loading.value = false;
      }
    };

    // 收藏景点
    const collect = async (attractionId: number) => {
      try {
        const response = await collectAttraction(attractionId);
        if (response.code === 200) {
          collectedIds.value.add(attractionId);
          ElMessage.success("收藏成功");
          return true;
        } else {
          ElMessage.error(response.message || "收藏失败");
          return false;
        }
      } catch (error: any) {
        ElMessage.error(error?.message || "收藏失败");
        return false;
      }
    };

    // 取消收藏
    const uncollect = async (attractionId: number) => {
      try {
        const response = await uncollectAttraction(attractionId);
        if (response.code === 200) {
          collectedIds.value.delete(attractionId);
          ElMessage.success("已取消收藏");
          return true;
        } else {
          ElMessage.error(response.message || "取消收藏失败");
          return false;
        }
      } catch (error: any) {
        ElMessage.error(error?.message || "取消收藏失败");
        return false;
      }
    };

    // 切换收藏状态
    const toggleCollection = async (attractionId: number) => {
      if (isCollected.value(attractionId)) {
        return await uncollect(attractionId);
      } else {
        return await collect(attractionId);
      }
    };

    // 手动设置收藏状态（用于同步其他页面的操作）
    const setCollectionStatus = (attractionId: number, collected: boolean) => {
      if (collected) {
        collectedIds.value.add(attractionId);
      } else {
        collectedIds.value.delete(attractionId);
      }
    };

    // 清空收藏状态（退出登录时调用）
    const clearCollections = () => {
      collectedIds.value.clear();
      initialized.value = false;
      console.log("收藏状态已清空");
    };

    // 重新加载收藏列表
    const reloadCollections = async () => {
      initialized.value = false;
      await initializeCollections();
    };

    return {
      collectedIds,
      isCollected,
      collectionCount,
      initialized,
      loading,
      initializeCollections,
      collect,
      uncollect,
      toggleCollection,
      setCollectionStatus,
      clearCollections,
      reloadCollections,
    };
  },
  {
    persist: {
      key: "collection",
      storage: localStorage,
      paths: ["collectedIds", "initialized"],
      serializer: {
        serialize: (state) => {
          return JSON.stringify({
            ...state,
            collectedIds: Array.from(state.collectedIds),
          });
        },
        deserialize: (value) => {
          const parsed = JSON.parse(value);
          return {
            ...parsed,
            collectedIds: new Set(parsed.collectedIds || []),
          };
        },
      },
    },
  }
);
