package com.careapp.service.impl;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;
import com.careapp.service.BudgetCalculationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BudgetCalculationService
 * Contains all budget calculation and validation logic
 */
@Service
public class BudgetCalculationServiceImpl implements BudgetCalculationService {

    @Override
    public Budget calculateBudgetMetrics(Budget budget) {
        if (budget == null || budget.getCategories() == null) {
            return budget;
        }
        
        // Calculate metrics for each category and sub-element
        for (BudgetCategory category : budget.getCategories()) {
            calculateCategoryMetrics(category);
        }
        
        return budget;
    }

    @Override
    public double calculateTotalUsed(Budget budget) {
        if (budget == null || budget.getCategories() == null) {
            return 0.0;
        }
        
        double totalUsed = 0.0;
        for (BudgetCategory category : budget.getCategories()) {
            totalUsed += calculateCategoryUsed(category);
        }
        
        return totalUsed;
    }

    @Override
    public double calculateTotalBalance(Budget budget) {
        if (budget == null) {
            return 0.0;
        }
        
        return budget.getTotalBudget() - calculateTotalUsed(budget);
    }

    @Override
    public double calculateUsagePercentage(Budget budget) {
        if (budget == null || budget.getTotalBudget() <= 0) {
            return 0.0;
        }
        
        double totalUsed = calculateTotalUsed(budget);
        return (totalUsed / budget.getTotalBudget()) * 100;
    }

    @Override
    public List<String> validateBudget(Budget budget) {
        List<String> errors = new ArrayList<>();
        
        if (budget == null) {
            errors.add("Budget cannot be null");
            return errors;
        }
        
        // Validate basic fields
        if (budget.getPatientId() == null || budget.getPatientId().trim().isEmpty()) {
            errors.add("Patient ID is required");
        }
        
        if (budget.getOrganizationId() == null || budget.getOrganizationId().trim().isEmpty()) {
            errors.add("Organization ID is required");
        }
        
        if (budget.getTotalBudget() < 0) {
            errors.add("Total budget cannot be negative");
        }
        
        // Validate categories
        if (budget.getCategories() != null) {
            for (int i = 0; i < budget.getCategories().size(); i++) {
                BudgetCategory category = budget.getCategories().get(i);
                List<String> categoryErrors = validateCategory(category, i);
                errors.addAll(categoryErrors);
            }
        }
        
        // Validate budget consistency
        if (budget.getCategories() != null && !budget.getCategories().isEmpty()) {
            double categoryTotal = budget.getCategories().stream()
                .mapToDouble(BudgetCategory::getCategoryBudget)
                .sum();
            
            // Allow some tolerance for floating point precision
            if (Math.abs(categoryTotal - budget.getTotalBudget()) > 0.01) {
                errors.add(String.format("Category budgets (%.2f) do not match total budget (%.2f)", 
                    categoryTotal, budget.getTotalBudget()));
            }
        }
        
        return errors;
    }

    @Override
    public boolean isOverBudget(Budget budget) {
        if (budget == null) {
            return false;
        }
        
        double totalUsed = calculateTotalUsed(budget);
        return totalUsed > budget.getTotalBudget();
    }

    @Override
    public List<String> getBudgetWarnings(Budget budget) {
        List<String> warnings = new ArrayList<>();
        
        if (budget == null || budget.getCategories() == null) {
            return warnings;
        }
        
        // Check overall budget usage
        double usagePercentage = calculateUsagePercentage(budget);
        if (usagePercentage >= 100) {
            warnings.add("CRITICAL: Total budget exceeded by " + String.format("%.2f%%", usagePercentage - 100));
        } else if (usagePercentage >= 90) {
            warnings.add("WARNING: Total budget usage at " + String.format("%.2f%%", usagePercentage));
        }
        
        // Check individual sub-elements
        for (BudgetCategory category : budget.getCategories()) {
            if (category.getSubElements() != null) {
                for (BudgetSubElement subElement : category.getSubElements()) {
                    String warning = getSubElementWarning(category.getName(), subElement);
                    if (warning != null) {
                        warnings.add(warning);
                    }
                }
            }
        }
        
        return warnings;
    }

    /**
     * Calculate metrics for a category
     * @param category Category to calculate
     */
    private void calculateCategoryMetrics(BudgetCategory category) {
        if (category == null || category.getSubElements() == null) {
            return;
        }
        
        // Calculate metrics for each sub-element
        for (BudgetSubElement subElement : category.getSubElements()) {
            calculateSubElementMetrics(subElement);
        }
    }

    /**
     * Calculate metrics for a sub-element
     * @param subElement Sub-element to calculate
     */
    private void calculateSubElementMetrics(BudgetSubElement subElement) {
        if (subElement == null) {
            return;
        }
        
        // Calculate total used
        subElement.calculateTotalUtilised();
        
        // Calculate balance
        subElement.calculateBalance();
        
        // Calculate warning level
        subElement.calculateWarningLevel();
    }

    /**
     * Calculate total used amount for a category
     * @param category Category to calculate
     * @return Total used amount
     */
    private double calculateCategoryUsed(BudgetCategory category) {
        if (category == null || category.getSubElements() == null) {
            return 0.0;
        }
        
        return category.getSubElements().stream()
            .mapToDouble(BudgetSubElement::getTotalUtilised)
            .sum();
    }

    /**
     * Validate a category
     * @param category Category to validate
     * @param index Category index for error messages
     * @return List of validation errors
     */
    private List<String> validateCategory(BudgetCategory category, int index) {
        List<String> errors = new ArrayList<>();
        
        if (category == null) {
            errors.add("Category at index " + index + " is null");
            return errors;
        }
        
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            errors.add("Category name at index " + index + " is required");
        }
        
        if (category.getCategoryBudget() < 0) {
            errors.add("Category budget at index " + index + " cannot be negative");
        }
        
        // Validate sub-elements
        if (category.getSubElements() != null) {
            for (int i = 0; i < category.getSubElements().size(); i++) {
                BudgetSubElement subElement = category.getSubElements().get(i);
                List<String> subElementErrors = validateSubElement(subElement, index, i);
                errors.addAll(subElementErrors);
            }
        }
        
        return errors;
    }

    /**
     * Validate a sub-element
     * @param subElement Sub-element to validate
     * @param categoryIndex Category index
     * @param subElementIndex Sub-element index
     * @return List of validation errors
     */
    private List<String> validateSubElement(BudgetSubElement subElement, int categoryIndex, int subElementIndex) {
        List<String> errors = new ArrayList<>();
        
        if (subElement == null) {
            errors.add(String.format("Sub-element at category %d, index %d is null", categoryIndex, subElementIndex));
            return errors;
        }
        
        if (subElement.getName() == null || subElement.getName().trim().isEmpty()) {
            errors.add(String.format("Sub-element name at category %d, index %d is required", categoryIndex, subElementIndex));
        }
        
        if (subElement.getSubElementBudget() < 0) {
            errors.add(String.format("Sub-element budget at category %d, index %d cannot be negative", categoryIndex, subElementIndex));
        }
        
        // Validate monthly usage
        if (subElement.getMonthlyUsage() != null) {
            if (subElement.getMonthlyUsage().size() != 12) {
                errors.add(String.format("Sub-element monthly usage at category %d, index %d must have exactly 12 months", categoryIndex, subElementIndex));
            }
            
            for (int i = 0; i < subElement.getMonthlyUsage().size(); i++) {
                Double usage = subElement.getMonthlyUsage().get(i);
                if (usage == null || usage < 0) {
                    errors.add(String.format("Sub-element monthly usage at category %d, index %d, month %d cannot be negative", categoryIndex, subElementIndex, i));
                }
            }
        }
        
        return errors;
    }

    /**
     * Get warning message for a sub-element
     * @param categoryName Category name
     * @param subElement Sub-element to check
     * @return Warning message or null if no warning
     */
    private String getSubElementWarning(String categoryName, BudgetSubElement subElement) {
        if (subElement == null || subElement.getSubElementBudget() <= 0) {
            return null;
        }
        
        double percentage = (subElement.getTotalUtilised() / subElement.getSubElementBudget()) * 100;
        
        if (percentage >= 100) {
            return String.format("CRITICAL: %s - %s exceeded budget by %.2f%%", 
                categoryName, subElement.getName(), percentage - 100);
        } else if (percentage >= 80) {
            return String.format("WARNING: %s - %s usage at %.2f%%", 
                categoryName, subElement.getName(), percentage);
        }
        
        return null;
    }
}
