package com.example.employee.controller;

import com.example.employee.service.storage.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Profile("local")
@RequestMapping("/local-files")
public class LocalFileController {

    private final StorageService storageService;

    @GetMapping("/**")
    public ResponseEntity<byte[]> download(HttpServletRequest request) {
        // /local-files/{folder}/{objectName}
        String uri = request.getRequestURI(); // /local-files/employees/7/photo/abc.png
        String prefix = "/local-files/";
        String path = uri.substring(prefix.length()); // employees/7/photo/abc.png

        // folder = employees/7/photo , objectName = abc.png
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) return ResponseEntity.badRequest().build();

        String folder = path.substring(0, lastSlash);
        String objectName = path.substring(lastSlash + 1);

        try {
            var resource = storageService.download(folder, objectName);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return ResponseEntity.ok()
                    .contentType(detect(objectName))
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private MediaType detect(String objectName) {
        String lower = objectName.toLowerCase();
        if (lower.endsWith(".png")) return MediaType.IMAGE_PNG;
        return MediaType.IMAGE_JPEG;
    }
}
