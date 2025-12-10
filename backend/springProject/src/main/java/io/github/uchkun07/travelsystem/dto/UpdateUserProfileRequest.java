package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户基本信息请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新用户基本信息请求")
public class UpdateUserProfileRequest {

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String fullName;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 性别（0=未知，1=男，2=女）
     */
    @Schema(description = "性别(0=未知,1=男,2=女)")
    private Integer gender;

    /**
     * 出生日期（YYYY-MM-DD）
     */
    @Schema(description = "出生日期(YYYY-MM-DD)")
    private String birthday;

    /**
     * 常驻地址
     */
    @Schema(description = "常驻地址")
    private String residentAddress;
}
