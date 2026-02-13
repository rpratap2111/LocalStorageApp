package com.example.localstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.localstorage.entity.StoredFile;

public interface FileRepository extends JpaRepository<StoredFile, Long> {
    void deleteByFilename(String filename);
}
