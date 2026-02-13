package com.example.localstorage.service;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService {

    private final Path uploadDir;

    public LocalStorageService() {
        this.uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public void upload(String key, InputStream inputStream) {
        try {
            Path targetLocation = this.uploadDir.resolve(key);
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + key + ". Please try again!", ex);
        }
    }

    public byte[] download(String key) throws IOException {
        Path filePath = this.uploadDir.resolve(key).normalize();
        if (!Files.exists(filePath)) {
            throw new IOException("File not found locally: " + key);
        }
        return Files.readAllBytes(filePath);
    }

    public void delete(String key) {
        Path filePath = this.uploadDir.resolve(key).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + key, ex);
        }
    }
}
