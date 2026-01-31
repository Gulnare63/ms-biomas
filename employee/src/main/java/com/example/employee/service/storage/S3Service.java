//package com.example.employee.service.storage;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.InputStreamResource;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URI;
//import java.net.URL;
//import java.time.Duration;
//
//@Service
//@Profile("prod")
//public class S3Service implements StorageService {
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//    @Value("${cloud.aws.region.static}")
//    private String region;
//
//    @Value("${cloud.aws.credentials.access-key}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String secretKey;
//
//    @Value("${cloud.aws.s3.endpoint}")
//    private String endpoint; // custom endpoint
//
//    private final S3Client s3Client;
//    private final S3Presigner presigner;
//
//    public S3Service(
//            @Value("${cloud.aws.credentials.access-key}") String accessKey,
//            @Value("${cloud.aws.credentials.secret-key}") String secretKey,
//            @Value("${cloud.aws.region.static}") String region,
//            @Value("${cloud.aws.s3.endpoint}") String endpoint) {
//
//        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);
//
//        // 👇 Important: path-style access for AzIntelecom/MinIO
//        S3Configuration s3Config = S3Configuration.builder()
//                .pathStyleAccessEnabled(true)
//                .build();
//
//        this.s3Client = S3Client.builder()
//                .endpointOverride(URI.create(endpoint))
//                .region(Region.of(region))
//                .serviceConfiguration(s3Config)
//                .credentialsProvider(StaticCredentialsProvider.create(creds))
//                .build();
//
//
//        this.presigner = S3Presigner.builder()
//                .endpointOverride(URI.create(endpoint))
//                .region(Region.of(region))
//                .serviceConfiguration(s3Config) // 👈 same here
//                .credentialsProvider(StaticCredentialsProvider.create(creds))
//                .build();
//    }
//
//    /**
//     * Upload MultipartFile
//     */
//    @Async
//    public void uploadFile(String folderName, MultipartFile file) {
//        String objectKey = folderName + "/" + file.getOriginalFilename();
//        try {
//            s3Client.putObject(
//                    PutObjectRequest.builder()
//                            .bucket(bucketName)
//                            .key(objectKey)
//                            .contentType(file.getContentType())
//                            .build(),
//                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
//            );
//        } catch (IOException e) {
//            throw new IllegalStateException("Failed to upload file to S3", e);
//        }
//    }
//
//    public String uploadPdf(byte[] pdfBytes, String fileName) {
//        String objectKey = "contracts/" + fileName;
//
//        PutObjectRequest putRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(objectKey)
//                .contentType("application/pdf") // you can try removing this if it fails
//                .build();
//
//        s3Client.putObject(putRequest, RequestBody.fromBytes(pdfBytes));
//
//        // return the full accessible URL, not just the filename
//        return fileName;
//    }
//
//
//    /**
//     * Generate Pre-signed URL
//     */
//    public String generateUrl(String folder, String objectName) {
//        String objectKey = (folder != null && !folder.isEmpty())
//                ? folder + "/" + objectName
//                : objectName;
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(objectKey)
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofDays(7))
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//        URL url = presigner.presignGetObject(presignRequest).url();
//        return url.toString();
//    }
//
//    /**
//     * Download File (streaming, no temp file on disk)
//     */
//    public InputStreamResource downloadFile(String folderName, String fileName) {
//        String objectKey = folderName + "/" + fileName;
//
//        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(
//                GetObjectRequest.builder()
//                        .bucket(bucketName)
//                        .key(objectKey)
//                        .build()
//        );
//
//        return new InputStreamResource(response);
//    }
//}
