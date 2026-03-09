package com.careapp.dto;

import java.util.List;

public class DailyScheduleRequest {
    private String scheduleDate; // YYYY-MM-DD format
    private List<String> selectedWorkerIds; // IDs of selected workers
    private List<String> morningShiftWorkerIds; // Workers for morning shift (8:00-16:00)
    private List<String> afternoonShiftWorkerIds; // Workers for afternoon shift (12:00-20:00)
    private List<String> eveningShiftWorkerIds; // Workers for evening shift (16:00-24:00)
    private String scheduleNotes; // Additional notes for the schedule

    // Constructors
    public DailyScheduleRequest() {}

    public DailyScheduleRequest(String scheduleDate, List<String> selectedWorkerIds, 
                               List<String> morningShiftWorkerIds, List<String> afternoonShiftWorkerIds, 
                               List<String> eveningShiftWorkerIds, String scheduleNotes) {
        this.scheduleDate = scheduleDate;
        this.selectedWorkerIds = selectedWorkerIds;
        this.morningShiftWorkerIds = morningShiftWorkerIds;
        this.afternoonShiftWorkerIds = afternoonShiftWorkerIds;
        this.eveningShiftWorkerIds = eveningShiftWorkerIds;
        this.scheduleNotes = scheduleNotes;
    }

    // Getters and Setters
    public String getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }

    public List<String> getSelectedWorkerIds() { return selectedWorkerIds; }
    public void setSelectedWorkerIds(List<String> selectedWorkerIds) { this.selectedWorkerIds = selectedWorkerIds; }

    public List<String> getMorningShiftWorkerIds() { return morningShiftWorkerIds; }
    public void setMorningShiftWorkerIds(List<String> morningShiftWorkerIds) { this.morningShiftWorkerIds = morningShiftWorkerIds; }

    public List<String> getAfternoonShiftWorkerIds() { return afternoonShiftWorkerIds; }
    public void setAfternoonShiftWorkerIds(List<String> afternoonShiftWorkerIds) { this.afternoonShiftWorkerIds = afternoonShiftWorkerIds; }

    public List<String> getEveningShiftWorkerIds() { return eveningShiftWorkerIds; }
    public void setEveningShiftWorkerIds(List<String> eveningShiftWorkerIds) { this.eveningShiftWorkerIds = eveningShiftWorkerIds; }

    public String getScheduleNotes() { return scheduleNotes; }
    public void setScheduleNotes(String scheduleNotes) { this.scheduleNotes = scheduleNotes; }
}
