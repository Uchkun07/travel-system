package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.BrowseRecordRequest;
import io.github.uchkun07.travelsystem.service.IUserBrowseRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户浏览记录控制器
 */
@Slf4j
@Tag(name = "浏览记录埋点", description = "用户浏览景点行为记录接口")
@RestController
@RequestMapping("/api/browse")
public class BrowseRecordController {

    @Autowired
    private IUserBrowseRecordService userBrowseRecordService;

    /**
     * 记录浏览行为
     */
    @Operation(summary = "记录浏览行为", description = "记录用户浏览景点的行为，同一用户短时间内重复浏览同一景点会累加浏览时长")
    @PostMapping("/record")
    public ApiResponse<String> recordBrowse(@Valid @RequestBody BrowseRecordRequest request) {
        log.info("接收浏览埋点请求 - 用户: {}, 景点: {}, 时长: {}秒",
                request.getUserId(), request.getAttractionId(), request.getBrowseDuration());

        try {
            userBrowseRecordService.recordBrowse(request);
            return ApiResponse.success("浏览记录保存成功");
        } catch (Exception e) {
            log.error("保存浏览记录失败", e);
            return ApiResponse.error("浏览记录保存失败: " + e.getMessage());
        }
    }
}
