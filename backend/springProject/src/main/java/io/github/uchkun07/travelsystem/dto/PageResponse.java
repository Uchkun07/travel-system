package io.github.uchkun07.travelsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应结果
 * @param <T> 数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页响应结果")
public class PageResponse<T> {

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> records;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Long pageNum;

    /**
     * 每页条数
     */
    @Schema(description = "每页条数")
    private Long pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Long totalPages;

    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页")
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    /**
     * 从MyBatis-Plus的Page对象构建
     */
    public static <T> PageResponse<T> of(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        return PageResponse.<T>builder()
                .records(page.getRecords())
                .total(page.getTotal())
                .pageNum(page.getCurrent())
                .pageSize(page.getSize())
                .totalPages(page.getPages())
                .hasPrevious(page.getCurrent() > 1)
                .hasNext(page.getCurrent() < page.getPages())
                .build();
    }
}
