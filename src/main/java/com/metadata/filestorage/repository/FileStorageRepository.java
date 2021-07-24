package com.metadata.filestorage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metadata.filestorage.model.File;

@Repository
public interface FileStorageRepository extends JpaRepository<File, String>{
    
    Optional<String> getNameById(String id);
}
