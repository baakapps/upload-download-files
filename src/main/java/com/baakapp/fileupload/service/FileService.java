package com.baakapp.fileupload.service;

import com.baakapp.fileupload.entity.FileData;
import com.baakapp.fileupload.model.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    FileUploadResponse upload(MultipartFile multipartFile);

    void deleteFileByName(String fileName);

    FileData getFileByName(String fileName);

    List<FileUploadResponse> getFiles();

    void updateByName(String currentName, String newName);
}
