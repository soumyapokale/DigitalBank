package com.bank.DigitalBank.Service.Impl;


import com.bank.DigitalBank.Entity.UploadedFileRepository;
import com.bank.DigitalBank.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String UPLOAD_DIR = "C:/springproject/uploads/";

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Value("${file.upload-dir:C:/springproject/uploads}")
    private String uploadDir;

    @Override
    public com.bank.DigitalBank.entity.UploadedFile storeFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // Validate file types
            String contentType = file.getContentType();
            if (!isAllowedFileType(contentType)) {
                throw new RuntimeException("Invalid file type: " + contentType);
            }

            // Create folder if not exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file to disk
            Path filePath = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Save metadata in DB
            com.bank.DigitalBank.entity.UploadedFile uploadedFile = new com.bank.DigitalBank.entity.UploadedFile();
            uploadedFile.setFileName(file.getOriginalFilename());
            uploadedFile.setFileType(file.getContentType());
            uploadedFile.setSize(file.getSize());
            uploadedFile.setFilePath(filePath.toString());
            uploadedFile.setUploadedAt(LocalDateTime.now());


            return uploadedFileRepository.save(uploadedFile);

        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }



    private boolean isAllowedFileType(String contentType) {
        return contentType.equalsIgnoreCase("application/pdf")
                || contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("application/msword")
                || contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadFileByName(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        File file = filePath.toFile();

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");

        MediaType mediaType = getMediaType(file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }

    private MediaType getMediaType(String fileName) {
        String name = fileName.toLowerCase();
        if (name.endsWith(".pdf")) return MediaType.APPLICATION_PDF;
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (name.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (name.endsWith(".doc")) return MediaType.valueOf("application/msword");
        if (name.endsWith(".docx")) return MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}

