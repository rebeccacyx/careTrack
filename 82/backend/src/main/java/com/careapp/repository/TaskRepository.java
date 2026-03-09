package com.careapp.repository;

import com.careapp.domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    
    // Find tasks by assigned worker
    List<Task> findByAssignedToId(String assignedToId);
    
    // Find tasks by assigned worker name
    List<Task> findByAssignedTo(String assignedTo);
    
    // Find tasks by status
    List<Task> findByStatus(String status);
    
    // Find tasks by priority
    List<Task> findByPriority(String priority);
    
    // Find tasks by due date
    List<Task> findByDueDate(LocalDate dueDate);
    
    
    // Find tasks created by a specific manager
    List<Task> findByCreatedBy(String createdBy);
    
    // Find tasks for a specific patient
    List<Task> findByPatientId(String patientId);
    
    // Find tasks for a specific organization
    List<Task> findByOrganizationId(String organizationId);
    
    // Find tasks by assigned worker and status
    List<Task> findByAssignedToIdAndStatus(String assignedToId, String status);
    
    // Find tasks by assigned worker and due date
    List<Task> findByAssignedToIdAndDueDate(String assignedToId, LocalDate dueDate);
    
    // Find tasks by status and due date
    List<Task> findByStatusAndDueDate(String status, LocalDate dueDate);
    
    // Find tasks by multiple statuses
    @Query("{'status': {$in: ?0}}")
    List<Task> findByStatusIn(List<String> statuses);
    
    // Find tasks by assigned worker and multiple statuses
    @Query("{'assignedToId': ?0, 'status': {$in: ?1}}")
    List<Task> findByAssignedToIdAndStatusIn(String assignedToId, List<String> statuses);
    
    // Find tasks by due date and multiple statuses
    @Query("{'dueDate': ?0, 'status': {$in: ?1}}")
    List<Task> findByDueDateAndStatusIn(LocalDate dueDate, List<String> statuses);
    
    // Find tasks by priority and status
    List<Task> findByPriorityAndStatus(String priority, String status);
    
    // Find tasks by created date range
    List<Task> findByCreatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    
    // Find tasks by updated date range
    List<Task> findByUpdatedAtBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    
    // Count tasks by status
    long countByStatus(String status);
    
    // Count tasks by assigned worker
    long countByAssignedToId(String assignedToId);
    
    // Count tasks by assigned worker and status
    long countByAssignedToIdAndStatus(String assignedToId, String status);
    
    // Count tasks by due date
    long countByDueDate(LocalDate dueDate);
    
    // Count tasks by due date and status
    long countByDueDateAndStatus(LocalDate dueDate, String status);
}
