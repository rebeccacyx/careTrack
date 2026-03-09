package com.careapp.dto;

import com.careapp.domain.Message;
import java.time.format.DateTimeFormatter;

/**
 * DTO for Message to match frontend field structure
 */
public class MessageDTO {
    private String id;
    private String subject;
    private String from;  // Maps to fromUserName
    private String to;    // Maps to toUserName
    private String date;  // Formatted createdAt
    private String status;
    private String content;
    
    // Additional fields for internal use
    private String fromUserId;
    private String toUserId;
    private String category;
    private String organizationId;
    
    // Date formatter for frontend
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public MessageDTO() {}
    
    /**
     * Convert from Message entity to DTO
     */
    public static MessageDTO fromEntity(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSubject(message.getSubject());
        dto.setFrom(message.getFromUserName());
        dto.setTo(message.getToUserName());
        dto.setStatus(message.getStatus());
        dto.setContent(message.getContent());
        dto.setFromUserId(message.getFromUserId());
        dto.setToUserId(message.getToUserId());
        dto.setCategory(message.getCategory());
        dto.setOrganizationId(message.getOrganizationId());
        
        // Format date
        if (message.getCreatedAt() != null) {
            dto.setDate(message.getCreatedAt().format(DATE_FORMATTER));
        }
        
        return dto;
    }
    
    /**
     * Convert from DTO to Message entity
     */
    public Message toEntity() {
        Message message = new Message();
        message.setId(this.id);
        message.setSubject(this.subject);
        message.setFromUserName(this.from);
        message.setToUserName(this.to);
        message.setStatus(this.status);
        message.setContent(this.content);
        message.setFromUserId(this.fromUserId);
        message.setToUserId(this.toUserId);
        message.setCategory(this.category != null ? this.category : "general");
        message.setOrganizationId(this.organizationId);
        
        return message;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getFromUserId() {
        return fromUserId;
    }
    
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }
    
    public String getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    @Override
    public String toString() {
        return "MessageDTO{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


