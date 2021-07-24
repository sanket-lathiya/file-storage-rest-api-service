package com.metadata.filestorage.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "file_version")
public class FileVersion {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(name = "file_id")
    private String fileId;
    
    @Column(name = "version_id", nullable = false)
    private Integer versionId;
    
    @Lob
    @Column(nullable = false)
    private byte[] content;
    
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;
    
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Timestamp updatedAt;
    
    public FileVersion() {
    }

    public FileVersion(Integer versionId, byte[] content) {
        this.versionId = versionId;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
        result = prime * result + ((versionId == null) ? 0 : versionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FileVersion other = (FileVersion)obj;
        if (fileId == null) {
            if (other.fileId != null) return false;
        }
        else if (!fileId.equals(other.fileId)) return false;
        if (versionId == null) {
            if (other.versionId != null) return false;
        }
        else if (!versionId.equals(other.versionId)) return false;
        return true;
    }
    
}
