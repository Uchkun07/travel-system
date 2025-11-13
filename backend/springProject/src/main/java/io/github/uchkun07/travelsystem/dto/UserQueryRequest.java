package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户查询请求")
public class UserQueryRequest {

    @Builder.Default
    @Schema(description = "页码", defaultValue = "1")
    private Long pageNum = 1L;

    @Builder.Default
    @Schema(description = "每页大小", defaultValue = "10")
    private Long pageSize = 10L;

    @Schema(description = "用户名(模糊查询)")
    private String username;

    @Schema(description = "昵称(模糊查询)")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户状态")
    private Integer status;

    @Schema(description = "标签字典ID(筛选拥有该标签的用户)")
    private Integer tagDictId;
}
