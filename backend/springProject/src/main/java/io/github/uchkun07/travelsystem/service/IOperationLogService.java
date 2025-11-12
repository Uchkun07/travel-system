package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.OperationLogQueryRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;

import java.util.List;

public interface IOperationLogService {

    void saveLog(Long adminId, String operationType, String operationObject, 
                 Long objectId, String operationContent, String operationIp);

    void deleteLog(Long operationLogId);

    void batchDeleteLogs(List<Long> operationLogIds);

    PageResponse<io.github.uchkun07.travelsystem.entity.OperationLog> queryLogs(OperationLogQueryRequest request);
}
