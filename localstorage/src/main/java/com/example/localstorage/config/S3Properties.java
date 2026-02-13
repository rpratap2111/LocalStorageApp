package com.example.localstorage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3Properties {
    private String bucket;
    private String region;
    private String accessKey;
    private String secretKey;
}
