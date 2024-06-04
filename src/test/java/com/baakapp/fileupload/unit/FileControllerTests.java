package com.baakapp.fileupload.unit;

import com.baakapp.fileupload.controller.FileController;
import com.baakapp.fileupload.entity.FileData;
import com.baakapp.fileupload.model.request.FileDeleteRequest;
import com.baakapp.fileupload.model.request.FileDownloadRequest;
import com.baakapp.fileupload.model.request.FileUpdateRequest;
import com.baakapp.fileupload.model.response.FileResponse;
import com.baakapp.fileupload.model.response.FileUploadResponse;
import com.baakapp.fileupload.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileControllerTests {
    @Mock
    private FileService fileService;

    @InjectMocks
    private FileController fileController;

    @Test
    void upload() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        FileUploadResponse response = new FileUploadResponse("test.txt", "text/plain", "/path/to/file", 12L);

        when(fileService.upload(any(MultipartFile.class))).thenReturn(response);

        ResponseEntity<FileUploadResponse> result = fileController.upload(file);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(fileService, times(1)).upload(any(MultipartFile.class));
    }

    @Test
    void download() {
        byte[] data = "Hello, World!".getBytes();
        FileData fileData = new FileData(1L, "test.txt", "text/plain", "/path/to/file", 12L, data);
        FileDownloadRequest request = new FileDownloadRequest("test.txt");

        when(fileService.getFileByName(anyString())).thenReturn(fileData);

        ResponseEntity<Resource> result = fileController.download(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(MediaType.parseMediaType("text/plain"), result.getHeaders().getContentType());
        assertEquals(data, ((ByteArrayResource) result.getBody()).getByteArray());
        verify(fileService, times(1)).getFileByName(anyString());
    }


    @Test
    void deleteFile() {
        FileDeleteRequest request = new FileDeleteRequest("test.txt");

        doNothing().when(fileService).deleteFileByName(anyString());

        ResponseEntity<FileResponse> result = fileController.deleteFile(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(new FileResponse("File deleted successfully"), result.getBody());
        verify(fileService, times(1)).deleteFileByName(anyString());
    }

    @Test
    void updateUserByName() {
        FileUpdateRequest request = new FileUpdateRequest("test.txt", "newTest.txt");

        doNothing().when(fileService).updateByName(anyString(), anyString());

        ResponseEntity<FileResponse> result = fileController.updateUserByName(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(new FileResponse("File updated successfully"), result.getBody());
        verify(fileService, times(1)).updateByName(anyString(), anyString());
    }
}
