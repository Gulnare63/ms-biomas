package com.example.employee.service.storage;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(String folder, String objectName, MultipartFile file);

    String generateUrl(String folder, String objectName);

    InputStreamResource download(String folder, String objectName);

    void delete(String folder, String objectName);
}
