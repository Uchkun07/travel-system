// API 响应接口
export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}
// 分页响应接口
export interface PageResponse<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  totalPages: number;
  hasPrevious: boolean;
  hasNext: boolean;
}
