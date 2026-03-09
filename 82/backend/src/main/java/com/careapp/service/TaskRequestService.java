package com.careapp.service;

import com.careapp.domain.TaskRequest;
import com.careapp.repository.TaskRequestRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskRequestService {
    
    @Resource
    private TaskRequestRepository taskRequestRepository;
    
    // Create a new task request
    public TaskRequest createTaskRequest(TaskRequest request) {
        if (request.getCreatedAt() == null) {
            request.setCreatedAt(LocalDateTime.now());
        }
        if (request.getUpdatedAt() == null) {
            request.setUpdatedAt(LocalDateTime.now());
        }
        if (request.getStatus() == null || request.getStatus().isEmpty()) {
            request.setStatus("Pending");
        }
        if (request.getSubmittedDate() == null) {
            request.setSubmittedDate(LocalDate.now());
        }
        return taskRequestRepository.save(request);
    }
    
    // Get task request by ID
    public Optional<TaskRequest> getTaskRequestById(String id) {
        return taskRequestRepository.findById(id);
    }
    
    // Get all pending requests for an organization (for Manager)
    public List<TaskRequest> getPendingRequestsByOrganization(String organizationId) {
        return taskRequestRepository.findByOrganizationIdAndStatusOrderByCreatedAtDesc(organizationId, "Pending");
    }
    
    // Get all requests by requester (for POA)
    public List<TaskRequest> getRequestsByRequester(String requesterId) {
        return taskRequestRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId);
    }
    
    // Get all requests by organization
    public List<TaskRequest> getAllRequestsByOrganization(String organizationId) {
        return taskRequestRepository.findByOrganizationId(organizationId);
    }
    
    // Get all requests by patient
    public List<TaskRequest> getRequestsByPatient(String patientId) {
        return taskRequestRepository.findByPatientId(patientId);
    }
    
    // Approve a task request
    public TaskRequest approveRequest(String requestId, String managerId, String approvalReason) {
        Optional<TaskRequest> optionalRequest = taskRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            TaskRequest request = optionalRequest.get();
            request.approve(managerId, approvalReason);
            return taskRequestRepository.save(request);
        }
        return null;
    }
    
    // Reject a task request
    public TaskRequest rejectRequest(String requestId, String managerId, String rejectionReason) {
        Optional<TaskRequest> optionalRequest = taskRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            TaskRequest request = optionalRequest.get();
            request.reject(managerId, rejectionReason);
            return taskRequestRepository.save(request);
        }
        return null;
    }
    
    // Update task request
    public TaskRequest updateTaskRequest(TaskRequest request) {
        request.setUpdatedAt(LocalDateTime.now());
        return taskRequestRepository.save(request);
    }
    
    // Delete task request
    public void deleteTaskRequest(String id) {
        taskRequestRepository.deleteById(id);
    }
}

