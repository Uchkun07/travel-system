import { defineStore } from "pinia";
import { ref } from "vue";
import { getAllAttractionTypes, type AttractionType } from "@/apis/attraction";

export const useAttractionTypeStore = defineStore("attractionType", () => {
  // 景点类型列表
  const attractionTypes = ref<AttractionType[]>([]);

  // 是否已加载
  const loaded = ref(false);

  // 是否正在加载
  const loading = ref(false);

  /**
   * 获取景点类型列表（带缓存）
   */
  const fetchAttractionTypes = async (forceRefresh = false) => {
    // 如果已加载且不强制刷新，直接返回缓存数据
    if (loaded.value && !forceRefresh) {
      return attractionTypes.value;
    }

    // 如果正在加载，等待加载完成
    if (loading.value) {
      // 等待加载完成后返回数据
      await new Promise((resolve) => {
        const checkLoading = setInterval(() => {
          if (!loading.value) {
            clearInterval(checkLoading);
            resolve(null);
          }
        }, 100);
      });
      return attractionTypes.value;
    }

    loading.value = true;
    try {
      const response = await getAllAttractionTypes();
      if (response.code === 200 && response.data) {
        attractionTypes.value = response.data;
        loaded.value = true;
      }
      return attractionTypes.value;
    } catch (error) {
      console.error("加载景点类型失败:", error);
      throw error;
    } finally {
      loading.value = false;
    }
  };

  /**
   * 清除缓存
   */
  const clearCache = () => {
    attractionTypes.value = [];
    loaded.value = false;
  };

  return {
    attractionTypes,
    loaded,
    loading,
    fetchAttractionTypes,
    clearCache,
  };
});
