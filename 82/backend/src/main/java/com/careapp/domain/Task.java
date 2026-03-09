package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "tasks")
public class Task {
    
    @Id
    private String id;
    
    @Field("title")
    private String title;
    
    @Field("description")
    private String description;
    
    @Field("assigned_to")
    private String assignedTo; // Worker ID or name
    
    @Field("assigned_to_id")
    private String assignedToId; // Worker user ID
    
    @Field("priority")
    private String priority; // normal, urgent, very-urgent
    
    @Field("status")
    private String status; // In Progress, Worker Completed, Completed, Rejected
    
    @Field("due_date")
    private LocalDate dueDate;
    
    @Field("created_by")
    private String createdBy; // Manager ID who created the task
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("completed_at")
    private LocalDateTime completedAt;
    
    @Field("approval_reason")
    private String approvalReason;
    
    @Field("rejection_reason")
    private String rejectionReason;
    
    @Field("patient_id")
    private String patientId; // Associated patient (REQUIRED)
    
    @Field("organization_id")
    private String organizationId; // Associated organization
    
    @Field("is_recurring")
    private Boolean isRecurring; // Whether this task was generated from a recurring template
    
    @Field("recurring_template_id")
    private String recurringTemplateId; // ID of the recurring task template that generated this task
    
    // Constructors
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Task(String title, String description, String assignedTo, String priority, LocalDate dueDate) {
        this();
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = "In Progress";
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public String getAssignedToId() {
        return assignedToId;
    }
    
    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getApprovalReason() {
        return approvalReason;
    }
    
    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public Boolean getIsRecurring() {
        return isRecurring;
    }
    
    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }
    
    public String getRecurringTemplateId() {
        return recurringTemplateId;
    }
    
    public void setRecurringTemplateId(String recurringTemplateId) {
        this.recurringTemplateId = recurringTemplateId;
    }
    
    // Helper methods
    public void markAsCompleted() {
        this.status = "Completed";
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsWorkerCompleted() {
        this.status = "Worker Completed";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void approveCompletion(String reason) {
        this.status = "Completed";
        this.approvalReason = reason;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void rejectCompletion(String reason) {
        this.status = "Rejected";
        this.rejectionReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
