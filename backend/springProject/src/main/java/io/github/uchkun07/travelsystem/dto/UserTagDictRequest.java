package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户标签字典请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户标签字典请求")
public class UserTagDictRequest {

    @Schema(description = "标签字典ID(更新时必填)")
    private Integer tagDictId;

    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tagName;

    @NotBlank(message = "标签编码不能为空")
    @Schema(description = "标签编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tagCode;

    @Schema(description = "标签等级(1=初级，2=中级，3=高级)")
    private Integer tagLevel;

    @Schema(description = "触发条件(JSON格式)")
    private String triggerCondition;

    @Schema(description = "标签描述")
    private String description;

    @Schema(description = "标签图标URL")
    private String iconUrl;

    @Schema(description = "标签状态(1=启用，0=禁用)")
    private Integer status;

    @Schema(description = "排序序号")
    private Integer sortOrder;
}
