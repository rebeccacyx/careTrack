package com.careapp.controller;

import com.careapp.domain.Worker;
import com.careapp.dto.WorkerPhotoUploadRequest;
import com.careapp.service.WorkerService;
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

@WebMvcTest(WorkerController.class)
class WorkerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkerService workerService;

    private Worker mockWorker;

    @BeforeEach
    void setUp() {
        mockWorker = new Worker();
        mockWorker.setId("worker001");
        mockWorker.setName("Alice");
        mockWorker.setOrganizationId("org001");
    }

    // ========== Create Worker ==========
    @Test
    void testCreateWorkerSuccess() throws Exception {
        Mockito.when(workerService.createWorker(Mockito.any(Worker.class))).thenReturn(mockWorker);

        String body = "{ \"name\":\"Alice\", \"organizationId\":\"org001\" }";

        mockMvc.perform(post("/api/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker created successfully!"))
                .andExpect(jsonPath("$.data.id").value("worker001"));
    }

    @Test
    void testCreateWorkerFailure() throws Exception {
        Mockito.when(workerService.createWorker(Mockito.any(Worker.class)))
                .thenThrow(new RuntimeException("DB Error"));

        String body = "{ \"name\":\"Alice\" }";

        mockMvc.perform(post("/api/workers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to create worker: DB Error"));
    }

    // ========== Get All Workers ==========
    @Test
    void testGetAllWorkers() throws Exception {
        Mockito.when(workerService.getAllWorkers()).thenReturn(Collections.singletonList(mockWorker));

        mockMvc.perform(get("/api/workers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("worker001"))
                .andExpect(jsonPath("$.msg").value("Workers retrieved successfully!"));
    }

    // ========== Get Worker By ID ==========
    @Test
    void testGetWorkerByIdFound() throws Exception {
        Mockito.when(workerService.getWorkerById("worker001")).thenReturn(Optional.of(mockWorker));

        mockMvc.perform(get("/api/workers/worker001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker retrieved successfully!"));
    }

    @Test
    void testGetWorkerByIdNotFound() throws Exception {
        Mockito.when(workerService.getWorkerById("notExist")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/workers/notExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Worker not found!"));
    }

    // ========== Delete Worker ==========
    @Test
    void testDeleteWorkerSuccess() throws Exception {
        Mockito.when(workerService.deleteWorker("worker001")).thenReturn(true);

        mockMvc.perform(delete("/api/workers/worker001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker deleted successfully!"));
    }

    @Test
    void testDeleteWorkerNotFound() throws Exception {
        Mockito.when(workerService.deleteWorker("worker001")).thenReturn(false);

        mockMvc.perform(delete("/api/workers/worker001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Worker not found!"));
    }

    // ========== Activate / Deactivate ==========
    @Test
    void testActivateWorkerSuccess() throws Exception {
        Mockito.when(workerService.activateWorker("worker001")).thenReturn(mockWorker);

        mockMvc.perform(post("/api/workers/worker001/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker activated successfully!"));
    }

    @Test
    void testDeactivateWorkerNotFound() throws Exception {
        Mockito.when(workerService.deactivateWorker("notExist")).thenReturn(null);

        mockMvc.perform(post("/api/workers/notExist/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Worker not found!"));
    }

    // ========== Update Worker ==========
    @Test
    void testUpdateWorkerSuccess() throws Exception {
        Mockito.when(workerService.updateWorker(Mockito.eq("worker001"), Mockito.any(Worker.class)))
                .thenReturn(mockWorker);

        String body = "{ \"name\":\"Updated Alice\" }";

        mockMvc.perform(put("/api/workers/worker001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker updated successfully!"));
    }

    @Test
    void testUpdateWorkerNotFound() throws Exception {
        Mockito.when(workerService.updateWorker(Mockito.eq("notExist"), Mockito.any(Worker.class)))
                .thenReturn(null);

        String body = "{ \"name\":\"Updated Alice\" }";

        mockMvc.perform(put("/api/workers/notExist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Worker not found!"));
    }

    // ========== Upload Worker Photo ==========
    @Test
    void testUploadWorkerPhotoSuccess() throws Exception {
        Mockito.when(workerService.uploadWorkerPhoto(Mockito.any(WorkerPhotoUploadRequest.class)))
                .thenReturn(mockWorker);

        String body = "{ \"workerId\":\"worker001\", \"photoUrl\":\"http://test/photo.jpg\" }";

        mockMvc.perform(post("/api/workers/upload-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker photo uploaded successfully!"));
    }

    @Test
    void testUploadWorkerPhotoMissingId() throws Exception {
        String body = "{ \"photoUrl\":\"http://test/photo.jpg\" }";

        mockMvc.perform(post("/api/workers/upload-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Worker ID is required!"));
    }

    // ========== Get Available Workers ==========
    @Test
    void testGetAvailableWorkers() throws Exception {
        Mockito.when(workerService.getAvailableWorkers("org001"))
                .thenReturn(Collections.singletonList(mockWorker));

        mockMvc.perform(get("/api/workers/organization/org001/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Available workers retrieved successfully!"));
    }

    // ========== Clear Daily Schedule ==========
    @Test
    void testClearDailySchedule() throws Exception {
        Mockito.when(workerService.clearDailySchedule("org001", "2025-10-17"))
                .thenReturn(3);

        mockMvc.perform(delete("/api/workers/organization/org001/daily-schedule/2025-10-17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Daily schedule cleared successfully!"))
                .andExpect(jsonPath("$.data").value("Cleared 3 worker schedules"));
    }

    // ========== Batch Upload Photos ==========
    @Test
    void testBatchUploadWorkerPhotos() throws Exception {
        Mockito.when(workerService.batchUploadWorkerPhotos(Mockito.anyMap()))
                .thenReturn(Collections.singletonList(mockWorker));

        String body = "{ \"worker001\":\"http://photo.jpg\" }";

        mockMvc.perform(post("/api/workers/batch-upload-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Successfully uploaded photos for 1 workers!"));
    }

    @Test
    void testBatchUploadWorkerPhotosEmpty() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/workers/batch-upload-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Photo upload data is required!"));
    }

    // ========== Delete Worker Photo ==========
    @Test
    void testDeleteWorkerPhotoSuccess() throws Exception {
        Mockito.when(workerService.deleteWorkerPhoto("worker001")).thenReturn(mockWorker);

        mockMvc.perform(delete("/api/workers/worker001/photo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker photo deleted successfully!"));
    }

    @Test
    void testDeleteWorkerPhotoNotFound() throws Exception {
        Mockito.when(workerService.deleteWorkerPhoto("worker001")).thenReturn(null);

        mockMvc.perform(delete("/api/workers/worker001/photo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Worker not found!"));
    }
}
