// 导出axios实例和通用方法
export {
  default as request,
  get,
  post,
  put,
  del,
  upload,
  download,
} from "./request";
export type { ApiResponse } from "./request";

// 导出认证相关API
export * from "./auth";
