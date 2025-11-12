import { get, del } from "./request";

// 操作日志接口定义
export interface OperationLog {
  operationLogId: number;
  adminId: number;
  operationType: string;
  operationObject: string;
  objectId: number;
  operationContent: string;
  operationIp: string;
  operationTime: string;
  createTime: string;
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

// 分页查询操作日志请求
export interface QueryOperationLogsRequest {
  pageNum?: number;
  pageSize?: number;
  adminId?: number;
  operationType?: string;
  operationObject?: string;
  objectId?: number;
  startTime?: string;
  endTime?: string;
}

// 分页查询操作日志
export function queryOperationLogs(params?: QueryOperationLogsRequest) {
  return get<PageResponse<OperationLog>>("/admin/operation-log/list", params);
}

// 删除操作日志
export function deleteOperationLog(operationLogId: number) {
  return del<void>(`/admin/operation-log/delete/${operationLogId}`);
}

// 批量删除操作日志
export function batchDeleteOperationLogs(operationLogIds: number[]) {
  return del<void>("/admin/operation-log/batch-delete", {
    data: operationLogIds,
  });
}
