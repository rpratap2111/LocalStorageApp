package com.example.localstorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

        @Bean
        public S3Client s3Client(
                        S3Properties props) {
                String accessKey = props.getAccessKey() != null ? props.getAccessKey() : "dummy-access-key";
                String secretKey = props.getSecretKey() != null ? props.getSecretKey() : "dummy-secret-key";

                return S3Client.builder()
                                .region(Region.of(props.getRegion()))
                                .credentialsProvider(
                                                StaticCredentialsProvider.create(
                                                                AwsBasicCredentials.create(
                                                                                accessKey,
                                                                                secretKey)))
                                .build();
        }
}
