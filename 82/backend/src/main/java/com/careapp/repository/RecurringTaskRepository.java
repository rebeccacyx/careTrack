package com.careapp.repository;

import com.careapp.domain.RecurringTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecurringTaskRepository extends MongoRepository<RecurringTask, String> {
    
    // Find all active recurring tasks
    List<RecurringTask> findByIsActiveTrue();
    
    // Find recurring tasks by patient ID
    List<RecurringTask> findByPatientId(String patientId);
    
    // Find recurring tasks by organization ID
    List<RecurringTask> findByOrganizationId(String organizationId);
    
    // Find recurring tasks by assigned worker
    List<RecurringTask> findByAssignedToId(String assignedToId);
    
    // Find recurring tasks by created by user
    List<RecurringTask> findByCreatedBy(String createdBy);
}
