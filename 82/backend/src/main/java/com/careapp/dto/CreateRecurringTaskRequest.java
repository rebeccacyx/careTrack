package com.careapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class CreateRecurringTaskRequest {
    
    private String title;
    private String description;
    private String assignedTo;        // Worker name (A, B, C)
    private String assignedToId;      // Worker ID (W001, W002, W003)
    private String frequency;         // daily, weekly, monthly
    private Integer frequencyNumber;  // Every N days/weeks/months
    private String timeOfDay;         // HH:mm format
    private String dayOfWeek;         // For weekly frequency (monday, tuesday, etc.)
    private Integer dayOfMonth;       // For monthly frequency (1-31)
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;        // Optional end date
    
    private String patientId;         // Associated patient
    private String organizationId;    // Associated organization
    private String createdBy;         // User who created this recurring task
    
    // Constructors
    public CreateRecurringTaskRequest() {}
    
    public CreateRecurringTaskRequest(String title, String description, String assignedTo, String frequency) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.frequency = frequency;
        this.frequencyNumber = 1;
    }
    
    // Getters and Setters
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    @Override
    public String toString() {
        return "CreateRecurringTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", frequency='" + frequency + '\'' +
                ", frequencyNumber=" + frequencyNumber +
                ", timeOfDay='" + timeOfDay + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
