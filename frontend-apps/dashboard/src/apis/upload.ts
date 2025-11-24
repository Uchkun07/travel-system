import request from "./request";

/**
 * 文件上传响应数据
 */
export interface FileUploadResponse {
  fileName: string;
  fileUrl: string;
  fileSize: number;
  fileType: string;
}

/**
 * 上传头像
 */
export const uploadAvatar = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post<FileUploadResponse>("/api/upload/avatar", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

/**
 * 上传城市图片
 */
export const uploadCityImage = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post<FileUploadResponse>("/api/upload/city", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

/**
 * 上传景点图片
 */
export const uploadAttractionImage = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post<FileUploadResponse>("/api/upload/attraction", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

/**
 * 上传轮播图图片
 */
export const uploadSlideshowImage = (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  return request.post<FileUploadResponse>("/api/upload/slideshow", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};

/**
 * 批量上传景点图片
 */
export const uploadAttractionImages = (files: File[]) => {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append("files", file);
  });
  return request.post<FileUploadResponse[]>(
    "/api/upload/attraction/batch",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
};

/**
 * 批量上传轮播图图片
 */
export const uploadSlideshowImages = (files: File[]) => {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append("files", file);
  });
  return request.post<FileUploadResponse[]>(
    "/api/upload/slideshow/batch",
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );
};

/**
 * 删除文件
 */
export const deleteFile = (fileUrl: string) => {
  return request.delete("/api/upload", {
    params: { fileUrl },
  });
};
