package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "messages")
public class Message {
    
    @Id
    private String id;
    
    private String subject;
    
    private String content;
    
    @Field("from_user_id")
    private String fromUserId;
    
    @Field("from_user_name")
    private String fromUserName;
    
    @Field("to_user_id")
    private String toUserId;
    
    @Field("to_user_name")
    private String toUserName;
    
    private String status; // sent, read, archived, deleted
    
    @Field("organization_id")
    private String organizationId;
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("read_at")
    private LocalDateTime readAt;
    
    @Field("is_reply")
    private Boolean isReply; // Whether this message is a reply
    
    @Field("original_message_id")
    private String originalMessageId; // ID of original message if this is a reply
    
    private String category; // general, urgent, notification, system
    
    @Field("attachments")
    private List<Attachment> attachments; // Message attachments
    
    @Field("reply_count")
    private Integer replyCount; // Number of replies to this message
    
    // Inner class for attachments
    public static class Attachment {
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
        
        public Attachment() {}
        
        public Attachment(String fileName, String fileUrl, String fileType, Long fileSize) {
            this.fileName = fileName;
            this.fileUrl = fileUrl;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }
        
        // Getters and Setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getFileUrl() { return fileUrl; }
        public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
        
        public String getFileType() { return fileType; }
        public void setFileType(String fileType) { this.fileType = fileType; }
        
        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    }
    
    // Constructors
    public Message() {
        this.createdAt = LocalDateTime.now();
        this.status = "sent";
        this.isReply = false;
        this.replyCount = 0;
        this.attachments = new ArrayList<>();
    }
    
    public Message(String subject, String content, String fromUserId, String toUserId) {
        this();
        this.subject = subject;
        this.content = content;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
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
    
    public String getFromUserName() {
        return fromUserName;
    }
    
    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }
    
    public String getToUserId() {
        return toUserId;
    }
    
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }
    
    public String getToUserName() {
        return toUserName;
    }
    
    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public Boolean getIsReply() {
        return isReply;
    }
    
    public void setIsReply(Boolean isReply) {
        this.isReply = isReply;
    }
    
    public String getOriginalMessageId() {
        return originalMessageId;
    }
    
    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public List<Attachment> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    
    public Integer getReplyCount() {
        return replyCount;
    }
    
    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", subject='" + subject + '\'' +
                ", fromUserName='" + fromUserName + '\'' +
                ", toUserName='" + toUserName + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}



