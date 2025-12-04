package com.m2it.hermes.application.port.in;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.m2it.hermes.domain.model.File;

public interface FileUseCase {

    List<File> uploadFiles(String bucketId, UUID refId, MultipartFile[] files);
    List<File> getFiles(String bucketId, UUID refId);
    List<File> getFiles(String bucketId);

    File getFileById(UUID id);
    Path getFilePath(String fileName);
    void deleteFile(UUID fileId);
}
