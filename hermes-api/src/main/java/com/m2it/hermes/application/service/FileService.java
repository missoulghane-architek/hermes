package com.m2it.hermes.application.service;

import com.m2it.hermes.application.port.in.FileUseCase;
import com.m2it.hermes.domain.exception.PropertyNotFoundException;
import com.m2it.hermes.domain.model.File;
import com.m2it.hermes.domain.port.out.FileRepository;
import com.m2it.hermes.domain.port.out.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileUseCase {

    private final FileRepository fileRepository;
    private final PropertyRepository propertyRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public List<File> uploadFilesForProperty(UUID propertyId, MultipartFile[] files) {
        // Verify property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + propertyId));

        List<File> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            // Store file physically
            String storedFileName = fileStorageService.storeFile(file);

            // Build file URL
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(storedFileName)
                    .toUriString();

            // Create domain model
            File domainFile = File.builder()
                    .id(UUID.randomUUID())
                    .name(file.getOriginalFilename())
                    .url(fileUrl)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save metadata to repository with property association
            File savedFile = fileRepository.saveForProperty(domainFile, propertyId);
            uploadedFiles.add(savedFile);

            log.info("File uploaded successfully for property {}: {}", propertyId, file.getOriginalFilename());
        }

        return uploadedFiles;
    }

    @Override
    public List<File> getFilesByProperty(UUID propertyId) {
        return fileRepository.findByPropertyId(propertyId);
    }

    @Override
    public Path getFilePath(String fileName) {
        return fileStorageService.getFilePath(fileName);
    }

    @Override
    @Transactional
    public void deleteFile(UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + fileId));

        // Extract file name from URL
        String fileName = file.getUrl().substring(file.getUrl().lastIndexOf("/") + 1);

        // Delete physical file
        fileStorageService.deleteFile(fileName);

        // Delete metadata from repository
        fileRepository.delete(fileId);

        log.info("File deleted successfully: {}", fileId);
    }
}
