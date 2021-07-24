package com.metadata.filestorage.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.metadata.filestorage.model.File;
import com.metadata.filestorage.model.FileVersion;

public interface IFileStorageService {

    File storeFile(MultipartFile file);
    List<File> storeMultipleFile(MultipartFile[] multipartFiles);
    File createNewFileVersion(File file, MultipartFile multipartFile);
    FileVersion updateFileVersion(String id, MultipartFile multipartFile, Integer versionId);
    File getFile(String id);
    List<File> getAllFiles();
    void deleteFile(String id);
    void deleteFileVersion(String id, Integer versionId);
}
