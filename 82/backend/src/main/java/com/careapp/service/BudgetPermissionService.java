package com.careapp.service;

import java.util.List;

/**
 * Service for managing budget permissions and access control
 * This service handles authorization checks for budget operations
 */
public interface BudgetPermissionService {
    
    /**
     * Check if a user can view a patient's budget
     * @param userId User ID requesting access
     * @param patientId Patient ID whose budget is being accessed
     * @return true if user has permission to view budget
     */
    boolean canViewBudget(String userId, String patientId);
    
    /**
     * Check if a user can create a budget for a patient
     * @param userId User ID requesting access
     * @param patientId Patient ID for whom budget is being created
     * @return true if user has permission to create budget
     */
    boolean canCreateBudget(String userId, String patientId);
    
    /**
     * Check if a user can update a patient's budget
     * @param userId User ID requesting access
     * @param patientId Patient ID whose budget is being updated
     * @return true if user has permission to update budget
     */
    boolean canUpdateBudget(String userId, String patientId);
    
    /**
     * Check if a user can delete a patient's budget
     * @param userId User ID requesting access
     * @param patientId Patient ID whose budget is being deleted
     * @return true if user has permission to delete budget
     */
    boolean canDeleteBudget(String userId, String patientId);
    
    /**
     * Check if a user can manage budget categories and sub-elements
     * @param userId User ID requesting access
     * @param patientId Patient ID whose budget is being managed
     * @return true if user has permission to manage budget items
     */
    boolean canManageBudgetItems(String userId, String patientId);
    
    /**
     * Check if a user can update monthly usage
     * @param userId User ID requesting access
     * @param patientId Patient ID whose budget usage is being updated
     * @return true if user has permission to update usage
     */
    boolean canUpdateUsage(String userId, String patientId);
    
    /**
     * Get all patients that a user can access budgets for
     * @param userId User ID
     * @return List of patient IDs the user can access
     */
    List<String> getAccessiblePatients(String userId);
    
    /**
     * Check if user is a budget administrator (POA, Manager, Admin)
     * @param userId User ID
     * @return true if user has administrative privileges
     */
    boolean isBudgetAdministrator(String userId);
}
