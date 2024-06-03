package com.baakapp.fileupload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="file")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column(unique = true)
    private String name;
    private String type;
    private String path;
    private Long size;
    @Lob
    @Column(columnDefinition="mediumblob")
    private byte[] data;

}
