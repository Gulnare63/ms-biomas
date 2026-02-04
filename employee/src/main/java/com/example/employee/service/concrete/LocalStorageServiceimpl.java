package com.example.employee.service.concrete;

import com.example.employee.service.storage.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

@Service
@Profile("local")
public class LocalStorageServiceimpl implements StorageService {

    @Value("${storage.local.base-path:./uploads}")
    private String basePath;

    @Value("${storage.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    @Async
    @Override
    public void upload(String folder, String objectName, MultipartFile file) {
        try {
            Path dir = Paths.get(basePath, folder);
            Files.createDirectories(dir);

            Path target = dir.resolve(objectName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to upload to server disk", e);
        }
    }

    @Override
    public String generateUrl(String folder, String objectName) {
        // local-da presigned yox, public link qaytarırıq: /local-files/{folder}/{objectName}
        return publicBaseUrl + "/local-files/" + folder + "/" + objectName;
    }

    @Override
    public InputStreamResource download(String folder, String objectName) {
        try {
            Path filePath = Paths.get(basePath, folder, objectName);
            InputStream is = Files.newInputStream(filePath, StandardOpenOption.READ);
            return new InputStreamResource(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to download from server disk", e);
        }
    }

    @Override
    public void delete(String folder, String objectName) {
        try {
            Path filePath = Paths.get(basePath, folder, objectName);
            Files.deleteIfExists(filePath);
            // boş folder-ləri də təmizlə: muellimden sorusub lazimdirsa edecem
            // FileSystemUtils.deleteRecursively(Paths.get(basePath, folder).toFile());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to delete from server disk", e);
        }
    }
}
