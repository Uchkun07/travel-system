package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.OperationLogQueryRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.mapper.OperationLogMapper;
import io.github.uchkun07.travelsystem.service.IOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class OperationLogServiceImpl implements IOperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    @Transactional
    public void saveLog(Long adminId, String operationType, String operationObject,
                        Long objectId, String operationContent, String operationIp) {
        io.github.uchkun07.travelsystem.entity.OperationLog operationLog = 
            io.github.uchkun07.travelsystem.entity.OperationLog.builder()
                .adminId(adminId)
                .operationType(operationType)
                .operationObject(operationObject)
                .objectId(objectId)
                .operationContent(operationContent)
                .operationIp(operationIp)
                .operationTime(LocalDateTime.now())
                .build();

        operationLogMapper.insert(operationLog);
        log.debug("保存操作日志: adminId={}, type={}, object={}", adminId, operationType, operationObject);
    }

    @Override
    @Transactional
    public void deleteLog(Long operationLogId) {
        io.github.uchkun07.travelsystem.entity.OperationLog operationLog = operationLogMapper.selectById(operationLogId);
        if (operationLog == null) {
            throw new IllegalArgumentException("操作日志不存在");
        }
        operationLogMapper.deleteById(operationLogId);
        log.info("删除操作日志成功: logId={}", operationLogId);
    }

    @Override
    @Transactional
    public void batchDeleteLogs(List<Long> operationLogIds) {
        if (operationLogIds == null || operationLogIds.isEmpty()) {
            throw new IllegalArgumentException("日志ID列表不能为空");
        }
        
        LambdaQueryWrapper<io.github.uchkun07.travelsystem.entity.OperationLog> wrapper = 
            new LambdaQueryWrapper<>();
        wrapper.in(io.github.uchkun07.travelsystem.entity.OperationLog::getOperationLogId, operationLogIds);
        int count = operationLogMapper.delete(wrapper);
        
        log.info("批量删除操作日志成功: 删除{}条", count);
    }

    @Override
    public PageResponse<io.github.uchkun07.travelsystem.entity.OperationLog> queryLogs(OperationLogQueryRequest request) {
        LambdaQueryWrapper<io.github.uchkun07.travelsystem.entity.OperationLog> wrapper = 
            new LambdaQueryWrapper<>();

        if (request.getAdminId() != null) {
            wrapper.eq(io.github.uchkun07.travelsystem.entity.OperationLog::getAdminId, request.getAdminId());
        }
        if (request.getOperationType() != null && !request.getOperationType().trim().isEmpty()) {
            wrapper.like(io.github.uchkun07.travelsystem.entity.OperationLog::getOperationType, 
                        request.getOperationType().trim());
        }
        if (request.getOperationObject() != null && !request.getOperationObject().trim().isEmpty()) {
            wrapper.like(io.github.uchkun07.travelsystem.entity.OperationLog::getOperationObject, 
                        request.getOperationObject().trim());
        }
        if (request.getObjectId() != null) {
            wrapper.eq(io.github.uchkun07.travelsystem.entity.OperationLog::getObjectId, request.getObjectId());
        }
        if (request.getStartTime() != null) {
            wrapper.ge(io.github.uchkun07.travelsystem.entity.OperationLog::getOperationTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            wrapper.le(io.github.uchkun07.travelsystem.entity.OperationLog::getOperationTime, request.getEndTime());
        }

        wrapper.orderByDesc(io.github.uchkun07.travelsystem.entity.OperationLog::getOperationTime);

        Page<io.github.uchkun07.travelsystem.entity.OperationLog> page = 
            new Page<>(request.getPageNum(), request.getPageSize());
        Page<io.github.uchkun07.travelsystem.entity.OperationLog> result = 
            operationLogMapper.selectPage(page, wrapper);
        
        return PageResponse.of(result);
    }
}
