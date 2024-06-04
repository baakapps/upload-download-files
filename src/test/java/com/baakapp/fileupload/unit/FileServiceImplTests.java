package com.baakapp.fileupload.unit;

import com.baakapp.fileupload.entity.FileData;
import com.baakapp.fileupload.model.exception.FileUploadException;
import com.baakapp.fileupload.model.response.FileUploadResponse;
import com.baakapp.fileupload.repository.FileRepository;
import com.baakapp.fileupload.service.impl.FileServiceImpl;
import com.baakapp.fileupload.util.FileValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTests {
    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileValidator fileValidator;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void whenUploadIsSuccessfulThenSaveAndReturnResult() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test file.txt", "text/plain", "Some file content".getBytes());
        FileData fileData = FileData.builder()
                .name("test file.txt")
                .type("text/plain")
                .data(file.getBytes())
                .size(file.getSize())
                .path("/test-file.txt")
                .build();
        FileUploadResponse response = new FileUploadResponse();

        when(fileRepository.save(any(FileData.class))).thenReturn(fileData);
        when(mapper.map(any(FileData.class), eq(FileUploadResponse.class))).thenReturn(response);

        FileUploadResponse result = fileService.upload(file);

        assertNotNull(result);
        verify(fileValidator).validate(file);
        verify(fileRepository).save(any(FileData.class));
        verify(mapper).map(fileData, FileUploadResponse.class);
    }

    @Test
    void whenFileIsAlreadyUploadedThenUploadShouldThrowFileUploadException() {
        MockMultipartFile file = new MockMultipartFile("file", "test file.txt", "text/plain", "Some file content".getBytes());

        when(fileRepository.save(any(FileData.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(FileUploadException.class, () -> fileService.upload(file));
        verify(fileValidator).validate(file);
        verify(fileRepository).save(any(FileData.class));
    }


    @Test
    void whenFileSizeExceedsLimitThenUploadShouldThrowFileUploadException() {
        long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
        MockMultipartFile file = new MockMultipartFile("file", "large file.txt", "text/plain", new byte[(int) (MAX_FILE_SIZE + 1)]);

        doThrow(new FileUploadException("File size exceeds the limit of 5MB")).when(fileValidator).validate(file);

        FileUploadException exception = assertThrows(FileUploadException.class, () -> fileService.upload(file));
        assertEquals("File size exceeds the limit of 5MB", exception.getMessage());
        verify(fileValidator).validate(file);
        verify(fileRepository, never()).save(any(FileData.class));
        verify(mapper, never()).map(any(FileData.class), eq(FileUploadResponse.class));
    }

    @Test
    void whenFileExistsThenGetFileByNameReturnsFileData() {
        String fileName = "testFile.txt";
        FileData fileData = new FileData();
        fileData.setName(fileName);

        when(fileRepository.findByName(fileName)).thenReturn(Optional.of(fileData));

        FileData result = fileService.getFileByName(fileName);

        assertNotNull(result);
        assertEquals(fileName, result.getName());
        verify(fileRepository, times(1)).findByName(fileName);
    }

    @Test
    void whenFileDoesNotExistThenGetFileByNameThrowsFileUploadException() {
        String fileName = "testFile.txt";
        FileData fileData = new FileData();
        fileData.setName(fileName);

        when(fileRepository.findByName(fileName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FileUploadException.class, () -> {
            fileService.getFileByName(fileName);
        });

        String expectedMessage = "File not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(fileRepository, times(1)).findByName(fileName);
    }

    @Test
    void whenFileExistsThenUpdateFileByName() {
        String currentName = "oldFileName";
        String newName = "newFileName";
        FileData fileData = new FileData();
        fileData.setName(currentName);

        when(fileRepository.findByName(currentName)).thenReturn(Optional.of(fileData));

        fileService.updateByName(currentName, newName);

        verify(fileRepository).findByName(currentName);
        verify(fileRepository).save(fileData);
        assert(fileData.getName().equals(newName));
    }

    @Test
    void whenFileNotFoundThenUpdateByNameThrowsException() {
        String currentName = "nonExistingFile";
        String newName = "newFileName";

        when(fileRepository.findByName(currentName)).thenReturn(Optional.empty());

        assertThrows(FileUploadException.class, () -> fileService.updateByName(currentName, newName));

        verify(fileRepository).findByName(currentName);
        verify(fileRepository, never()).save(any(FileData.class));
    }

    @Test
    void whenFileExistsThenDeleteFileByName() {
        FileData fileData = new FileData();
        fileData.setName("homework");

        when(fileRepository.findByName("testFile.txt")).thenReturn(Optional.of(fileData));

        fileService.deleteFileByName("testFile.txt");

        verify(fileRepository, times(1)).delete(fileData);
    }

    @Test
    void whenFileNotFoundThenDeleteFileByNameThrowsException() {
        // Arrange
        when(fileRepository.findByName("nonExistentFile.txt")).thenReturn(Optional.empty());

        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            fileService.deleteFileByName("nonExistentFile.txt");
        });

        assertEquals("File not found", exception.getMessage());
        verify(fileRepository, never()).delete(any(FileData.class));
    }
}
