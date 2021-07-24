package com.metadata.filestorage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.metadata.filestorage.model.File;
import com.metadata.filestorage.model.FileVersion;
import com.metadata.filestorage.repository.FileStorageRepository;
import com.metadata.filestorage.repository.FileVersionRepository;

@SpringBootTest
@AutoConfigureMockMvc
class FilestorageApplicationTests {

    @MockBean
    private FileStorageRepository fileStorageRepository;

    @MockBean
    private FileVersionRepository fileVersionRepository;

    @Autowired 
    private MockMvc mockMvc;

    private final String contentType = "text/plain";

    private final String fileId1 = "fileId1";
    private final String fileVersionId1 = "fileVersionId1";
    private final String fileName1 = "sample-file-1.txt";
    private final String fileContent1 = "This is the file content 1";

    private final String fileId2 = "fileId2";
    private final String fileVersionId21 = "fileVersionId21";
    private final String fileName2 = "sample-file-2.txt";
    private final String fileContent2 = "This is the file content 2";


    @BeforeEach
    private void setUp() throws Exception {
        File file1 = new File(fileName1, contentType);
        Field id1 = file1.getClass().getDeclaredField("id");
        id1.setAccessible(true);
        id1.set(file1, fileId1);
        FileVersion fileVersion1 = new FileVersion(1, fileContent1.getBytes());
        fileVersion1.setFileId(fileId1);
        Field fvId1 = fileVersion1.getClass().getDeclaredField("id");
        fvId1.setAccessible(true);
        fvId1.set(fileVersion1, fileVersionId1);
        file1.setFileVersions(new HashSet<>(Arrays.asList(fileVersion1)));

        File file2 = new File(fileName2, contentType);
        Field id2 = file2.getClass().getDeclaredField("id");
        id2.setAccessible(true);
        id2.set(file2, fileId2);
        FileVersion fileVersion2 = new FileVersion(1, fileContent2.getBytes());
        fileVersion2.setFileId(fileId2);
        Field fvId2 = fileVersion2.getClass().getDeclaredField("id");
        fvId2.setAccessible(true);
        fvId2.set(fileVersion2, fileVersionId21);
        file2.setFileVersions(new HashSet<>(Arrays.asList(fileVersion1)));

        Mockito.when(fileStorageRepository.save(ArgumentMatchers.any(File.class))).thenReturn(file1);
        Mockito.when(fileStorageRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(Arrays.asList(file1, file2));
        Mockito.when(fileStorageRepository.findById(ArgumentMatchers.anyString())).thenReturn(Optional.of(file1));
        Mockito.when(fileVersionRepository.findByFileIdAndVersionId(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(Optional.of(fileVersion1));
        Mockito.when(fileStorageRepository.findAll()).thenReturn(Arrays.asList(file1, file2));
    }

    @Test
    void uploadNewFileTest() throws Exception {

        MockMultipartFile sampleFile = new MockMultipartFile(
            "file",
            fileName1,
            contentType,
            fileContent1.getBytes());

        MockMultipartHttpServletRequestBuilder multipartRequest = MockMvcRequestBuilders.multipart("/filestorage-api/v1/files");

        mockMvc.perform(multipartRequest.file(sampleFile))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.fileId").value(fileId1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(fileName1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(contentType))
        .andExpect(MockMvcResultMatchers.jsonPath("$.versions[0].versionId").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.versions[0].downloadUrl").value(String.format("http://localhost/filestorage-api/v1/files/%s?versionId=1", fileId1)));
    }

    @Test
    void uploadMultipleFilesTest() throws Exception {

        MockMultipartFile sampleFile1 = new MockMultipartFile(
            "files",
            fileName1,
            contentType,
            fileContent1.getBytes());

        MockMultipartFile sampleFile2 = new MockMultipartFile(
            "files",
            fileName2,
            contentType,
            fileContent2.getBytes());

        MockMultipartHttpServletRequestBuilder multipartRequest = MockMvcRequestBuilders.multipart("/filestorage-api/v1/multiple-files");

        mockMvc.perform(multipartRequest.file(sampleFile1).file(sampleFile2))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fileId").value(fileId1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(fileName1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].type").value(contentType))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].versions[0].versionId").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].versions[0].downloadUrl").value(String.format("http://localhost/filestorage-api/v1/files/%s?versionId=1", fileId1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].fileId").value(fileId2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value(fileName2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].type").value(contentType))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].versions[0].versionId").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].versions[0].downloadUrl").value(String.format("http://localhost/filestorage-api/v1/files/%s?versionId=1", fileId2)));
    }

    @Test
    void createNewFileVersionTest() throws Exception {

        MockMultipartFile sampleFile1 = new MockMultipartFile(
            "file",
            fileName1,
            contentType,
            fileContent1.getBytes());

        MockMultipartHttpServletRequestBuilder multipartRequest = MockMvcRequestBuilders.multipart(String.format("/filestorage-api/v1/files/%s", fileId1));

        multipartRequest.with(new RequestPostProcessor() {

            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod(HttpMethod.PUT.name());
                return request;
            }
        });

        mockMvc.perform(multipartRequest.file(sampleFile1))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateExistingFileVersionTest() throws Exception {

        MockMultipartFile sampleFile1 = new MockMultipartFile(
            "file",
            fileName1,
            contentType,
            fileContent1.getBytes());

        MockMultipartHttpServletRequestBuilder multipartRequest = MockMvcRequestBuilders.multipart(String.format("/filestorage-api/v1/files/%s", fileId1));

        multipartRequest.with(new RequestPostProcessor() {

            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod(HttpMethod.PUT.name());
                return request;
            }
        });

        mockMvc.perform(multipartRequest.file(sampleFile1).queryParam("versionId", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void listAllFilesTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/filestorage-api/v1/files/"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].fileId").value(fileId1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value(fileName1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].type").value(contentType))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].versions[0].versionId").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].versions[0].downloadUrl").value(String.format("http://localhost/filestorage-api/v1/files/%s?versionId=1", fileId1)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].fileId").value(fileId2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].name").value(fileName2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].type").value(contentType))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].versions[0].versionId").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].versions[0].downloadUrl").value(String.format("http://localhost/filestorage-api/v1/files/%s?versionId=1", fileId2)));
    }

    @Test
    void getFileWithoutVersionIdTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/filestorage-api/v1/files/%s", fileId1)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName1 + "\""));
    }

    @Test
    void getFileWithVersionIdTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/filestorage-api/v1/files/%s", fileId1)).queryParam("versionId", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName1 + "\""));
    }

    @Test
    void deleteFileWithVersionIdTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/filestorage-api/v1/files/%s", fileId1)).queryParam("versionId", "1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteFileWithAllVersionsTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/filestorage-api/v1/files/%s", fileId1)))
        .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
