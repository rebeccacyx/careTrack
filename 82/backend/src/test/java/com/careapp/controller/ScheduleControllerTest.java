package com.careapp.controller;

import com.careapp.domain.Schedule;
import com.careapp.dto.DailyScheduleRequest;
import com.careapp.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    private Schedule mockSchedule;

    @BeforeEach
    void setUp() {
        mockSchedule = new Schedule();
        mockSchedule.setId("s001");
        mockSchedule.setWorkerId("w001");
        mockSchedule.setOrganizationId("org001");
        mockSchedule.setShiftType("Morning");
        mockSchedule.setStatus("Assigned");
    }

    // ========== Create Schedule ==========
    @Test
    void testCreateScheduleSuccess() throws Exception {
        Mockito.when(scheduleService.createSchedule(Mockito.any(Schedule.class))).thenReturn(mockSchedule);

        String body = "{"
                + "\"workerId\":\"w001\","
                + "\"organizationId\":\"org001\","
                + "\"shiftType\":\"Morning\""
                + "}";

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedule created successfully!"));
    }

    @Test
    void testCreateScheduleError() throws Exception {
        Mockito.when(scheduleService.createSchedule(Mockito.any(Schedule.class)))
                .thenThrow(new RuntimeException("DB Error"));

        String body = "{ \"workerId\":\"w001\" }";

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to create schedule: DB Error"));
    }

    // ========== Get All ==========
    @Test
    void testGetAllSchedules() throws Exception {
        Mockito.when(scheduleService.getAllSchedules()).thenReturn(Collections.singletonList(mockSchedule));

        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedules retrieved successfully!"))
                .andExpect(jsonPath("$.data[0].id").value("s001"));
    }

    // ========== Get By ID ==========
    @Test
    void testGetScheduleByIdSuccess() throws Exception {
        Mockito.when(scheduleService.getScheduleById("s001")).thenReturn(Optional.of(mockSchedule));

        mockMvc.perform(get("/api/schedules/s001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedule retrieved successfully!"));
    }

    @Test
    void testGetScheduleByIdNotFound() throws Exception {
        Mockito.when(scheduleService.getScheduleById("x")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/schedules/x"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Schedule not found!"));
    }

    // ========== Update ==========
    @Test
    void testUpdateScheduleSuccess() throws Exception {
        Mockito.when(scheduleService.updateSchedule(Mockito.eq("s001"), Mockito.any(Schedule.class)))
                .thenReturn(mockSchedule);

        mockMvc.perform(put("/api/schedules/s001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Done\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedule updated successfully!"));
    }

    @Test
    void testUpdateScheduleNotFound() throws Exception {
        Mockito.when(scheduleService.updateSchedule(Mockito.eq("s001"), Mockito.any(Schedule.class)))
                .thenReturn(null);

        mockMvc.perform(put("/api/schedules/s001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Done\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Schedule not found!"));
    }

    // ========== Delete ==========
    @Test
    void testDeleteScheduleSuccess() throws Exception {
        Mockito.when(scheduleService.deleteSchedule("s001")).thenReturn(true);

        mockMvc.perform(delete("/api/schedules/s001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedule deleted successfully!"));
    }

    @Test
    void testDeleteScheduleNotFound() throws Exception {
        Mockito.when(scheduleService.deleteSchedule("s001")).thenReturn(false);

        mockMvc.perform(delete("/api/schedules/s001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Schedule not found!"));
    }

    // ========== Upload Worker Photo (URL) ==========
    @Test
    void testUploadWorkerPhotoSuccess() throws Exception {
        Mockito.when(scheduleService.uploadWorkerPhoto("s001", "http://img.com/photo.png"))
                .thenReturn(mockSchedule);

        String body = "{ \"photoUrl\": \"http://img.com/photo.png\" }";

        mockMvc.perform(post("/api/schedules/s001/upload-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Worker photo URL updated successfully!"));
    }

    @Test
    void testUploadWorkerPhotoNotFound() throws Exception {
        Mockito.when(scheduleService.uploadWorkerPhoto("s001", "x")).thenReturn(null);

        mockMvc.perform(post("/api/schedules/s001/upload-photo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"photoUrl\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Schedule not found!"));
    }

    // ========== Update Status ==========
    @Test
    void testUpdateScheduleStatusSuccess() throws Exception {
        Mockito.when(scheduleService.updateScheduleStatus("s001", "Completed"))
                .thenReturn(mockSchedule);

        mockMvc.perform(put("/api/schedules/s001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedule status updated successfully!"));
    }

    @Test
    void testUpdateScheduleStatusNotFound() throws Exception {
        Mockito.when(scheduleService.updateScheduleStatus("s001", "Completed")).thenReturn(null);

        mockMvc.perform(put("/api/schedules/s001/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"));
    }

    // ========== Has Schedule On Date ==========
    @Test
    void testHasScheduleOnDate() throws Exception {
        Mockito.when(scheduleService.hasScheduleOnDate("w001", LocalDate.of(2025, 10, 17))).thenReturn(true);

        mockMvc.perform(get("/api/schedules/worker/w001/has-schedule/2025-10-17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedule check completed successfully!"))
                .andExpect(jsonPath("$.data").value(true));
    }

    // ========== Batch Create ==========
    @Test
    void testBatchCreateDailySchedules() throws Exception {
        Mockito.when(scheduleService.batchCreateDailySchedules(Mockito.any(DailyScheduleRequest.class), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(mockSchedule));

        String body = "{"
                + "\"date\":\"2025-10-17\","
                + "\"workerIds\":[\"w001\"]"
                + "}";

        mockMvc.perform(post("/api/schedules/batch-create")
                        .header("X-Organization-Id", "org001")
                        .header("X-User-Id", "m001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Schedules created successfully for 1 workers!"));
    }

    // ========== Batch Update Status ==========
    @Test
    void testBatchUpdateScheduleStatus() throws Exception {
        Mockito.when(scheduleService.batchUpdateScheduleStatus(Mockito.anyList(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(mockSchedule));

        String body = "{ \"scheduleIds\": [\"s001\"], \"status\": \"Completed\" }";

        mockMvc.perform(put("/api/schedules/batch-update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Successfully updated 1 schedules!"));
    }

    @Test
    void testBatchUpdateScheduleStatusMissingFields() throws Exception {
        mockMvc.perform(put("/api/schedules/batch-update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("scheduleIds are required!"));
    }

    // ========== Batch Delete ==========
    @Test
    void testBatchDeleteSchedules() throws Exception {
        Mockito.when(scheduleService.batchDeleteSchedules(Mockito.anyList())).thenReturn(3);

        String body = "{ \"scheduleIds\": [\"s001\", \"s002\", \"s003\"] }";

        mockMvc.perform(delete("/api/schedules/batch-delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Successfully deleted 3 schedules!"));
    }

    // ========== Copy ==========
    @Test
    void testCopySchedulesSuccess() throws Exception {
        Mockito.when(scheduleService.copySchedules(Mockito.any(LocalDate.class), Mockito.any(LocalDate.class),
                        Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(mockSchedule));

        String body = "{"
                + "\"sourceDate\":\"2025-10-17\","
                + "\"targetDate\":\"2025-10-18\""
                + "}";

        mockMvc.perform(post("/api/schedules/copy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Successfully copied 1 schedules from 2025-10-17 to 2025-10-18"));
    }

    @Test
    void testCopySchedulesMissingDates() throws Exception {
        mockMvc.perform(post("/api/schedules/copy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"targetDate\":\"2025-10-18\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("sourceDate and targetDate are required!"));
    }

    // ========== Validate ==========
    @Test
    void testValidateScheduleNoConflict() throws Exception {
        Mockito.when(scheduleService.validateSchedule("w001", LocalDate.of(2025, 10, 17), "Morning"))
                .thenReturn(true);

        mockMvc.perform(get("/api/schedules/validate")
                        .param("workerId", "w001")
                        .param("date", "2025-10-17")
                        .param("shiftType", "Morning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.msg").value("No conflicts found. Schedule can be created."));
    }

    @Test
    void testValidateScheduleConflict() throws Exception {
        Mockito.when(scheduleService.validateSchedule("w001", LocalDate.of(2025, 10, 17), "Morning"))
                .thenReturn(false);

        mockMvc.perform(get("/api/schedules/validate")
                        .param("workerId", "w001")
                        .param("date", "2025-10-17")
                        .param("shiftType", "Morning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false))
                .andExpect(jsonPath("$.msg").value("Conflict found. Worker already has a schedule for this shift."));
    }
}
