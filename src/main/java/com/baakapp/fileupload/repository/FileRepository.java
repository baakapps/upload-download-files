package com.baakapp.fileupload.repository;

import com.baakapp.fileupload.entity.FileData;
import com.baakapp.fileupload.model.response.FileUploadResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileData, Long> {
    @Query("SELECT new com.baakapp.fileupload.model.response.FileUploadResponse(f.name, f.type, f.path, f.size) FROM FileData f")
    List<FileUploadResponse> findAllFiles();

    @Transactional
    @Modifying
    @Query("DELETE FROM FileData f WHERE f.name = ?1")
    void deleteByName(String name);

    Optional<FileData> findByName(String name);

}
