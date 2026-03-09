package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "schedules")
public class Schedule {
    @Id
    private String id;

    @Field("worker_id")
    private String workerId; // Worker's ID

    @Field("worker_name")
    private String workerName; // Worker's name for display

    @Field("schedule_date")
    private LocalDate scheduleDate; // Date of the shift

    @Field("shift_type")
    private String shiftType; // "morning", "evening", "full-day"

    @Field("shift_start_time")
    private String shiftStartTime; // e.g., "08:00"

    @Field("shift_end_time")
    private String shiftEndTime; // e.g., "16:00"

    @Field("organization_id")
    private String organizationId;

    @Field("manager_id")
    private String managerId; // Manager who created this schedule

    @Field("status")
    private String status; // "scheduled", "confirmed", "completed", "cancelled"

    @Field("worker_photo_url")
    private String workerPhotoUrl; // URL of uploaded worker photo

    @Field("worker_photo_uploaded_at")
    private LocalDateTime workerPhotoUploadedAt; // When photo was uploaded

    @Field("notes")
    private String notes; // Schedule notes from manager

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Schedule() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "scheduled"; // Default status
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }

    public String getShiftStartTime() { return shiftStartTime; }
    public void setShiftStartTime(String shiftStartTime) { this.shiftStartTime = shiftStartTime; }

    public String getShiftEndTime() { return shiftEndTime; }
    public void setShiftEndTime(String shiftEndTime) { this.shiftEndTime = shiftEndTime; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getWorkerPhotoUrl() { return workerPhotoUrl; }
    public void setWorkerPhotoUrl(String workerPhotoUrl) { this.workerPhotoUrl = workerPhotoUrl; }

    public LocalDateTime getWorkerPhotoUploadedAt() { return workerPhotoUploadedAt; }
    public void setWorkerPhotoUploadedAt(LocalDateTime workerPhotoUploadedAt) { this.workerPhotoUploadedAt = workerPhotoUploadedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
