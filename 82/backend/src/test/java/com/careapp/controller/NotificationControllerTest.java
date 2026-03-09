package com.careapp.controller;

import com.careapp.domain.Notification;
import com.careapp.service.NotificationService;
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

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    private Notification mockNotification;

    @BeforeEach
    void setUp() {
        mockNotification = new Notification();
        mockNotification.setId("n001");
        mockNotification.setRecipientId("user001");
        mockNotification.setTitle("Test Notification");
        mockNotification.setMessage("This is a test message");
        mockNotification.setType("TASK");
        mockNotification.setCategory("General");
        mockNotification.setIsRead(false);
    }

    // ========== Create Notification ==========
    @Test
    void testCreateNotificationSuccess() throws Exception {
        Mockito.when(notificationService.createNotification(Mockito.any(Notification.class)))
                .thenReturn(mockNotification);

        String body = "{"
                + "\"recipientId\":\"user001\","
                + "\"title\":\"Test Notification\","
                + "\"message\":\"This is a test message\""
                + "}";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Notification created successfully!"))
                .andExpect(jsonPath("$.data.id").value("n001"));
    }

    @Test
    void testCreateNotificationError() throws Exception {
        Mockito.when(notificationService.createNotification(Mockito.any(Notification.class)))
                .thenThrow(new RuntimeException("DB error"));

        String body = "{ \"recipientId\":\"user001\" }";

        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to create notification: DB error"));
    }

    // ========== Get All Notifications ==========
    @Test
    void testGetAllNotificationsSuccess() throws Exception {
        Mockito.when(notificationService.getAllNotifications()).thenReturn(Collections.singletonList(mockNotification));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Notifications retrieved successfully!"))
                .andExpect(jsonPath("$.data[0].id").value("n001"));
    }

    // ========== Get Notification By ID ==========
    @Test
    void testGetNotificationByIdSuccess() throws Exception {
        Mockito.when(notificationService.getNotificationById("n001")).thenReturn(Optional.of(mockNotification));

        mockMvc.perform(get("/api/notifications/n001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Notification retrieved successfully!"));
    }

    @Test
    void testGetNotificationByIdNotFound() throws Exception {
        Mockito.when(notificationService.getNotificationById("notExist")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/notifications/notExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Notification not found!"));
    }

    // ========== Get Notifications By Recipient ==========
    @Test
    void testGetNotificationsByRecipient() throws Exception {
        Mockito.when(notificationService.getNotificationsByRecipient("user001"))
                .thenReturn(Collections.singletonList(mockNotification));

        mockMvc.perform(get("/api/notifications/recipient/user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Notifications retrieved successfully!"));
    }

    // ========== Unread Notifications ==========
    @Test
    void testGetUnreadNotifications() throws Exception {
        Mockito.when(notificationService.getUnreadNotifications("user001"))
                .thenReturn(Collections.singletonList(mockNotification));

        mockMvc.perform(get("/api/notifications/unread").header("X-User-Id", "user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Unread notifications retrieved successfully!"));
    }

    @Test
    void testGetUnreadCount() throws Exception {
        Mockito.when(notificationService.getUnreadCount("user001")).thenReturn(3L);

        mockMvc.perform(get("/api/notifications/unread/count").header("X-User-Id", "user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Unread count retrieved successfully!"))
                .andExpect(jsonPath("$.data").value(3));
    }

    @Test
    void testGetUrgentCount() throws Exception {
        Mockito.when(notificationService.getUrgentCount("user001")).thenReturn(1L);

        mockMvc.perform(get("/api/notifications/urgent/count").header("X-User-Id", "user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Urgent count retrieved successfully!"))
                .andExpect(jsonPath("$.data").value(1));
    }

    // ========== Mark As Read ==========
    @Test
    void testMarkAsReadSuccess() throws Exception {
        Mockito.when(notificationService.markAsRead("n001")).thenReturn(mockNotification);

        mockMvc.perform(put("/api/notifications/n001/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Notification marked as read!"));
    }

    @Test
    void testMarkAsReadNotFound() throws Exception {
        Mockito.when(notificationService.markAsRead("notExist")).thenReturn(null);

        mockMvc.perform(put("/api/notifications/notExist/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Notification not found!"));
    }

    // ========== Mark All As Read ==========
    @Test
    void testMarkAllAsRead() throws Exception {
        Mockito.when(notificationService.markAllAsRead("user001")).thenReturn(5);

        mockMvc.perform(put("/api/notifications/read-all").header("X-User-Id", "user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Marked 5 notifications as read!"));
    }

    // ========== Delete Notification ==========
    @Test
    void testDeleteNotificationSuccess() throws Exception {
        Mockito.when(notificationService.deleteNotification("n001")).thenReturn(true);

        mockMvc.perform(delete("/api/notifications/n001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Notification deleted successfully!"));
    }

    @Test
    void testDeleteNotificationNotFound() throws Exception {
        Mockito.when(notificationService.deleteNotification("n001")).thenReturn(false);

        mockMvc.perform(delete("/api/notifications/n001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Notification not found!"));
    }

    // ========== Delete All Notifications ==========
    @Test
    void testDeleteAllNotifications() throws Exception {
        Mockito.when(notificationService.deleteAllNotifications("user001")).thenReturn(10);

        mockMvc.perform(delete("/api/notifications/delete-all").header("X-User-Id", "user001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Deleted 10 notifications!"));
    }

    // ========== Create Task Assigned Notification ==========
    @Test
    void testCreateTaskAssignedNotification() throws Exception {
        Mockito.when(notificationService.createTaskAssignedNotification(
                        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(mockNotification);

        String body = "{"
                + "\"workerId\":\"w001\","
                + "\"taskId\":\"t001\","
                + "\"taskTitle\":\"Medication\","
                + "\"assignedBy\":\"manager001\""
                + "}";

        mockMvc.perform(post("/api/notifications/task-assigned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Task assigned notification created!"));
    }

    // ========== Broadcast To Role ==========
    @Test
    void testBroadcastToRole() throws Exception {
        Mockito.when(notificationService.broadcastToRole(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(Collections.singletonList(mockNotification));

        String body = "{"
                + "\"role\":\"Worker\","
                + "\"title\":\"System Update\","
                + "\"message\":\"All workers please check schedule\""
                + "}";

        mockMvc.perform(post("/api/notifications/broadcast")
                        .header("X-Organization-Id", "org001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Broadcast notification sent!"));
    }

    // ========== Cleanup Expired Notifications ==========
    @Test
    void testCleanupExpiredNotifications() throws Exception {
        Mockito.when(notificationService.cleanupExpiredNotifications()).thenReturn(7);

        mockMvc.perform(post("/api/notifications/cleanup"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Cleaned up 7 expired notifications!"));
    }
}

