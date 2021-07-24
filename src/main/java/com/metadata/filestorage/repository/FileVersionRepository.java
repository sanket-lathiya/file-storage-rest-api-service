package com.metadata.filestorage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metadata.filestorage.model.FileVersion;

@Repository
public interface FileVersionRepository  extends JpaRepository<FileVersion, String>{

    Optional<FileVersion> findByFileIdAndVersionId(String fileId, Integer versionId);
}
