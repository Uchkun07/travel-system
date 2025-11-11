import axios from "axios";
import type {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  AxiosError,
} from "axios";
import { ElMessage } from "element-plus";
import Cookies from "js-cookie";

// 响应数据接口
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data?: T;
}

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  timeout: 15000,
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});

// 请求拦截器
service.interceptors.request.use(
  (config: any) => {
    // 从 Cookie 获取 token
    const token = Cookies.get("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    console.error("请求错误:", error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;

    // 如果返回的是Blob类型(文件下载),直接返回
    if (response.config.responseType === "blob") {
      return response;
    }

    // 正常响应直接返回数据
    return res;
  },
  (error: AxiosError) => {
    console.error("响应错误:", error);

    // 处理HTTP错误状态码
    if (error.response) {
      const status = error.response.status;
      const data: any = error.response.data;

      switch (status) {
        case 401:
          ElMessage.error(data.message || "登录已过期,请重新登录");
          // 清除 Cookie 和本地存储
          Cookies.remove("token");
          localStorage.removeItem("user");
          sessionStorage.removeItem("user");
          sessionStorage.removeItem("token");
          // 跳转到登录页
          window.location.href = "/login";
          break;
        case 403:
          ElMessage.error("没有权限访问该资源");
          break;
        case 404:
          ElMessage.error("请求的资源不存在");
          break;
        case 500:
          ElMessage.error(data.message || "服务器内部错误");
          break;
        default:
          // 对于400等其他错误，不在拦截器显示，由业务代码处理
          break;
      }
    } else if (error.request) {
      ElMessage.error("网络错误,请检查您的网络连接");
    } else {
      // 请求配置错误不显示给用户
      console.error("请求配置错误:", error.message);
    }

    return Promise.reject(error);
  }
);

// 封装GET请求
export function get<T = any>(
  url: string,
  params?: any,
  config?: AxiosRequestConfig
): Promise<ApiResponse<T>> {
  return service.get(url, { params, ...config });
}

// 封装POST请求
export function post<T = any>(
  url: string,
  data?: any,
  config?: AxiosRequestConfig
): Promise<ApiResponse<T>> {
  return service.post(url, data, config);
}

// 封装PUT请求
export function put<T = any>(
  url: string,
  data?: any,
  config?: AxiosRequestConfig
): Promise<ApiResponse<T>> {
  return service.put(url, data, config);
}

// 封装DELETE请求
export function del<T = any>(
  url: string,
  params?: any,
  config?: AxiosRequestConfig
): Promise<ApiResponse<T>> {
  return service.delete(url, { params, ...config });
}

export default service;
