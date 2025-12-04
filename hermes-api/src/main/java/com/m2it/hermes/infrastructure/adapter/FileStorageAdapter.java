package com.m2it.hermes.infrastructure.adapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.m2it.hermes.application.service.FileStorageService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileStorageAdapter implements FileStorageService {

    private final Path uploadDir;

    public FileStorageAdapter(@Value("${file.upload-dir:uploads}") String uploadDirectory) {
        this.uploadDir = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            log.info("Upload directory initialized at: {}", this.uploadDir);
        } catch (IOException e) {
            log.error("Failed to create upload directory at: {}", this.uploadDir, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.warn("Attempted to store an empty file");
            throw new RuntimeException("Failed to store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("File stored successfully: {}", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("Failed to store file: {}", fileName, e);
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.uploadDir.resolve(fileName).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("File deleted successfully: {}", fileName);
            } else {
                log.warn("File not found for deletion: {}", fileName);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileName, e);
            throw new RuntimeException("Failed to delete file " + fileName, e);
        }
    }

    @Override
    public Path getFilePath(String fileName) {
        return this.uploadDir.resolve(fileName).normalize();
    }
}
