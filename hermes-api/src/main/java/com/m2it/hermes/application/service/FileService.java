package com.m2it.hermes.application.service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.m2it.hermes.application.port.in.FileUseCase;
import com.m2it.hermes.domain.model.File;
import com.m2it.hermes.domain.port.out.FileRepository;
import com.m2it.hermes.domain.port.out.PropertyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileUseCase {

    private final FileRepository fileRepository;
    private final PropertyRepository propertyRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public List<File> uploadFiles(String bucketType, UUID refId, MultipartFile[] files){

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
            File savedFile = fileRepository.save(domainFile, bucketType, refId);
            uploadedFiles.add(savedFile);

            log.info("File uploaded successfully for Id {}: {}", refId, file.getOriginalFilename());
        }

        return uploadedFiles;
    }

    @Override
    public List<File> getFiles(String bucketType, UUID propertyId) {
        return fileRepository.findByRefId(bucketType, propertyId);
    }

    @Override
    public Path getFilePath(String fileName) {
        return fileStorageService.getFilePath(fileName);
    }

    @Override
    public File getFileById(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with ID: " + id));
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

    @Override
    public List<File> getFiles(String bucketId) {
         return fileRepository.findByBucketId(bucketId);
    }
}
