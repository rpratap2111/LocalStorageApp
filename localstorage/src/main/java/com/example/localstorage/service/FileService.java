package com.example.localstorage.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.localstorage.dto.FileResponseDTO;
import com.example.localstorage.entity.StoredFile;
import com.example.localstorage.repository.FileRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    private final FileRepository repo;
    private final S3StorageService s3StorageService;

    // Upload file to S3 + save metadata
    public FileResponseDTO upload(MultipartFile file) throws IOException {

        String key = file.getOriginalFilename();

        // Upload to S3 (Circuit Breaker protected)
        String storageType = s3StorageService.upload(
                key,
                file.getInputStream(),
                file.getSize(),
                file.getContentType());

        // Save metadata to DB
        StoredFile storedFile = new StoredFile();
        storedFile.setFilename(key);
        storedFile.setContentType(file.getContentType());
        storedFile.setSize(file.getSize());

        if ("LOCAL".equals(storageType)) {
            storedFile.setPath("local://" + key);
        } else {
            storedFile.setPath("s3://" + key);
        }

        repo.save(storedFile);

        return new FileResponseDTO(
                storedFile.getId(),
                storedFile.getFilename(),
                storedFile.getPath());
    }

    // Download file from S3
    public byte[] download(String filename) throws IOException {
        return s3StorageService.download(filename);
    }

    // Delete file from S3 + DB
    public void delete(String filename) {
        s3StorageService.delete(filename);
        repo.deleteByFilename(filename);
    }

    // List metadata
    public List<StoredFile> list() {
        return repo.findAll();
    }
}
