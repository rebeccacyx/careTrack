package com.careapp.controller;

import com.careapp.domain.FileRecord;
import com.careapp.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private FileRecord mockFile;

    @BeforeEach
    void setUp() {
        mockFile = new FileRecord();
        mockFile.setId("file001");
        mockFile.setName("report.pdf");
        mockFile.setCategory("report");
        mockFile.setUploadedBy("user001");
        mockFile.setFileUrl("http://localhost/files/report.pdf");
        mockFile.setContentType("application/pdf");
        mockFile.setSize(2048L);
    }

    // ========== Upload File Metadata ==========
    @Test
    void testUploadFileSuccess() throws Exception {
        Mockito.when(fileService.save(Mockito.any(FileRecord.class))).thenReturn(mockFile);

        String body = "{"
                + "\"name\":\"report.pdf\","
                + "\"category\":\"report\","
                + "\"uploadedBy\":\"user001\","
                + "\"fileUrl\":\"http://localhost/files/report.pdf\","
                + "\"contentType\":\"application/pdf\","
                + "\"size\":2048,"
                + "\"comment\":\"monthly report\""
                + "}";

        mockMvc.perform(post("/api/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("File metadata saved successfully!"))
                .andExpect(jsonPath("$.data.name").value("report.pdf"));
    }

    @Test
    void testUploadFileMissingFields() throws Exception {
        String body = "{ \"category\": \"report\" }";

        mockMvc.perform(post("/api/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("name and fileUrl are required"));
    }

    @Test
    void testUploadFileInternalError() throws Exception {
        Mockito.when(fileService.save(Mockito.any(FileRecord.class)))
                .thenThrow(new RuntimeException("DB error"));

        String body = "{ \"name\": \"report.pdf\", \"fileUrl\": \"http://test.pdf\" }";

        mockMvc.perform(post("/api/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to save file metadata: DB error"));
    }

    // ========== List Files ==========
    @Test
    void testListAllFiles() throws Exception {
        Mockito.when(fileService.listAll()).thenReturn(Collections.singletonList(mockFile));

        mockMvc.perform(get("/api/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Files retrieved successfully!"))
                .andExpect(jsonPath("$.data[0].name").value("report.pdf"));
    }

    @Test
    void testListFilesByCategory() throws Exception {
        Mockito.when(fileService.listByCategory("report")).thenReturn(Collections.singletonList(mockFile));

        mockMvc.perform(get("/api/files").param("category", "report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("report"))
                .andExpect(jsonPath("$.msg").value("Files retrieved successfully!"));
    }

    @Test
    void testListFilesByUploader() throws Exception {
        Mockito.when(fileService.listByUploader("user001")).thenReturn(Collections.singletonList(mockFile));

        mockMvc.perform(get("/api/files").param("uploadedBy", "user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].uploadedBy").value("user001"))
                .andExpect(jsonPath("$.msg").value("Files retrieved successfully!"));
    }

    // ========== Get File By ID ==========
    @Test
    void testGetFileSuccess() throws Exception {
        Mockito.when(fileService.getById("file001")).thenReturn(Optional.of(mockFile));

        mockMvc.perform(get("/api/files/file001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("File retrieved successfully!"))
                .andExpect(jsonPath("$.data.name").value("report.pdf"));
    }

    @Test
    void testGetFileNotFound() throws Exception {
        Mockito.when(fileService.getById("notExist")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/files/notExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("File not found!"));
    }

    // ========== Update Comment ==========
    @Test
    void testUpdateCommentSuccess() throws Exception {
        mockFile.setComment("updated comment");
        Mockito.when(fileService.updateComment("file001", "updated comment")).thenReturn(mockFile);

        String body = "{ \"comment\": \"updated comment\" }";

        mockMvc.perform(put("/api/files/file001/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Comment updated successfully!"))
                .andExpect(jsonPath("$.data.comment").value("updated comment"));
    }

    @Test
    void testUpdateCommentNotFound() throws Exception {
        Mockito.when(fileService.updateComment("file999", "hello")).thenReturn(null);

        String body = "{ \"comment\": \"hello\" }";

        mockMvc.perform(put("/api/files/file999/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("File not found!"));
    }

    @Test
    void testUpdateCommentError() throws Exception {
        Mockito.when(fileService.updateComment(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new RuntimeException("DB Error"));

        String body = "{ \"comment\": \"test\" }";

        mockMvc.perform(put("/api/files/file001/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to update comment: DB Error"));
    }

    // ========== Delete ==========
    @Test
    void testDeleteFileSuccess() throws Exception {
        Mockito.when(fileService.deleteById("file001")).thenReturn(true);

        mockMvc.perform(delete("/api/files/file001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("File deleted successfully!"));
    }

    @Test
    void testDeleteFileNotFound() throws Exception {
        Mockito.when(fileService.deleteById("file001")).thenReturn(false);

        mockMvc.perform(delete("/api/files/file001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("File not found!"));
    }

    @Test
    void testDeleteFileError() throws Exception {
        Mockito.when(fileService.deleteById("file001"))
                .thenThrow(new RuntimeException("DB Error"));

        mockMvc.perform(delete("/api/files/file001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to delete file: DB Error"));
    }
}

