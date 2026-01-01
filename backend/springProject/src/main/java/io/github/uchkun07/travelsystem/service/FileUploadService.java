package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.FileUploadResponse;
import io.github.uchkun07.travelsystem.enums.FileCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
public class FileUploadService {
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${file.upload.city-dir:C:/cities}")
    private String cityUploadDir;
    
    @Value("${file.upload.slideshow-dir:C:/slideshows}")
    private String slideshowUploadDir;
    
    /**
     * 允许上传的图片格式
     */
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );
    
    /**
     * 最大文件大小 (5MB)
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    /**
     * 上传图片文件
     *
     * @param file 文件
     * @param category 文件分类
     * @return 文件上传响应
     * @throws IOException IO异常
     */
    public FileUploadResponse uploadImage(MultipartFile file, FileCategory category) throws IOException {
        // 验证文件
        validateFile(file);
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("不支持的文件格式,仅支持: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }
        
        // 生成新文件名 (日期_UUID.扩展名)
        String datePrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String newFileName = datePrefix + "_" + UUID.randomUUID().toString().replace("-", "") + "." + extension;
        
        // 获取上传目录路径
        String uploadDir = getUploadDirectory(category);
        Path uploadPath = Paths.get(uploadDir);
        
        // 创建目录(如果不存在)
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 保存文件
        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 生成访问URL
        String fileUrl = "/" + category.getPath() + "/" + newFileName;
        
        // 返回响应
        return new FileUploadResponse(
                newFileName,
                fileUrl,
                file.getSize(),
                file.getContentType()
        );
    }
    
    /**
     * 批量上传图片
     *
     * @param files 文件数组
     * @param category 文件分类
     * @return 文件上传响应列表
     * @throws IOException IO异常
     */
    public List<FileUploadResponse> uploadImages(MultipartFile[] files, FileCategory category) throws IOException {
        return Arrays.stream(files)
                .map(file -> {
                    try {
                        return uploadImage(file, category);
                    } catch (IOException e) {
                        throw new RuntimeException("文件上传失败: " + file.getOriginalFilename(), e);
                    }
                })
                .toList();
    }
    
    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     * @param category 文件分类
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl, FileCategory category) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            // 构建文件路径
            String uploadDir = getUploadDirectory(category);
            Path filePath = Paths.get(uploadDir, fileName);
            
            // 删除文件
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 验证文件
     *
     * @param file 文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过 5MB");
        }
    }
    
    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 扩展名
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
    
    /**
     * 获取上传目录
     *
     * @param category 文件分类
     * @return 上传目录路径
     */
    private String getUploadDirectory(FileCategory category) {
        // 根据分类返回不同的上传目录
        switch (category) {
            case CITY:
                return cityUploadDir;
            case SLIDESHOW:
                return slideshowUploadDir;
            default:
                return "C:/uploads/" + category.getPath();
        }
    }
}
