package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @Field("recipient_id")
    private String recipientId; // User ID of the recipient
    
    @Field("recipient_role")
    private String recipientRole; // Role of recipient (MANAGER, WORKER, FAMILY_MEMBER, POA)
    
    @Field("sender_id")
    private String senderId; // User ID of the sender (can be null for system notifications)
    
    @Field("sender_name")
    private String senderName; // Name of the sender
    
    private String type; // notification type: TASK_ASSIGNED, TASK_COMPLETED, SCHEDULE_UPDATED, MESSAGE_RECEIVED, BUDGET_UPDATED, etc.
    
    private String title; // Notification title
    
    private String message; // Notification message content
    
    @Field("is_read")
    private Boolean isRead; // Whether the notification has been read
    
    private String priority; // normal, high, urgent
    
    @Field("related_entity_type")
    private String relatedEntityType; // Type of related entity (task, schedule, message, budget, etc.)
    
    @Field("related_entity_id")
    private String relatedEntityId; // ID of related entity
    
    @Field("action_url")
    private String actionUrl; // URL to navigate to when notification is clicked
    
    @Field("organization_id")
    private String organizationId;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("read_at")
    private LocalDateTime readAt;
    
    @Field("expires_at")
    private LocalDateTime expiresAt; // Optional expiration time for the notification
    
    private String category; // general, task, schedule, message, budget, system
    
    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.priority = "normal";
    }
    
    public Notification(String recipientId, String type, String title, String message) {
        this();
        this.recipientId = recipientId;
        this.type = type;
        this.title = title;
        this.message = message;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }
    
    public String getRecipientRole() {
        return recipientRole;
    }
    
    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getRelatedEntityType() {
        return relatedEntityType;
    }
    
    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }
    
    public String getRelatedEntityId() {
        return relatedEntityId;
    }
    
    public void setRelatedEntityId(String relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", recipientId='" + recipientId + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", priority='" + priority + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}


