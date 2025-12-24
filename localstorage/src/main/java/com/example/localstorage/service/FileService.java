package com.example.localstorage.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import static java.nio.file.Files.deleteIfExists;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private final String storageLocation = "uploads"; // or load from config

    public FileResponseDTO upload(MultipartFile file) throws IOException {

        Files.createDirectories(Paths.get(storageLocation));

        Path path = Paths.get(storageLocation + "/" + file.getOriginalFilename());
        Files.write(path, file.getBytes());

        StoredFile storedFile = new StoredFile();
        storedFile.setFilename(file.getOriginalFilename());
        storedFile.setContentType(file.getContentType());
        storedFile.setSize(file.getSize());
        storedFile.setPath(path.toString());

        repo.save(storedFile);

        return new FileResponseDTO(storedFile.getId(), storedFile.getFilename(), storedFile.getPath());
    }

    public byte[] download(String filename) throws IOException {
        Path path = Paths.get(storageLocation + "/" + filename);
        return Files.readAllBytes(path);
    }

    public void delete(String filename) throws IOException {

        // Decode encoded filenames (spaces, commas)
        String decoded = java.net.URLDecoder.decode(filename, StandardCharsets.UTF_8);

        // Delete file from disk
        Path path = Paths.get(storageLocation).resolve(decoded);
        deleteIfExists(path);

        // Delete DB entry
        repo.deleteByFilename(decoded);
    }

    public List<StoredFile> list() {
        return repo.findAll();
    }
}
