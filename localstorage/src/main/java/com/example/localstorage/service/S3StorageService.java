package com.example.localstorage.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.example.localstorage.config.S3Properties;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;
    private final S3Properties props;
    private final LocalStorageService localStorageService;

    private static final String CB_NAME = "s3CircuitBreaker";

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "uploadFallback")
    public String upload(String key, InputStream inputStream, long size, String contentType) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(inputStream, size));
        return "S3";
    }

    public String uploadFallback(String key, InputStream is, long size, String ct, Throwable ex) {
        System.err.println("S3 Upload Failed: " + ex.getMessage() + ". Falling back to local storage.");
        localStorageService.upload(key, is);
        return "LOCAL";
    }

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "deleteFallback")
    public void delete(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build());
    }

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "downloadFallback")
    public byte[] download(String key) throws IOException {
        return s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(props.getBucket())
                        .key(key)
                        .build())
                .readAllBytes();
    }

    public byte[] downloadFallback(String key, Throwable ex) throws IOException {
        System.err.println("S3 Download Failed: " + ex.getMessage() + ". Falling back to local storage.");
        return localStorageService.download(key);
    }

    public void deleteFallback(String key, Throwable ex) {
        System.err.println("S3 Delete Failed: " + ex.getMessage() + ". Falling back to local storage.");
        localStorageService.delete(key);
    }

}
