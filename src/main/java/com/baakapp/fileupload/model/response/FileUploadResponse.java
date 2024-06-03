package com.baakapp.fileupload.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private String name;
    private String type;
    private String path;
    private Long size;
}
