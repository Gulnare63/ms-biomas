//package com.example.employee.service.storage;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@Profile("dev")
//public class MinioService implements StorageService{
//
//    @Value("${minio.bucket.name}")
//    private String bucketName;
//
//    private final MinioClient minioClient;
//
//    public MinioService(MinioClient minioClient) {
//        this.minioClient = minioClient;
//    }
//
//    public String generateUrl(String objectName) {
//        return generateUrl("videos", objectName);
//    }
//
//    public InputStreamResource downloadFile(String folderName, String fileName) {
//        try {
//            String objectName = folderName + "/" + fileName;
//            InputStream stream = minioClient.getObject(
//                    GetObjectArgs.builder()
//                            .bucket(this.bucketName)
//                            .object(objectName)
//                            .build()
//            );
//
//            return new InputStreamResource(stream);
//        } catch (MinioException e) {
//            System.err.println("Error occurred: " + e);
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public String getObject(String folder, String objectName) {
//        String url = null;
//        try {
//            url = minioClient.getPresignedObjectUrl(
//                    GetPresignedObjectUrlArgs.builder()
//                            .method(Method.GET)
//                            .bucket(this.bucketName)
//                            .object(folder + "/" + objectName)
//                            .expiry(7, TimeUnit.DAYS)
//                            .extraQueryParams(Map.of("response-content-disposition", "attachment; filename=\"" + objectName + "\""))
//                            .build()
//            );
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return url;
//    }
//
//    public String generateUrl(String folder, String objectName) {
//        try {
//            String fullPath = folder != null && !folder.trim().isEmpty() ? folder.trim() + "/" + objectName : objectName;
//
//            GetPresignedObjectUrlArgs.Builder builder = GetPresignedObjectUrlArgs.builder()
//                    .method(Method.GET)
//                    .bucket(this.bucketName)
//                    .object(fullPath)
//                    .expiry(7, TimeUnit.DAYS);
//
//            return minioClient.getPresignedObjectUrl(builder.build());
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    @Async
//    public void uploadFile(String folderName, MultipartFile file) {
//        try {
//            String objectName = folderName + "/" + file.getOriginalFilename();
//
//            minioClient.putObject(PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(objectName)
//                    .contentType(file.getContentType())
//                    .stream(file.getInputStream(), file.getSize(), -1).build());
//        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
//            throw new IllegalStateException("The file cannot be uploaded to the internal storage. Please retry later", e);
//        }
//    }
//
//    public String uploadPdf(byte[] pdfBytes, String fileName) {
//        final String folderName = "contracts";
//        final String objectName = folderName + "/" + fileName;
//        final String contentType = "application/pdf";
//
//        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes)) {
//            PutObjectArgs args = PutObjectArgs.builder()
//                    .bucket(bucketName)
//                    .object(objectName)
//                    .contentType(contentType)
//                    .stream(inputStream, pdfBytes.length, -1)
//                    .build();
//
//            minioClient.putObject(args);
//
//            return fileName;
//        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
//            throw new IllegalStateException("Failed to upload PDF file to MinIO storage. Please try again later.");
//        }
//    }
//}
