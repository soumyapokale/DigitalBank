package com.bank.DigitalBank.Entity;



import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public interface UploadedFileRepository extends JpaRepository<com.bank.DigitalBank.entity.UploadedFile, Long> {


}
