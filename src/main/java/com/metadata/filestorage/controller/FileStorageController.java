package com.metadata.filestorage.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.metadata.filestorage.defines.FileStorageConstants;
import com.metadata.filestorage.exception.BadRequestException;
import com.metadata.filestorage.exception.FileNotFoundException;
import com.metadata.filestorage.model.File;
import com.metadata.filestorage.model.FileVersion;
import com.metadata.filestorage.response.ResponseFile;
import com.metadata.filestorage.response.ResponseMessage;
import com.metadata.filestorage.service.IFileStorageService;

@RestController
@RequestMapping(FileStorageConstants.BASE_PATH)
public class FileStorageController {

    @Autowired
    private IFileStorageService fileStorageService;

    /**
     * This API will create a new file with versionId=1.
     * 
     * @param multipartFile
     * @return newly created file details
     */
    @PostMapping("/files")
    public ResponseEntity<ResponseFile> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        File file = fileStorageService.storeFile(multipartFile);

        ResponseFile response = new ResponseFile(file.getId(), file.getName(), file.getType());

        file.getFileVersions().stream().forEach(version -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(FileStorageConstants.BASE_PATH)
                .path("/files/")
                .path(file.getId())
                .queryParam("versionId", version.getVersionId())
                .toUriString();

            response.addVersion(new ResponseFile.Versions(version.getVersionId(), fileDownloadUri, version.getContent().length, version.getCreatedAt(), version.getUpdatedAt()));
        });

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * This API will create a multiple new files with versionId=1.
     * 
     * @param multipartFile
     * @return list of newly created file details
     */
    @PostMapping("/multiple-files")
    public ResponseEntity<List<ResponseFile>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {

        List<ResponseFile> responses = fileStorageService.storeMultipleFile(files)
            .stream()
            .map(uploadedfile -> {

                ResponseFile response = new ResponseFile(uploadedfile.getId(), uploadedfile.getName(), uploadedfile.getType());

                uploadedfile.getFileVersions().stream().forEach(version -> {

                    String fileDownloadUri = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path(FileStorageConstants.BASE_PATH)
                        .path("/files/")
                        .path(uploadedfile.getId())
                        .queryParam("versionId", version.getVersionId())
                        .toUriString();

                    response.addVersion(new ResponseFile.Versions(version.getVersionId(), fileDownloadUri, version.getContent().length, version.getCreatedAt(), version.getUpdatedAt()));
                });

                return response;
            }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    /**
     * This API will update the file content if there is versionId in the query parameter otherwise create a new file version.
     * The incoming file name and type should be match with existing file object otherwise it will throw bad request exception.
     * 
     * @param id
     * @param multipartFile
     * @param versionId
     * @return Sucess message
     */
    @PutMapping("/files/{id}")
    public ResponseEntity<ResponseMessage> updateFile(@PathVariable String id, @RequestParam("file") MultipartFile multipartFile, @RequestParam(value = "versionId", required = false) Integer versionId) {
        File file = fileStorageService.getFile(id);
        
        if(!file.getName().equals(StringUtils.cleanPath(multipartFile.getOriginalFilename()))) {
            throw new BadRequestException("File name is not correct!");
        }
        
        if(!file.getType().equals(multipartFile.getContentType())) {
            throw new BadRequestException("File type is not correct!");
        }

        if(versionId == null) {
            fileStorageService.createNewFileVersion(file, multipartFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(String.format("New file version created for fileId: %s", id)));
        }else {
            fileStorageService.updateFileVersion(id, multipartFile, versionId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(String.format("File version %s updated for fileId: %s", versionId, id)));
        }
    }

    /**
     * This API will return list of files with all the associated versions.
     * 
     * @return List of files
     */
    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getFileList() {
        List<ResponseFile> files = fileStorageService.getAllFiles()
            .stream()
            .map(file -> {
                ResponseFile response = new ResponseFile(file.getId(), file.getName(), file.getType());

                file.getFileVersions().stream().forEach(version -> {
                    String fileDownloadUri = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path(FileStorageConstants.BASE_PATH)
                        .path("/files/")
                        .path(file.getId())
                        .queryParam("versionId", version.getVersionId())
                        .toUriString();

                    response.addVersion(new ResponseFile.Versions(version.getVersionId(), fileDownloadUri, version.getContent().length, version.getCreatedAt(), version.getUpdatedAt()));
                });

                return response;
            }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    /**
     * This API will return file content as attachment.
     * If there is no versionId in query parameter then it will return the latest file version. 
     * 
     * @param id
     * @param versionId
     * @return File content as attachment
     */
    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable String id, @RequestParam(value = "versionId", required = false) Integer versionId) {
        File file = fileStorageService.getFile(id);
        FileVersion fileVersion = null;
        
        if(file.getFileVersions().isEmpty()) {
            throw new FileNotFoundException(String.format("All file versions were removed for fileId: %s", id));
        }

        if(versionId == null) {
            fileVersion = file.getFileVersions().stream().max(Comparator.comparing(FileVersion::getVersionId)).get();
        }else {
            Optional<FileVersion> fileVer = file.getFileVersions().stream().filter(version -> version.getVersionId() == versionId).findFirst();

            if(!fileVer.isPresent()) {
                throw new FileNotFoundException(String.format("File not found with id: %s and version: %s", id, versionId));
            }

            fileVersion = fileVer.get();
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "_v" + fileVersion.getVersionId() + "\"")
            .body(new ByteArrayResource(fileVersion.getContent()));
    }

    /**
     * This API will delete the file version if there is versionId in the query parameter 
     * otherwise deletes entire file object including all the associated versions.
     * 
     * @param id
     * @param versionId
     * @return Success message
     */
    @DeleteMapping("/files/{id}")
    public ResponseEntity<ResponseMessage> deleteFile(@PathVariable String id, @RequestParam(value = "versionId", required = false) Integer versionId) {
        if(versionId == null) {
            fileStorageService.deleteFile(id); 
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(String.format("All file versions deleted for fileId: %s", id)));
        }else {
            fileStorageService.deleteFileVersion(id, versionId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(String.format("File version %s deleted for fileId: %s", versionId, id)));
        }
    }
}
