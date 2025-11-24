package io.github.uchkun07.travelsystem.dto;

/**
 * 文件上传响应DTO
 */
public class FileUploadResponse {
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
    
    /**
     * 文件大小(字节)
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;

    public FileUploadResponse() {
    }

    public FileUploadResponse(String fileName, String fileUrl, Long fileSize, String fileType) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
