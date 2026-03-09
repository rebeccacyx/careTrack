package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "task_requests")
public class TaskRequest {
    
    @Id
    private String id;
    
    @Field("requester_id")
    private String requesterId; // POA user ID
    
    @Field("requester_name")
    private String requester; // POA name (e.g., "POA")
    
    @Field("task_title")
    private String taskTitle; // Title of the requested task
    
    @Field("request_type")
    private String requestType; // new, recurring, modify, remove, reschedule
    
    @Field("description")
    private String description; // Task description or modification details
    
    @Field("priority")
    private String priority; // normal, urgent, very-urgent
    
    @Field("reason")
    private String reason; // Reason for the request
    
    @Field("status")
    private String status; // Pending, Approved, Rejected
    
    @Field("organization_id")
    private String organizationId; // Associated organization
    
    @Field("patient_id")
    private String patientId; // Associated patient
    
    // Recurring task fields
    @Field("frequency")
    private String frequency; // daily, weekly, monthly, yearly
    
    @Field("frequency_number")
    private Integer frequencyNumber; // e.g., every 2 weeks
    
    @Field("time_of_day")
    private String timeOfDay; // HH:mm format
    
    @Field("day_of_week")
    private String dayOfWeek; // Monday, Tuesday, etc.
    
    @Field("day_of_month")
    private Integer dayOfMonth; // 1-31
    
    @Field("month")
    private String month; // January, February, etc.
    
    @Field("start_date")
    private LocalDate startDate;
    
    @Field("end_date")
    private LocalDate endDate;
    
    // Approval/Rejection fields
    @Field("approved_by")
    private String approvedBy; // Manager user ID who approved/rejected
    
    @Field("approval_reason")
    private String approvalReason; // Reason for approval
    
    @Field("rejection_reason")
    private String rejectionReason; // Reason for rejection
    
    @Field("submitted_date")
    private LocalDate submittedDate; // Date when request was submitted
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("processed_at")
    private LocalDateTime processedAt; // When approved/rejected
    
    // Constructors
    public TaskRequest() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "Pending";
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }
    
    public String getRequester() {
        return requester;
    }
    
    public void setRequester(String requester) {
        this.requester = requester;
    }
    
    public String getTaskTitle() {
        return taskTitle;
    }
    
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    
    public String getRequestType() {
        return requestType;
    }
    
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getFrequency() {
        return frequency;
    }
    
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
    
    public Integer getFrequencyNumber() {
        return frequencyNumber;
    }
    
    public void setFrequencyNumber(Integer frequencyNumber) {
        this.frequencyNumber = frequencyNumber;
    }
    
    public String getTimeOfDay() {
        return timeOfDay;
    }
    
    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public Integer getDayOfMonth() {
        return dayOfMonth;
    }
    
    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
    
    public String getMonth() {
        return month;
    }
    
    public void setMonth(String month) {
        this.month = month;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
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
    
    public LocalDate getSubmittedDate() {
        return submittedDate;
    }
    
    public void setSubmittedDate(LocalDate submittedDate) {
        this.submittedDate = submittedDate;
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
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    // Helper methods
    public void approve(String managerId, String reason) {
        this.status = "Approved";
        this.approvedBy = managerId;
        this.approvalReason = reason;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void reject(String managerId, String reason) {
        this.status = "Rejected";
        this.approvedBy = managerId;
        this.rejectionReason = reason;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

