package com.m2it.hermes.application.service;

import java.nio.file.Path;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file);
    void deleteFile(String fileName);
    Path getFilePath(String fileName);
}
