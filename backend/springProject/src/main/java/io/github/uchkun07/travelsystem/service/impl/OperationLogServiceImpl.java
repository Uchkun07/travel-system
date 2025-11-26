package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.uchkun07.travelsystem.dto.OperationLogQueryRequest;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.mapper.AdminMapper;
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
    
    @Autowired
    private AdminMapper adminMapper;

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
        // 如果提供了操作者姓名,需要通过admin表查询对应的adminId列表
        if (request.getOperatorName() != null && !request.getOperatorName().trim().isEmpty()) {
            LambdaQueryWrapper<io.github.uchkun07.travelsystem.entity.Admin> adminWrapper = 
                new LambdaQueryWrapper<>();
            adminWrapper.like(io.github.uchkun07.travelsystem.entity.Admin::getFullName, 
                            request.getOperatorName().trim())
                       .or()
                       .like(io.github.uchkun07.travelsystem.entity.Admin::getUsername, 
                            request.getOperatorName().trim());
            List<io.github.uchkun07.travelsystem.entity.Admin> admins = 
                adminMapper.selectList(adminWrapper);
            if (admins.isEmpty()) {
                // 如果没有匹配的管理员,返回空结果
                return PageResponse.<io.github.uchkun07.travelsystem.entity.OperationLog>builder()
                    .records(List.of())
                    .total(0L)
                    .pageNum(request.getPageNum())
                    .pageSize(request.getPageSize())
                    .totalPages(0L)
                    .build();
            }
            List<Long> adminIds = admins.stream()
                .map(io.github.uchkun07.travelsystem.entity.Admin::getAdminId)
                .toList();
            wrapper.in(io.github.uchkun07.travelsystem.entity.OperationLog::getAdminId, adminIds);
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
