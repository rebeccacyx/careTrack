package com.careapp.service;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;


/**
 * Service interface for Budget operations
 * Defines all business logic methods for budget management
 */
public interface BudgetService {
    
    // Basic CRUD operations
    
    /**
     * Create a new budget for a patient
     * @param budget Budget object to create
     * @return Created budget
     */
    Budget createBudget(Budget budget);
    
    /**
     * Get budget by patient ID
     * @param patientId Patient ID
     * @return Budget for the patient
     */
    Budget getBudgetByPatientId(String patientId);
    
    /**
     * Update existing budget
     * @param budget Budget object to update
     * @return Updated budget
     */
    Budget updateBudget(Budget budget);
    
    /**
     * Delete budget by patient ID
     * @param patientId Patient ID
     * @return true if deleted successfully
     */
    boolean deleteBudget(String patientId);
    
    /**
     * Delete budget by budget ID
     * @param budgetId Budget document ID
     * @return true if deleted successfully
     */
    boolean deleteBudgetById(String budgetId);
    
    // Budget adjustment operations
    
    /**
     * Adjust total budget amount
     * @param patientId Patient ID
     * @param newTotalBudget New total budget amount
     * @param reason Reason for adjustment
     * @return Updated budget
     */
    Budget adjustTotalBudget(String patientId, double newTotalBudget, String reason);
    
    /**
     * Add new category to budget
     * @param patientId Patient ID
     * @param category New category to add
     * @return Updated budget
     */
    Budget addCategory(String patientId, BudgetCategory category);
    
    /**
     * Add new sub-element to a category
     * @param patientId Patient ID
     * @param categoryId Category ID
     * @param subElement New sub-element to add
     * @return Updated budget
     */
    Budget addSubElement(String patientId, String categoryId, BudgetSubElement subElement);
    
    /**
     * Update monthly usage for a sub-element
     * @param patientId Patient ID
     * @param categoryId Category ID
     * @param subElementId Sub-element ID
     * @param month Month index (0-11)
     * @param amount Amount used in the month
     * @return Updated budget
     */
    Budget updateMonthlyUsage(String patientId, String categoryId, String subElementId, 
                             int month, double amount);
    
    // Advanced adjustments
    Budget reallocateBetweenCategories(String patientId, String fromCategoryId, String toCategoryId, double amount, String reason);
    
    Budget reallocateBetweenSubElements(String patientId, String categoryId, String fromSubElementId, String toSubElementId, double amount, String reason);
    
    Budget refundSubElement(String patientId, String categoryId, String subElementId, double amount, String reason);
    
    Budget updateMonthlyUsageBulk(String patientId, String categoryId, String subElementId, java.util.List<Double> monthlyAmounts);
    
}
