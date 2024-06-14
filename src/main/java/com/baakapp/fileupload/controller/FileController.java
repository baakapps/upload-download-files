package com.baakapp.fileupload.controller;

import com.baakapp.fileupload.entity.FileData;
import com.baakapp.fileupload.model.request.FileDeleteRequest;
import com.baakapp.fileupload.model.request.FileDownloadRequest;
import com.baakapp.fileupload.model.request.FileUpdateRequest;
import com.baakapp.fileupload.model.response.FileResponse;
import com.baakapp.fileupload.model.response.FileUploadResponse;
import com.baakapp.fileupload.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping(value="upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fileService.upload(multipartFile));
    }

    @GetMapping(value="download", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> download(@RequestBody FileDownloadRequest fileDownloadRequest) {
        FileData file = fileService.getFileByName(fileDownloadRequest.fileName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(file.getType()))
                .body(new ByteArrayResource(file.getData()));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FileUploadResponse>> getFiles() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fileService.getFiles());
    }

    @DeleteMapping()
    public ResponseEntity<FileResponse> deleteFile(@RequestBody FileDeleteRequest fileDeleteRequest) {
        fileService.deleteFileByName(fileDeleteRequest.getName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FileResponse("File deleted successfully"));
    }

    @PutMapping()
    public ResponseEntity<FileResponse> updateUserByName(@RequestBody FileUpdateRequest fileUpdateRequest) {
        fileService.updateByName(fileUpdateRequest.getCurrentName(), fileUpdateRequest.getNewName());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new FileResponse("File updated successfully"));
    }
}
