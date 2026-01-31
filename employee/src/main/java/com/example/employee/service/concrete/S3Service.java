package com.example.employee.service.concrete;


import com.example.employee.service.storage.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("prod")
public class S3Service implements StorageService {

    @Override
    public void upload(String folder, String objectName, MultipartFile file) {
        throw new UnsupportedOperationException("S3 not implemented yet");
    }

    @Override
    public String generateUrl(String folder, String objectName) {
        throw new UnsupportedOperationException("S3 not implemented yet");
    }

    @Override
    public InputStreamResource download(String folder, String objectName) {
        throw new UnsupportedOperationException("S3 not implemented yet");
    }

    @Override
    public void delete(String folder, String objectName) {
        throw new UnsupportedOperationException("S3 not implemented yet");
    }
}
