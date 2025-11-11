package io.github.uchkun07.travelsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统操作日志表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("operation_log")
public class OperationLog {

    /**
     * 操作日志唯一标识（安全审计核心）
     */
    @TableId(value = "operation_log_id", type = IdType.AUTO)
    private Long operationLogId;

    /**
     * 关联管理员表（操作人）
     */
    @TableField("admin_id")
    private Long adminId;

    /**
     * 操作类型（如"景点新增""类型编辑"）
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 操作对象（如"景点""类型""标签"）
     */
    @TableField("operation_object")
    private String operationObject;

    /**
     * 操作对象ID（如景点ID=1001）
     */
    @TableField("object_id")
    private Long objectId;

    /**
     * 操作内容（详细描述）
     */
    @TableField("operation_content")
    private String operationContent;

    /**
     * 操作IP地址
     */
    @TableField("operation_ip")
    private String operationIp;

    /**
     * 操作时间
     */
    @TableField("operation_time")
    private LocalDateTime operationTime;

    /**
     * 日志创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
