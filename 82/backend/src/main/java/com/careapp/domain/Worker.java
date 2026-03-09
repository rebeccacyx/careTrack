package com.careapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "workers")
public class Worker {
    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private String password; // worker password for login
    
    @Field("worker_id")
    private String workerId; 
    
    private String status; // "active", "inactive", "pending", "blocked"
    
    @Field("organization_id")
    private String organizationId;
    
    @Field("manager_id")
    private String managerId; // Manager who manages this worker
    
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @Field("activated_at")
    private LocalDateTime activatedAt;
    
    @Field("last_login_at")
    private LocalDateTime lastLoginAt;
    
    private String notes; // notes
    
    // Note: Removed assignedPatients field
    // because Workers should access Patient data through Task assignments
    
    @Field("daily_schedule")
    private String dailySchedule; // daily schedule
    
    @Field("specializations")
    private List<String> specializations; // specializations
    
    @Field("photo_url")
    private String photoUrl; // worker photo URL
    
    @Field("shift_allocations")
    private List<ShiftAllocation> shiftAllocations; // shift allocations by management

    // Inner class for shift allocation
    public static class ShiftAllocation {
        @Field("shift_date")
        private String shiftDate; // YYYY-MM-DD format
        
        @Field("shift_time")
        private String shiftTime; // e.g., "09:00-17:00"
        
        @Field("patient_id")
        private String patientId; // patient being cared for
        
        @Field("allocated_by")
        private String allocatedBy; // manager who allocated the shift
        
        @Field("allocated_at")
        private LocalDateTime allocatedAt;
        
        @Field("status")
        private String status; // "scheduled", "in_progress", "completed", "cancelled"
        
        @Field("notes")
        private String notes; // additional notes for the shift

        // Constructors
        public ShiftAllocation() {
            this.allocatedAt = LocalDateTime.now();
            this.status = "scheduled";
        }

        public ShiftAllocation(String shiftDate, String shiftTime, String patientId, String allocatedBy) {
            this();
            this.shiftDate = shiftDate;
            this.shiftTime = shiftTime;
            this.patientId = patientId;
            this.allocatedBy = allocatedBy;
        }

        // Getters and Setters
        public String getShiftDate() { return shiftDate; }
        public void setShiftDate(String shiftDate) { this.shiftDate = shiftDate; }

        public String getShiftTime() { return shiftTime; }
        public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }

        public String getAllocatedBy() { return allocatedBy; }
        public void setAllocatedBy(String allocatedBy) { this.allocatedBy = allocatedBy; }

        public LocalDateTime getAllocatedAt() { return allocatedAt; }
        public void setAllocatedAt(LocalDateTime allocatedAt) { this.allocatedAt = allocatedAt; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    // Constructors
    public Worker() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "pending";
    }

    public Worker(String name, String email, String workerId, String organizationId) {
        this();
        this.name = name;
        this.email = email;
        this.workerId = workerId;
        this.organizationId = organizationId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getActivatedAt() { return activatedAt; }
    public void setActivatedAt(LocalDateTime activatedAt) { this.activatedAt = activatedAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Note: Removed getter and setter methods for assignedPatients

    public String getDailySchedule() { return dailySchedule; }
    public void setDailySchedule(String dailySchedule) { this.dailySchedule = dailySchedule; }

    public List<String> getSpecializations() { return specializations; }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public List<ShiftAllocation> getShiftAllocations() { return shiftAllocations; }
    public void setShiftAllocations(List<ShiftAllocation> shiftAllocations) { this.shiftAllocations = shiftAllocations; }
}
