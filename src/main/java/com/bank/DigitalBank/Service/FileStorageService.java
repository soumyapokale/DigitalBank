package com.bank.DigitalBank.Service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    com.bank.DigitalBank.entity.UploadedFile storeFile(MultipartFile file);

    ResponseEntity<InputStreamResource> downloadFileByName(String fileName) throws IOException;
}
