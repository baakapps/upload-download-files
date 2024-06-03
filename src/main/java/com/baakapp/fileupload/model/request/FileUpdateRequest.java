package com.baakapp.fileupload.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUpdateRequest {
    private String currentName;
    private String newName;
}
