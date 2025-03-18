package com.e_commerce.product_service.service.minio;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String minioEndpoint;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public MinioService(@Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.minioEndpoint = url; // Store MinIO URL
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            String objectUrl = String.format("%s/%s/%s", minioEndpoint, bucketName, fileName);
            return objectUrl;

        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("File upload failed");
        }
    }

    // ✅ Tambahkan metode deleteFile untuk menghapus file lama dari MinIO
    public void deleteFile(String fileUrl, String bucketName) {
        try {
            // ✅ Ekstrak hanya nama file dari URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());

            System.out.println("✅ File berhasil dihapus dari MinIO: " + fileName);

        } catch (Exception e) {
            throw new RuntimeException("❌ Error deleting file from MinIO: " + e.getMessage());
        }
    }

}
