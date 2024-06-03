package com.baakapp.fileupload.util;

import com.baakapp.fileupload.model.exception.FileUploadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Component
public class FileValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/png", "image/jpeg", "image/jpg",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // docx
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // xlsx
    );
    public void validate(MultipartFile file) {
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("File size exceeds the limit of 5MB");
        }

        // Check file type
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new FileUploadException("File type is not supported");
        }
    }
}
