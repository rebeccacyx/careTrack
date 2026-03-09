package com.careapp.repository;

import com.careapp.domain.TaskRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRequestRepository extends MongoRepository<TaskRequest, String> {
    
    // Find all pending requests for a specific organization
    List<TaskRequest> findByOrganizationIdAndStatus(String organizationId, String status);
    
    // Find all requests by requester (POA)
    List<TaskRequest> findByRequesterId(String requesterId);
    
    // Find all requests by patient
    List<TaskRequest> findByPatientId(String patientId);
    
    // Find all requests by organization
    List<TaskRequest> findByOrganizationId(String organizationId);
    
    // Find pending requests by organization
    List<TaskRequest> findByOrganizationIdAndStatusOrderByCreatedAtDesc(String organizationId, String status);
    
    // Find requests by requester and status
    List<TaskRequest> findByRequesterIdAndStatusOrderByCreatedAtDesc(String requesterId, String status);
    
    // Find all requests by requester (all statuses)
    List<TaskRequest> findByRequesterIdOrderByCreatedAtDesc(String requesterId);
}

