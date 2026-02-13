package com.example.localstorage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileResponseDTO {
    private Long id;
    private String filename;
    private String path;
}
