package com.careapp.service;

import com.careapp.domain.Budget;

import java.util.List;

/**
 * Service for budget calculation and validation logic
 * This service handles all complex budget calculations and business rules
 */
public interface BudgetCalculationService {
    
    /**
     * Calculate all budget metrics for a budget
     * This includes totals, balances, and warning levels
     * @param budget Budget to calculate
     * @return Updated budget with calculated values
     */
    Budget calculateBudgetMetrics(Budget budget);
    
    /**
     * Calculate total used amount across all categories and sub-elements
     * @param budget Budget to calculate
     * @return Total amount used
     */
    double calculateTotalUsed(Budget budget);
    
    /**
     * Calculate total remaining balance
     * @param budget Budget to calculate
     * @return Total remaining balance
     */
    double calculateTotalBalance(Budget budget);
    
    /**
     * Calculate overall usage percentage
     * @param budget Budget to calculate
     * @return Usage percentage (0-100)
     */
    double calculateUsagePercentage(Budget budget);
    
    /**
     * Validate budget data for consistency
     * @param budget Budget to validate
     * @return List of validation errors (empty if valid)
     */
    List<String> validateBudget(Budget budget);
    
    /**
     * Check if budget is over budget
     * @param budget Budget to check
     * @return true if over budget
     */
    boolean isOverBudget(Budget budget);
    
    /**
     * Get all budget warnings for a budget
     * @param budget Budget to analyze
     * @return List of warning messages
     */
    List<String> getBudgetWarnings(Budget budget);
}
