package io.github.uchkun07.travelsystem.controller;

import io.github.uchkun07.travelsystem.dto.ApiResponse;
import io.github.uchkun07.travelsystem.dto.FileUploadResponse;
import io.github.uchkun07.travelsystem.enums.FileCategory;
import io.github.uchkun07.travelsystem.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    /**
     * 上传头像
     */
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    @PostMapping("/avatar")
    public ApiResponse<FileUploadResponse> uploadAvatar(
            @Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileUploadService.uploadImage(file, FileCategory.AVATAR);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传城市图片
     */
    @Operation(summary = "上传城市图片", description = "上传城市相关图片")
    @PostMapping("/city")
    public ApiResponse<FileUploadResponse> uploadCityImage(
            @Parameter(description = "城市图片文件") @RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileUploadService.uploadImage(file, FileCategory.CITY);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传景点图片
     */
    @Operation(summary = "上传景点图片", description = "上传景点相关图片")
    @PostMapping("/attraction")
    public ApiResponse<FileUploadResponse> uploadAttractionImage(
            @Parameter(description = "景点图片文件") @RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileUploadService.uploadImage(file, FileCategory.ATTRACTION);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传轮播图
     */
    @Operation(summary = "上传轮播图", description = "上传轮播图图片")
    @PostMapping("/slideshow")
    public ApiResponse<FileUploadResponse> uploadSlideshowImage(
            @Parameter(description = "轮播图文件") @RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponse response = fileUploadService.uploadImage(file, FileCategory.SLIDESHOW);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传景点图片
     */
    @Operation(summary = "批量上传景点图片", description = "批量上传景点相关图片")
    @PostMapping("/attraction/batch")
    public ApiResponse<List<FileUploadResponse>> uploadAttractionImages(
            @Parameter(description = "景点图片文件数组") @RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> responses = fileUploadService.uploadImages(files, FileCategory.ATTRACTION);
            return ApiResponse.success(responses);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 批量上传轮播图
     */
    @Operation(summary = "批量上传轮播图", description = "批量上传轮播图图片")
    @PostMapping("/slideshow/batch")
    public ApiResponse<List<FileUploadResponse>> uploadSlideshowImages(
            @Parameter(description = "轮播图文件数组") @RequestParam("files") MultipartFile[] files) {
        try {
            List<FileUploadResponse> responses = fileUploadService.uploadImages(files, FileCategory.SLIDESHOW);
            return ApiResponse.success(responses);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        } catch (RuntimeException e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @Operation(summary = "删除文件", description = "根据URL删除文件")
    @DeleteMapping
    public ApiResponse<Boolean> deleteFile(
            @Parameter(description = "文件URL") @RequestParam("fileUrl") String fileUrl,
            @Parameter(description = "文件分类") @RequestParam("category") String category) {
        try {
            FileCategory fileCategory = FileCategory.valueOf(category.toUpperCase());
            boolean deleted = fileUploadService.deleteFile(fileUrl, fileCategory);
            if (deleted) {
                return ApiResponse.success("文件删除成功", true);
            } else {
                return ApiResponse.error(404, "文件不存在或删除失败");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, "无效的文件分类");
        }
    }
}
