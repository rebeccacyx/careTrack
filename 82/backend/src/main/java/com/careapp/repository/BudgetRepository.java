package com.careapp.repository;

import com.careapp.domain.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Budget operations
 * This interface provides basic CRUD operations for Budget entities
 */
@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    
    /**
     * Find budget by patient ID
     * @param patientId Patient ID
     * @return Budget for the patient
     */
    Optional<Budget> findByPatientId(String patientId);
    
    
    /**
     * Check if budget exists for a patient
     * @param patientId Patient ID
     * @return true if budget exists, false otherwise
     */
    boolean existsByPatientId(String patientId);
}
