package com.careapp.dto;

public class WorkerPhotoUploadRequest {
    private String workerId;
    private String photoUrl; // URL of the uploaded photo
    private String photoFileName; // Original filename
    private String photoContentType; // MIME type
    private long photoSize; // File size in bytes

    // Constructors
    public WorkerPhotoUploadRequest() {}

    public WorkerPhotoUploadRequest(String workerId, String photoUrl, String photoFileName, 
                                   String photoContentType, long photoSize) {
        this.workerId = workerId;
        this.photoUrl = photoUrl;
        this.photoFileName = photoFileName;
        this.photoContentType = photoContentType;
        this.photoSize = photoSize;
    }

    // Getters and Setters
    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getPhotoFileName() { return photoFileName; }
    public void setPhotoFileName(String photoFileName) { this.photoFileName = photoFileName; }

    public String getPhotoContentType() { return photoContentType; }
    public void setPhotoContentType(String photoContentType) { this.photoContentType = photoContentType; }

    public long getPhotoSize() { return photoSize; }
    public void setPhotoSize(long photoSize) { this.photoSize = photoSize; }
}
