package com.example.employee.service.concrete;

import com.example.employee.service.storage.StorageService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@Profile("dev")
public class MinioServiceimpl implements StorageService {

    @Value("${storage.bucket}")
    private String bucketName;

    @Value("${storage.url-expiry-days:7}")
    private int expiryDays;

    private final MinioClient minioClient;

    public MinioServiceimpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Async
    @Override
    public void upload(String folder, String objectName, MultipartFile file) {
        try {
            String objectKey = folder + "/" + objectName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("MinIO upload failed", e);
        }
    }

    @Override
    public String generateUrl(String folder, String objectName) {
        try {
            String objectKey = folder + "/" + objectName;

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(expiryDays, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("MinIO url generation failed", e);
        }
    }

    @Override
    public InputStreamResource download(String folder, String objectName) {
        try {
            String objectKey = folder + "/" + objectName;

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new IllegalStateException("MinIO download failed", e);
        }
    }

    @Override
    public void delete(String folder, String objectName) {
        try {
            String objectKey = folder + "/" + objectName;

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new IllegalStateException("MinIO delete failed", e);
        }
    }
}
