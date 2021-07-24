package com.metadata.filestorage.response;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ResponseFile {
    private String fileId;
    private String name;
    private String type;
    private List<Versions> versions;

    public ResponseFile(String fileId, String name, String type) {
        this.fileId = fileId;
        this.name = name;
        this.type = type;
        versions = new ArrayList<>();
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Versions> getVersions() {
        return versions;
    }

    public void setVersions(List<Versions> versions) {
        this.versions = versions;
    }
    
    public void addVersion(Versions versions) {
        this.versions.add(versions);
    }


    public static class Versions {
        private Integer versionId;
        private String downloadUrl;
        private long size;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        public Versions(Integer versionId, String downloadUrl, long size, Timestamp createdAt, Timestamp updatedAt) {
            this.versionId = versionId;
            this.downloadUrl = downloadUrl;
            this.size = size;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public Integer getVersionId() {
            return versionId;
        }
        
        public void setVersionId(Integer versionId) {
            this.versionId = versionId;
        }
        
        public String getDownloadUrl() {
            return downloadUrl;
        }
        
        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
        
        public long getSize() {
            return size;
        }
        
        public void setSize(long size) {
            this.size = size;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Timestamp createdAt) {
            this.createdAt = createdAt;
        }

        public Timestamp getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Timestamp updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}