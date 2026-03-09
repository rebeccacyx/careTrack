package com.careapp.service;

import com.careapp.domain.Notification;
import com.careapp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    /**
     * Create a new notification
     * @param notification The notification to create
     * @return The created notification
     */
    public Notification createNotification(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }
        if (notification.getIsRead() == null) {
            notification.setIsRead(false);
        }
        if (notification.getPriority() == null || notification.getPriority().isEmpty()) {
            notification.setPriority("normal");
        }
        return notificationRepository.save(notification);
    }
    
    /**
     * Get notification by ID
     * @param id The notification ID
     * @return Optional containing the notification if found
     */
    public Optional<Notification> getNotificationById(String id) {
        return notificationRepository.findById(id);
    }
    
    /**
     * Get all notifications
     * @return List of all notifications
     */
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    /**
     * Get notifications by recipient ID
     * @param recipientId The recipient user ID
     * @return List of notifications for the recipient
     */
    public List<Notification> getNotificationsByRecipient(String recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }
    
    /**
     * Get unread notifications by recipient ID
     * @param recipientId The recipient user ID
     * @return List of unread notifications
     */
    public List<Notification> getUnreadNotifications(String recipientId) {
        return notificationRepository.findByRecipientIdAndIsReadOrderByPriorityDescCreatedAtDesc(recipientId, false);
    }
    
    /**
     * Get notifications by recipient and type
     * @param recipientId The recipient user ID
     * @param type The notification type
     * @return List of notifications
     */
    public List<Notification> getNotificationsByRecipientAndType(String recipientId, String type) {
        return notificationRepository.findByRecipientIdAndType(recipientId, type);
    }
    
    /**
     * Get notifications by recipient and category
     * @param recipientId The recipient user ID
     * @param category The notification category
     * @return List of notifications
     */
    public List<Notification> getNotificationsByRecipientAndCategory(String recipientId, String category) {
        return notificationRepository.findByRecipientIdAndCategory(recipientId, category);
    }
    
    /**
     * Mark notification as read
     * @param notificationId The notification ID
     * @return The updated notification, or null if not found
     */
    public Notification markAsRead(String notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }
        return null;
    }
    
    /**
     * Mark all notifications as read for a recipient
     * @param recipientId The recipient user ID
     * @return Number of notifications marked as read
     */
    public int markAllAsRead(String recipientId) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientIdAndIsRead(recipientId, false);
        int count = 0;
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
            count++;
        }
        return count;
    }
    
    /**
     * Delete notification
     * @param notificationId The notification ID
     * @return True if deleted successfully, false otherwise
     */
    public boolean deleteNotification(String notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            return true;
        }
        return false;
    }
    
    /**
     * Delete all notifications for a recipient
     * @param recipientId The recipient user ID
     * @return Number of notifications deleted
     */
    public int deleteAllNotifications(String recipientId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);
        int count = notifications.size();
        notificationRepository.deleteAll(notifications);
        return count;
    }
    
    /**
     * Get count of unread notifications
     * @param recipientId The recipient user ID
     * @return Count of unread notifications
     */
    public long getUnreadCount(String recipientId) {
        return notificationRepository.countByRecipientIdAndIsRead(recipientId, false);
    }
    
    /**
     * Get count of urgent notifications
     * @param recipientId The recipient user ID
     * @return Count of urgent notifications
     */
    public long getUrgentCount(String recipientId) {
        return notificationRepository.countByRecipientIdAndPriority(recipientId, "urgent");
    }
    
    /**
     * Create notification for task assignment
     * @param workerId The worker ID
     * @param taskId The task ID
     * @param taskTitle The task title
     * @param assignedBy The user who assigned the task
     * @return The created notification
     */
    public Notification createTaskAssignedNotification(String workerId, String taskId, String taskTitle, String assignedBy) {
        Notification notification = new Notification();
        notification.setRecipientId(workerId);
        notification.setRecipientRole("WORKER");
        notification.setSenderId(assignedBy);
        notification.setType("TASK_ASSIGNED");
        notification.setTitle("New Task Assigned");
        notification.setMessage("You have been assigned a new task: " + taskTitle);
        notification.setCategory("task");
        notification.setPriority("normal");
        notification.setRelatedEntityType("task");
        notification.setRelatedEntityId(taskId);
        notification.setActionUrl("/tasks/" + taskId);
        return createNotification(notification);
    }
    
    /**
     * Create notification for task completion
     * @param managerId The manager ID
     * @param taskId The task ID
     * @param taskTitle The task title
     * @param completedBy The worker who completed the task
     * @return The created notification
     */
    public Notification createTaskCompletedNotification(String managerId, String taskId, String taskTitle, String completedBy) {
        Notification notification = new Notification();
        notification.setRecipientId(managerId);
        notification.setRecipientRole("MANAGER");
        notification.setSenderId(completedBy);
        notification.setType("TASK_COMPLETED");
        notification.setTitle("Task Completed");
        notification.setMessage("Task \"" + taskTitle + "\" has been marked as completed and awaits your approval.");
        notification.setCategory("task");
        notification.setPriority("high");
        notification.setRelatedEntityType("task");
        notification.setRelatedEntityId(taskId);
        notification.setActionUrl("/tasks/" + taskId);
        return createNotification(notification);
    }
    
    /**
     * Create notification for schedule update
     * @param workerId The worker ID
     * @param scheduleDate The schedule date
     * @param shiftType The shift type
     * @param updatedBy The user who updated the schedule
     * @return The created notification
     */
    public Notification createScheduleUpdatedNotification(String workerId, String scheduleDate, String shiftType, String updatedBy) {
        Notification notification = new Notification();
        notification.setRecipientId(workerId);
        notification.setRecipientRole("WORKER");
        notification.setSenderId(updatedBy);
        notification.setType("SCHEDULE_UPDATED");
        notification.setTitle("Schedule Updated");
        notification.setMessage("Your schedule for " + scheduleDate + " (" + shiftType + " shift) has been updated.");
        notification.setCategory("schedule");
        notification.setPriority("normal");
        notification.setRelatedEntityType("schedule");
        notification.setActionUrl("/schedules");
        return createNotification(notification);
    }
    
    /**
     * Create notification for new message
     * @param recipientId The recipient user ID
     * @param messageId The message ID
     * @param senderName The sender name
     * @param messageSubject The message subject
     * @return The created notification
     */
    public Notification createMessageReceivedNotification(String recipientId, String messageId, String senderName, String messageSubject) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setSenderName(senderName);
        notification.setType("MESSAGE_RECEIVED");
        notification.setTitle("New Message");
        notification.setMessage("You have received a new message from " + senderName + ": " + messageSubject);
        notification.setCategory("message");
        notification.setPriority("normal");
        notification.setRelatedEntityType("message");
        notification.setRelatedEntityId(messageId);
        notification.setActionUrl("/messages/" + messageId);
        return createNotification(notification);
    }
    
    /**
     * Create notification for budget update
     * @param recipientId The recipient user ID (POA/FM)
     * @param budgetId The budget ID
     * @param budgetCategory The budget category
     * @param updatedBy The user who updated the budget
     * @return The created notification
     */
    public Notification createBudgetUpdatedNotification(String recipientId, String budgetId, String budgetCategory, String updatedBy) {
        Notification notification = new Notification();
        notification.setRecipientId(recipientId);
        notification.setSenderId(updatedBy);
        notification.setType("BUDGET_UPDATED");
        notification.setTitle("Budget Updated");
        notification.setMessage("The budget for " + budgetCategory + " has been updated.");
        notification.setCategory("budget");
        notification.setPriority("high");
        notification.setRelatedEntityType("budget");
        notification.setRelatedEntityId(budgetId);
        notification.setActionUrl("/budget");
        return createNotification(notification);
    }
    
    /**
     * Clean up expired notifications
     * @return Number of notifications deleted
     */
    public int cleanupExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findByExpiresAtBefore(LocalDateTime.now());
        int count = expiredNotifications.size();
        notificationRepository.deleteAll(expiredNotifications);
        return count;
    }
    
    /**
     * Get notifications by organization
     * @param organizationId The organization ID
     * @return List of notifications
     */
    public List<Notification> getNotificationsByOrganization(String organizationId) {
        return notificationRepository.findByOrganizationId(organizationId);
    }
    
    /**
     * Broadcast notification to all users with a specific role
     * @param role The recipient role
     * @param title The notification title
     * @param message The notification message
     * @param organizationId The organization ID
     * @return List of created notifications
     */
    public List<Notification> broadcastToRole(String role, String title, String message, String organizationId) {
        // This would typically iterate through users with the specified role
        // For now, we'll create a placeholder notification
        Notification notification = new Notification();
        notification.setRecipientRole(role);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType("BROADCAST");
        notification.setCategory("system");
        notification.setPriority("normal");
        notification.setOrganizationId(organizationId);
        
        return List.of(createNotification(notification));
    }
}



