package com.baakapp.fileupload.service.impl;

import com.baakapp.fileupload.entity.FileData;
import com.baakapp.fileupload.model.exception.FileProcessingException;
import com.baakapp.fileupload.model.exception.FileUploadException;
import com.baakapp.fileupload.model.response.FileUploadResponse;
import com.baakapp.fileupload.repository.FileRepository;
import com.baakapp.fileupload.service.FileService;
import com.baakapp.fileupload.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileValidator fileValidator;

    private final ModelMapper mapper;

    @Override
    public FileUploadResponse upload(MultipartFile file) {
        fileValidator.validate(file);

        String fileName = file.getOriginalFilename().replace(" ", "-");

        try {
            FileData fileData = fileRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .data(getFileBytes(file))
                .size(file.getSize())
                .path("/" + fileName)
                .build());
            return mapper.map(fileData, FileUploadResponse.class);
        } catch (DataIntegrityViolationException e) {
            throw new FileUploadException("File is already uploaded. Please change name of the file.");
        }
    }

    @Override
    public FileData getFileByName(String fileName) {
        return fileRepository
                .findByName(fileName)
                .orElseThrow(() -> new FileUploadException("File not found"));
    }

    @Override
    public List<FileUploadResponse> getFiles() {
        return fileRepository.findAllFiles();
    }

    @Override
    public void deleteFileByName(String fileName) {
        fileRepository.deleteByName(fileName);
    }

    @Override
    public void updateByName(String currentName, String newName) {
        FileData fileData = fileRepository
                .findByName(currentName)
                .orElseThrow(() -> new FileUploadException("File not found"));

        fileData.setName(newName);

        fileRepository.save(fileData);
    }

    private static byte[] getFileBytes(MultipartFile multipartFile) throws FileUploadException {
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new FileProcessingException("Error processing file: " + e.getMessage(), e);
        }
    }
}
