package com.m2it.hermes.application.port.in;

import com.m2it.hermes.domain.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public interface FileUseCase {
    /**
     * Upload files for a property
     * @param propertyId the property ID
     * @param files the files to upload
     * @return the list of created files
     */
    List<File> uploadFilesForProperty(UUID propertyId, MultipartFile[] files);

    /**
     * Get all files for a property
     * @param propertyId the property ID
     * @return the list of files
     */
    List<File> getFilesByProperty(UUID propertyId);

    /**
     * Get a file path for download
     * @param fileName the file name
     * @return the file path
     */
    Path getFilePath(String fileName);

    /**
     * Delete a file
     * @param fileId the file ID
     */
    void deleteFile(UUID fileId);
}
