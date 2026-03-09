package com.careapp.controller;

import com.careapp.domain.Task;
import com.careapp.domain.RecurringTask;
import com.careapp.dto.CreateRecurringTaskRequest;
import com.careapp.service.TaskService;
import com.careapp.service.RecurringTaskService;
import com.careapp.service.TaskRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private RecurringTaskService recurringTaskService;

    @MockBean
    private TaskRequestService taskRequestService;

    private Task task;
    private RecurringTask recurringTask;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId("t1");
        task.setTitle("Medication Reminder");
        task.setStatus("In Progress");

        recurringTask = new RecurringTask();
        recurringTask.setId("r1");
        recurringTask.setTitle("Daily Exercise");
        recurringTask.setIsActive(true);
    }

    @Test
    void testCreateTaskSuccess() throws Exception {
        Mockito.when(taskService.createTask(Mockito.any(Task.class))).thenReturn(task);
        String body = "{\"title\":\"Medication Reminder\"}";
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task created successfully!"))
                .andExpect(jsonPath("$.data.title").value("Medication Reminder"));
    }

    @Test
    void testCreateTaskFailure() throws Exception {
        Mockito.when(taskService.createTask(Mockito.any(Task.class)))
                .thenThrow(new RuntimeException("DB Error"));
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg", containsString("Failed to create task")));
    }

    @Test
    void testGetTaskByIdSuccess() throws Exception {
        Mockito.when(taskService.getTaskById("t1")).thenReturn(Optional.of(task));
        mockMvc.perform(get("/api/tasks/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Medication Reminder"))
                .andExpect(jsonPath("$.msg").value("Task retrieved successfully!"));
    }

    @Test
    void testGetTaskByIdNotFound() throws Exception {
        Mockito.when(taskService.getTaskById("999")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Task not found!"));
    }

    @Test
    void testDeleteTaskSuccess() throws Exception {
        Mockito.when(taskService.deleteTask("t1")).thenReturn(true);
        mockMvc.perform(delete("/api/tasks/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task deleted successfully!"));
    }

    @Test
    void testDeleteTaskNotFound() throws Exception {
        Mockito.when(taskService.deleteTask("t1")).thenReturn(false);
        mockMvc.perform(delete("/api/tasks/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    void testCreateRecurringTaskSuccess() throws Exception {
        Mockito.when(recurringTaskService.createRecurringTask(Mockito.any(CreateRecurringTaskRequest.class)))
                .thenReturn(recurringTask);
        String body = "{\"title\":\"Daily Exercise\"}";
        mockMvc.perform(post("/api/tasks/recurring")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Recurring task template created successfully!"));
    }

    @Test
    void testGetAllRecurringTasks() throws Exception {
        Mockito.when(recurringTaskService.getAllRecurringTasks())
                .thenReturn(List.of(recurringTask));
        mockMvc.perform(get("/api/tasks/recurring"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Daily Exercise"));
    }

    @Test
    void testToggleRecurringTaskStatus() throws Exception {
        RecurringTask toggled = new RecurringTask();
        toggled.setId("r1");
        toggled.setIsActive(false);
        Mockito.when(recurringTaskService.toggleRecurringTaskStatus("r1"))
                .thenReturn(toggled);
        mockMvc.perform(post("/api/tasks/recurring/r1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", containsString("deactivated")));
    }

    @Test
    void testGetAllTasks() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(List.of(task));
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Medication Reminder"))
                .andExpect(jsonPath("$.msg").value("Tasks retrieved successfully!"));
    }

    @Test
    void testGetTasksByStatus() throws Exception {
        Mockito.when(taskService.getTasksByStatus("In Progress")).thenReturn(List.of(task));
        mockMvc.perform(get("/api/tasks/status/In Progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("In Progress"));
    }

    @Test
    void testApproveTaskCompletionSuccess() throws Exception {
        Mockito.when(taskService.approveTaskCompletion(Mockito.eq("t1"), Mockito.anyString()))
                .thenReturn(task);
        mockMvc.perform(post("/api/tasks/t1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"approvalReason\":\"Good job\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task completion approved!"));
    }

    @Test
    void testRejectTaskCompletionNotFound() throws Exception {
        Mockito.when(taskService.rejectTaskCompletion(Mockito.eq("t1"), Mockito.anyString()))
                .thenReturn(null);
        mockMvc.perform(post("/api/tasks/t1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rejectionReason\":\"Incorrect\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    void testUpdateTaskStatusSuccess() throws Exception {
        Mockito.when(taskService.updateTaskStatus(Mockito.eq("t1"), Mockito.anyString()))
                .thenReturn(task);
        mockMvc.perform(put("/api/tasks/t1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"Completed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task status updated successfully!"));
    }

    @Test
    void testGetTaskStats() throws Exception {
        Mockito.when(taskService.getAllTasks()).thenReturn(List.of(task));
        Mockito.when(taskService.getTaskCountByStatus(Mockito.anyString())).thenReturn(1L);
        Mockito.when(taskService.getTaskCountByDueDate(Mockito.any(LocalDate.class))).thenReturn(1L);
        mockMvc.perform(get("/api/tasks/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task statistics retrieved successfully!"));
    }
}
