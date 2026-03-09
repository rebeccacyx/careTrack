package com.careapp.controller;

import com.careapp.domain.Notification;
import com.careapp.service.NotificationService;
import com.careapp.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Create a new notification
     * POST /api/notifications
     */
    @PostMapping
    public Result<Notification> createNotification(@RequestBody Notification notification) {
        try {
            Notification createdNotification = notificationService.createNotification(notification);
            return Result.success(createdNotification, "Notification created successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create notification: " + e.getMessage());
        }
    }
    
    /**
     * Get all notifications
     * GET /api/notifications
     */
    @GetMapping
    public Result<List<Notification>> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            return Result.success(notifications, "Notifications retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get notification by ID
     * GET /api/notifications/{id}
     */
    @GetMapping("/{id}")
    public Result<Notification> getNotificationById(@PathVariable String id) {
        try {
            Optional<Notification> notification = notificationService.getNotificationById(id);
            if (notification.isPresent()) {
                return Result.success(notification.get(), "Notification retrieved successfully!");
            } else {
                return Result.error("404", "Notification not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve notification: " + e.getMessage());
        }
    }
    
    /**
     * Get notifications by recipient (current user)
     * GET /api/notifications/my
     */
    @GetMapping("/my")
    public Result<List<Notification>> getMyNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            List<Notification> notifications = notificationService.getNotificationsByRecipient(recipientId);
            return Result.success(notifications, "Notifications retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get notifications by recipient ID
     * GET /api/notifications/recipient/{recipientId}
     */
    @GetMapping("/recipient/{recipientId}")
    public Result<List<Notification>> getNotificationsByRecipient(@PathVariable String recipientId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByRecipient(recipientId);
            return Result.success(notifications, "Notifications retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get unread notifications for current user
     * GET /api/notifications/unread
     */
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            List<Notification> notifications = notificationService.getUnreadNotifications(recipientId);
            return Result.success(notifications, "Unread notifications retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve unread notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get unread notification count
     * GET /api/notifications/unread/count
     */
    @GetMapping("/unread/count")
    public Result<Long> getUnreadCount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            long count = notificationService.getUnreadCount(recipientId);
            return Result.success(count, "Unread count retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve unread count: " + e.getMessage());
        }
    }
    
    /**
     * Get urgent notification count
     * GET /api/notifications/urgent/count
     */
    @GetMapping("/urgent/count")
    public Result<Long> getUrgentCount(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            long count = notificationService.getUrgentCount(recipientId);
            return Result.success(count, "Urgent count retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve urgent count: " + e.getMessage());
        }
    }
    
    /**
     * Get notifications by type
     * GET /api/notifications/type/{type}
     */
    @GetMapping("/type/{type}")
    public Result<List<Notification>> getNotificationsByType(
            @PathVariable String type,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            List<Notification> notifications = notificationService.getNotificationsByRecipientAndType(recipientId, type);
            return Result.success(notifications, "Notifications retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve notifications: " + e.getMessage());
        }
    }
    
    /**
     * Get notifications by category
     * GET /api/notifications/category/{category}
     */
    @GetMapping("/category/{category}")
    public Result<List<Notification>> getNotificationsByCategory(
            @PathVariable String category,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            List<Notification> notifications = notificationService.getNotificationsByRecipientAndCategory(recipientId, category);
            return Result.success(notifications, "Notifications retrieved successfully!");
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve notifications: " + e.getMessage());
        }
    }
    
    /**
     * Mark notification as read
     * PUT /api/notifications/{id}/read
     */
    @PutMapping("/{id}/read")
    public Result<Notification> markAsRead(@PathVariable String id) {
        try {
            Notification notification = notificationService.markAsRead(id);
            if (notification != null) {
                return Result.success(notification, "Notification marked as read!");
            } else {
                return Result.error("404", "Notification not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to mark notification as read: " + e.getMessage());
        }
    }
    
    /**
     * Mark all notifications as read for current user
     * PUT /api/notifications/read-all
     */
    @PutMapping("/read-all")
    public Result<Integer> markAllAsRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            int count = notificationService.markAllAsRead(recipientId);
            return Result.success(count, "Marked " + count + " notifications as read!");
        } catch (Exception e) {
            return Result.error("500", "Failed to mark all notifications as read: " + e.getMessage());
        }
    }
    
    /**
     * Delete notification
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteNotification(@PathVariable String id) {
        try {
            boolean deleted = notificationService.deleteNotification(id);
            if (deleted) {
                return Result.success(true, "Notification deleted successfully!");
            } else {
                return Result.error("404", "Notification not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to delete notification: " + e.getMessage());
        }
    }
    
    /**
     * Delete all notifications for current user
     * DELETE /api/notifications/delete-all
     */
    @DeleteMapping("/delete-all")
    public Result<Integer> deleteAllNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String recipientId = userId != null ? userId : "default-user-001";
            int count = notificationService.deleteAllNotifications(recipientId);
            return Result.success(count, "Deleted " + count + " notifications!");
        } catch (Exception e) {
            return Result.error("500", "Failed to delete all notifications: " + e.getMessage());
        }
    }
    
    /**
     * Create task assigned notification
     * POST /api/notifications/task-assigned
     */
    @PostMapping("/task-assigned")
    public Result<Notification> createTaskAssignedNotification(@RequestBody Map<String, String> body) {
        try {
            String workerId = body.get("workerId");
            String taskId = body.get("taskId");
            String taskTitle = body.get("taskTitle");
            String assignedBy = body.get("assignedBy");
            
            Notification notification = notificationService.createTaskAssignedNotification(
                workerId, taskId, taskTitle, assignedBy);
            return Result.success(notification, "Task assigned notification created!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create notification: " + e.getMessage());
        }
    }
    
    /**
     * Create task completed notification
     * POST /api/notifications/task-completed
     */
    @PostMapping("/task-completed")
    public Result<Notification> createTaskCompletedNotification(@RequestBody Map<String, String> body) {
        try {
            String managerId = body.get("managerId");
            String taskId = body.get("taskId");
            String taskTitle = body.get("taskTitle");
            String completedBy = body.get("completedBy");
            
            Notification notification = notificationService.createTaskCompletedNotification(
                managerId, taskId, taskTitle, completedBy);
            return Result.success(notification, "Task completed notification created!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create notification: " + e.getMessage());
        }
    }
    
    /**
     * Create schedule updated notification
     * POST /api/notifications/schedule-updated
     */
    @PostMapping("/schedule-updated")
    public Result<Notification> createScheduleUpdatedNotification(@RequestBody Map<String, String> body) {
        try {
            String workerId = body.get("workerId");
            String scheduleDate = body.get("scheduleDate");
            String shiftType = body.get("shiftType");
            String updatedBy = body.get("updatedBy");
            
            Notification notification = notificationService.createScheduleUpdatedNotification(
                workerId, scheduleDate, shiftType, updatedBy);
            return Result.success(notification, "Schedule updated notification created!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create notification: " + e.getMessage());
        }
    }
    
    /**
     * Create message received notification
     * POST /api/notifications/message-received
     */
    @PostMapping("/message-received")
    public Result<Notification> createMessageReceivedNotification(@RequestBody Map<String, String> body) {
        try {
            String recipientId = body.get("recipientId");
            String messageId = body.get("messageId");
            String senderName = body.get("senderName");
            String messageSubject = body.get("messageSubject");
            
            Notification notification = notificationService.createMessageReceivedNotification(
                recipientId, messageId, senderName, messageSubject);
            return Result.success(notification, "Message received notification created!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create notification: " + e.getMessage());
        }
    }
    
    /**
     * Create budget updated notification
     * POST /api/notifications/budget-updated
     */
    @PostMapping("/budget-updated")
    public Result<Notification> createBudgetUpdatedNotification(@RequestBody Map<String, String> body) {
        try {
            String recipientId = body.get("recipientId");
            String budgetId = body.get("budgetId");
            String budgetCategory = body.get("budgetCategory");
            String updatedBy = body.get("updatedBy");
            
            Notification notification = notificationService.createBudgetUpdatedNotification(
                recipientId, budgetId, budgetCategory, updatedBy);
            return Result.success(notification, "Budget updated notification created!");
        } catch (Exception e) {
            return Result.error("500", "Failed to create notification: " + e.getMessage());
        }
    }
    
    /**
     * Clean up expired notifications
     * POST /api/notifications/cleanup
     */
    @PostMapping("/cleanup")
    public Result<Integer> cleanupExpiredNotifications() {
        try {
            int count = notificationService.cleanupExpiredNotifications();
            return Result.success(count, "Cleaned up " + count + " expired notifications!");
        } catch (Exception e) {
            return Result.error("500", "Failed to cleanup notifications: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast notification to role
     * POST /api/notifications/broadcast
     */
    @PostMapping("/broadcast")
    public Result<List<Notification>> broadcastToRole(
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-Organization-Id", required = false) String organizationId) {
        try {
            String role = body.get("role");
            String title = body.get("title");
            String message = body.get("message");
            String orgId = organizationId != null ? organizationId : "org-001";
            
            List<Notification> notifications = notificationService.broadcastToRole(role, title, message, orgId);
            return Result.success(notifications, "Broadcast notification sent!");
        } catch (Exception e) {
            return Result.error("500", "Failed to broadcast notification: " + e.getMessage());
        }
    }
}



