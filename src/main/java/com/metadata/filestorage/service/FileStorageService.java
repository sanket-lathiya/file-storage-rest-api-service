package com.metadata.filestorage.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.metadata.filestorage.controller.FileStorageController;
import com.metadata.filestorage.exception.FileNotFoundException;
import com.metadata.filestorage.exception.FileStorageException;
import com.metadata.filestorage.model.File;
import com.metadata.filestorage.model.FileVersion;
import com.metadata.filestorage.repository.FileStorageRepository;
import com.metadata.filestorage.repository.FileVersionRepository;

@Service
public class FileStorageService implements IFileStorageService{

    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Autowired
    private FileVersionRepository fileVersionRepository;

    private final Logger logger = LoggerFactory.getLogger(FileStorageController.class);

    @Override
    @Transactional
    public File storeFile(MultipartFile multipartFile){
        try {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            File file = new File(fileName, multipartFile.getContentType());
            FileVersion fileVersion = new FileVersion(1, multipartFile.getBytes());
            file.setFileVersions(new HashSet<>(Arrays.asList(fileVersion)));
            return fileStorageRepository.save(file);
        } catch (IOException e) {
            String message = "Failed to store file!";
            logger.error(message, e);
            throw new FileStorageException(message);
        }
    }

    @Override
    @Transactional
    public List<File> storeMultipleFile(MultipartFile[] multipartFiles){
        try {
            List<File> files = new ArrayList<>();

            for(MultipartFile multipartFile : multipartFiles) {
                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                File file = new File(fileName, multipartFile.getContentType());
                FileVersion fileVersion = new FileVersion(1, multipartFile.getBytes());
                file.setFileVersions(new HashSet<>(Arrays.asList(fileVersion)));
                files.add(file);
            }

            return fileStorageRepository.saveAll(files);
        } catch (IOException e) {
            String message = "Failed to store mutiple files!";
            logger.error(message, e);
            throw new FileStorageException(message);
        }
    }

    @Override
    @Transactional
    public File createNewFileVersion(File file, MultipartFile multipartFile) {

        try {
            Set<FileVersion> fileVersions = file.getFileVersions();

            Integer maxFileVersion = 0;
            if(!fileVersions.isEmpty()) {
                maxFileVersion = fileVersions.stream().max(Comparator.comparing(FileVersion::getVersionId)).get().getVersionId();
            }

            FileVersion newFileVersion = new FileVersion(maxFileVersion + 1, multipartFile.getBytes());
            fileVersions.add(newFileVersion);
            return fileStorageRepository.save(file);
        } catch (IOException e) {
            String message = String.format("Failed to create new file version for fileId: %s", file.getId());
            logger.error(message, e);
            throw new FileStorageException(message);
        }
    }

    @Override
    @Transactional
    public FileVersion updateFileVersion(String id, MultipartFile multipartFile, Integer versionId){
        Optional<FileVersion> fileVersion = fileVersionRepository.findByFileIdAndVersionId(id, versionId);

        if(!fileVersion.isPresent()) {
            throw new FileNotFoundException(String.format("File not found with id: %s and version: %s", id, versionId));
        }

        try {
            fileVersion.get().setContent(multipartFile.getBytes());
            fileVersion.get().setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            return fileVersionRepository.save(fileVersion.get());
        } catch (IOException e) {
            String message = String.format("Failed to update file version %s for fileId: %s", versionId, id);
            logger.error(message, e);
            throw new FileStorageException(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public File getFile(String id){
        Optional<File> file = fileStorageRepository.findById(id);

        if(!file.isPresent()) {
            throw new FileNotFoundException(String.format("File is not found with id: %s", id));
        }
        
        return file.get();
    }

    @Override
    @Transactional(readOnly = true)
    public List<File> getAllFiles(){
        return fileStorageRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteFile(String id){
        Optional<File> file = fileStorageRepository.findById(id);

        if(!file.isPresent()) {
            throw new FileNotFoundException(String.format("File is not found with id: %s", id));
        }

        fileStorageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteFileVersion(String id, Integer versionId) {

        Optional<FileVersion> fileVersion = fileVersionRepository.findByFileIdAndVersionId(id, versionId);

        if(!fileVersion.isPresent()) {
            throw new FileNotFoundException(String.format("File not found with id: %s and version: %s", id, versionId));
        }

        fileVersionRepository.deleteById(fileVersion.get().getId());
    }
}
