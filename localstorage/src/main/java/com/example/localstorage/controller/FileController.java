package com.example.localstorage.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.localstorage.dto.FileResponseDTO;
import com.example.localstorage.entity.StoredFile;
import com.example.localstorage.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileService service;

    @PostMapping("/upload")
    public ResponseEntity<FileResponseDTO> upload(@RequestParam("file") MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(service.upload(file));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String filename) throws IOException {

        byte[] data;
        data = service.download(filename);

        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFilename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<String> delete(@PathVariable String filename) throws IOException {
        String decoded = URLDecoder.decode(filename, StandardCharsets.UTF_8);
        service.delete(decoded);
        return ResponseEntity.ok("Deleted: " + decoded);
    }

    @GetMapping("/list")
    public List<StoredFile> list() {
        return service.list();
    }
}
