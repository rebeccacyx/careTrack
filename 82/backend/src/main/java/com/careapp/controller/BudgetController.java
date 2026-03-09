package com.careapp.controller;

import com.careapp.domain.Budget;
import com.careapp.domain.BudgetCategory;
import com.careapp.domain.BudgetSubElement;
import com.careapp.service.BudgetCalculationService;
import com.careapp.service.BudgetService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Budget operations
 * Provides API endpoints for budget management
 */
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    @Resource
    private BudgetService budgetService;
    
    @Resource
    private BudgetCalculationService budgetCalculationService;


    // Basic CRUD operations

    /**
     * Create a new budget
     * POST /api/budget
     */
    @PostMapping
    public Result<Budget> createBudget(@RequestBody Budget budget) {
        try {
            Budget createdBudget = budgetService.createBudget(budget);
            return Result.success(createdBudget, "Budget created successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to create budget: " + e.getMessage());
        }
    }

    /**
     * Get budget by patient ID
     * GET /api/budget/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public Result<Budget> getBudgetByPatient(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            return Result.success(budget, "Budget retrieved successfully!");
        } catch (Exception e) {
            return Result.error("404", "Budget not found: " + e.getMessage());
        }
    }

    /**
     * Update existing budget
     * PUT /api/budget
     */
    @PutMapping
    public Result<Budget> updateBudget(@RequestBody Budget budget) {
        try {
            Budget updatedBudget = budgetService.updateBudget(budget);
            return Result.success(updatedBudget, "Budget updated successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to update budget: " + e.getMessage());
        }
    }

    /**
     * Delete budget by patient ID
     * DELETE /api/budget/patient/{patientId}
     */
    @DeleteMapping("/patient/{patientId}")
    public Result<String> deleteBudget(@PathVariable String patientId) {
        try {
            boolean deleted = budgetService.deleteBudget(patientId);
            if (deleted) {
                return Result.success("Budget deleted", "Budget deleted successfully!");
            } else {
                return Result.error("404", "Budget not found");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to delete budget: " + e.getMessage());
        }
    }

    /**
     * Delete budget by budget ID (frontend compatibility)
     * DELETE /api/budget/{budgetId}
     */
    @DeleteMapping("/{budgetId}")
    public Result<String> deleteBudgetById(@PathVariable String budgetId) {
        try {
            boolean deleted = budgetService.deleteBudgetById(budgetId);
            if (deleted) {
                return Result.success("Budget deleted", "Budget deleted successfully!");
            } else {
                return Result.error("404", "Budget not found");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to delete budget: " + e.getMessage());
        }
    }

    // Budget adjustment operations

    /**
     * Adjust total budget amount
     * POST /api/budget/adjust-total
     */
    @PostMapping("/adjust-total")
    public Result<Budget> adjustTotalBudget(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            if (patientId == null || patientId.isEmpty()) {
                return Result.error("400", "patientId is required");
            }
            if (request.get("newTotalBudget") == null) {
                return Result.error("400", "newTotalBudget is required");
            }
            Double newTotalBudget = Double.valueOf(request.get("newTotalBudget").toString());
            if (newTotalBudget < 0) {
                return Result.error("400", "newTotalBudget cannot be negative");
            }
            String reason = (String) request.get("reason");

            Budget updatedBudget = budgetService.adjustTotalBudget(patientId, newTotalBudget, reason);
            return Result.success(updatedBudget, "Total budget adjusted successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to adjust total budget: " + e.getMessage());
        }
    }

    /**
     * Reallocate budget between two categories
     * POST /api/budget/reallocate-category
     */
    @PostMapping("/reallocate-category")
    public Result<Budget> reallocateBetweenCategories(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String fromCategoryId = (String) request.get("fromCategoryId");
            String toCategoryId = (String) request.get("toCategoryId");
            if (patientId == null || patientId.isEmpty()) {
                return Result.error("400", "patientId is required");
            }
            if (fromCategoryId == null || fromCategoryId.isEmpty()) {
                return Result.error("400", "fromCategoryId is required");
            }
            if (toCategoryId == null || toCategoryId.isEmpty()) {
                return Result.error("400", "toCategoryId is required");
            }
            if (request.get("amount") == null) {
                return Result.error("400", "amount is required");
            }
            Double amount = Double.valueOf(request.get("amount").toString());
            if (amount <= 0) {
                return Result.error("400", "amount must be positive");
            }
            String reason = (String) request.get("reason");

            Budget updated = budgetService.reallocateBetweenCategories(patientId, fromCategoryId, toCategoryId, amount, reason);
            return Result.success(updated, "Category reallocation completed!");
        } catch (Exception e) {
            return Result.error("400", "Failed to reallocate category budget: " + e.getMessage());
        }
    }

    /**
     * Reallocate budget between two sub-elements within a category
     * POST /api/budget/reallocate-subelement
     */
    @PostMapping("/reallocate-subelement")
    public Result<Budget> reallocateBetweenSubElements(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String fromSubElementId = (String) request.get("fromSubElementId");
            String toSubElementId = (String) request.get("toSubElementId");
            if (patientId == null || patientId.isEmpty()) {
                return Result.error("400", "patientId is required");
            }
            if (categoryId == null || categoryId.isEmpty()) {
                return Result.error("400", "categoryId is required");
            }
            if (fromSubElementId == null || fromSubElementId.isEmpty()) {
                return Result.error("400", "fromSubElementId is required");
            }
            if (toSubElementId == null || toSubElementId.isEmpty()) {
                return Result.error("400", "toSubElementId is required");
            }
            if (request.get("amount") == null) {
                return Result.error("400", "amount is required");
            }
            Double amount = Double.valueOf(request.get("amount").toString());
            if (amount <= 0) {
                return Result.error("400", "amount must be positive");
            }
            String reason = (String) request.get("reason");

            Budget updated = budgetService.reallocateBetweenSubElements(patientId, categoryId, fromSubElementId, toSubElementId, amount, reason);
            return Result.success(updated, "Sub-element reallocation completed!");
        } catch (Exception e) {
            return Result.error("400", "Failed to reallocate sub-element budget: " + e.getMessage());
        }
    }

    /**
     * Process a refund for a sub-element (decrease utilised, increase balance)
     * POST /api/budget/refund
     */
    @PostMapping("/refund")
    public Result<Budget> refundSubElement(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String subElementId = (String) request.get("subElementId");
            if (patientId == null || patientId.isEmpty()) {
                return Result.error("400", "patientId is required");
            }
            if (categoryId == null || categoryId.isEmpty()) {
                return Result.error("400", "categoryId is required");
            }
            if (subElementId == null || subElementId.isEmpty()) {
                return Result.error("400", "subElementId is required");
            }
            if (request.get("amount") == null) {
                return Result.error("400", "amount is required");
            }
            Double amount = Double.valueOf(request.get("amount").toString());
            if (amount <= 0) {
                return Result.error("400", "amount must be positive");
            }
            String reason = (String) request.get("reason");

            Budget updated = budgetService.refundSubElement(patientId, categoryId, subElementId, amount, reason);
            return Result.success(updated, "Refund processed successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to process refund: " + e.getMessage());
        }
    }

    /**
     * Add new category to budget
     * POST /api/budget/category
     */
    @PostMapping("/category")
    public Result<Budget> addCategory(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            if (patientId == null || patientId.isEmpty()) {
                return Result.error("400", "patientId is required");
            }
            
            // Create BudgetCategory from request with guards
            BudgetCategory category = new BudgetCategory();
            if (request.get("name") != null) {
                category.setName(String.valueOf(request.get("name")));
            }
            if (request.get("description") != null) {
                category.setDescription(String.valueOf(request.get("description")));
            }
            if (request.get("categoryBudget") != null) {
                double cb = Double.valueOf(String.valueOf(request.get("categoryBudget")));
                if (cb < 0) {
                    return Result.error("400", "categoryBudget cannot be negative");
                }
                category.setCategoryBudget(cb);
            }

            Budget updatedBudget = budgetService.addCategory(patientId, category);
            return Result.success(updatedBudget, "Category added successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to add category: " + e.getMessage());
        }
    }

    /**
     * Add new sub-element to a category
     * POST /api/budget/sub-element
     */
    @PostMapping("/sub-element")
    public Result<Budget> addSubElement(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            if (patientId == null || patientId.isEmpty()) {
                return Result.error("400", "patientId is required");
            }
            if (categoryId == null || categoryId.isEmpty()) {
                return Result.error("400", "categoryId is required");
            }
            
            // Create BudgetSubElement from request with guards
            BudgetSubElement subElement = new BudgetSubElement();
            if (request.get("name") != null) {
                subElement.setName(String.valueOf(request.get("name")));
            }
            if (request.get("description") != null) {
                subElement.setDescription(String.valueOf(request.get("description")));
            }
            if (request.get("subElementBudget") != null) {
                double seb = Double.valueOf(String.valueOf(request.get("subElementBudget")));
                if (seb < 0) {
                    return Result.error("400", "subElementBudget cannot be negative");
                }
                subElement.setSubElementBudget(seb);
            }

            Budget updatedBudget = budgetService.addSubElement(patientId, categoryId, subElement);
            return Result.success(updatedBudget, "Sub-element added successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to add sub-element: " + e.getMessage());
        }
    }

    /**
     * Update monthly usage for a sub-element
     * POST /api/budget/monthly-usage
     */
    @PostMapping("/monthly-usage")
    public Result<Budget> updateMonthlyUsage(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String subElementId = (String) request.get("subElementId");
            if (patientId == null || categoryId == null || subElementId == null) {
                return Result.error("400", "patientId, categoryId and subElementId are required");
            }
            if (request.get("month") == null || request.get("amount") == null) {
                return Result.error("400", "month and amount are required");
            }
            Integer month = Integer.valueOf(String.valueOf(request.get("month")));
            Double amount = Double.valueOf(String.valueOf(request.get("amount")));

            Budget updatedBudget = budgetService.updateMonthlyUsage(patientId, categoryId, subElementId, month, amount);
            return Result.success(updatedBudget, "Monthly usage updated successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to update monthly usage: " + e.getMessage());
        }
    }

    /**
     * Bulk update monthly usage for a sub-element (12 months array)
     * POST /api/budget/monthly-usage/bulk
     */
    @PostMapping("/monthly-usage/bulk")
    public Result<Budget> updateMonthlyUsageBulk(@RequestBody Map<String, Object> request) {
        try {
            String patientId = (String) request.get("patientId");
            String categoryId = (String) request.get("categoryId");
            String subElementId = (String) request.get("subElementId");
            if (patientId == null || categoryId == null || subElementId == null) {
                return Result.error("400", "patientId, categoryId and subElementId are required");
            }
            @SuppressWarnings("unchecked")
            List<Object> raw = (List<Object>) request.get("monthlyUsage");
            if (raw == null) {
                return Result.error("400", "monthlyUsage is required and must be an array of 12 numbers");
            }
            java.util.ArrayList<Double> monthly = new java.util.ArrayList<>();
            for (Object v : raw) {
                if (v == null) {
                    monthly.add(0.0);
                } else if (v instanceof Number) {
                    monthly.add(((Number) v).doubleValue());
                } else {
                    monthly.add(Double.valueOf(v.toString()));
                }
            }
            Budget updated = budgetService.updateMonthlyUsageBulk(patientId, categoryId, subElementId, monthly);
            return Result.success(updated, "Monthly usage updated successfully!");
        } catch (Exception e) {
            return Result.error("400", "Failed to update monthly usage: " + e.getMessage());
        }
    }

    // Query operations


    // Budget analysis and warnings

    /**
     * Get budget warnings for a patient
     * GET /api/budget/warnings/{patientId}
     */
    @GetMapping("/warnings/{patientId}")
    public Result<List<String>> getBudgetWarnings(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            List<String> warnings = budgetCalculationService.getBudgetWarnings(budget);
            return Result.success(warnings, "Budget warnings retrieved successfully!");
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget warnings: " + e.getMessage());
        }
    }

    /**
     * Get budget statistics for a patient
     * GET /api/budget/statistics/{patientId}
     */
    @GetMapping("/statistics/{patientId}")
    public Result<Map<String, Object>> getBudgetStatistics(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            
            Map<String, Object> statistics = Map.of(
                "totalBudget", budget.getTotalBudget(),
                "totalUsed", budgetCalculationService.calculateTotalUsed(budget),
                "totalBalance", budgetCalculationService.calculateTotalBalance(budget),
                "usagePercentage", budgetCalculationService.calculateUsagePercentage(budget),
                "isOverBudget", budgetCalculationService.isOverBudget(budget),
                "warnings", budgetCalculationService.getBudgetWarnings(budget)
            );
            
            return Result.success(statistics, "Budget statistics retrieved successfully!");
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget statistics: " + e.getMessage());
        }
    }

    /**
     * Validate budget data
     * POST /api/budget/validate
     */
    @PostMapping("/validate")
    public Result<List<String>> validateBudget(@RequestBody Budget budget) {
        try {
            List<String> validationErrors = budgetCalculationService.validateBudget(budget);
            if (validationErrors.isEmpty()) {
                return Result.success(validationErrors, "Budget validation passed!");
            } else {
                return Result.success(validationErrors, "Budget validation failed with " + validationErrors.size() + " errors");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to validate budget: " + e.getMessage());
        }
    }

    // ========== Additional API endpoints for frontend compatibility ==========

    /**
     * Get budget categories by patient ID
     * GET /api/budget/patient/{patientId}/categories
     */
    @GetMapping("/patient/{patientId}/categories")
    public Result<List<BudgetCategory>> getBudgetCategories(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null && budget.getCategories() != null) {
                return Result.success(budget.getCategories(), "Budget categories retrieved successfully!");
            } else {
                return Result.success(List.of(), "No categories found for this patient!");
            }
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget categories: " + e.getMessage());
        }
    }

    /**
     * Create budget category for patient
     * POST /api/budget/patient/{patientId}/categories
     */
    @PostMapping("/patient/{patientId}/categories")
    public Result<BudgetCategory> createBudgetCategory(@PathVariable String patientId, @RequestBody BudgetCategory categoryData) {
        try {
            Budget updatedBudget = budgetService.addCategory(patientId, categoryData);
            if (updatedBudget != null && updatedBudget.getCategories() != null) {
                // Find the newly created category (assuming it's the last one)
                List<BudgetCategory> categories = updatedBudget.getCategories();
                BudgetCategory newCategory = categories.get(categories.size() - 1);
                return Result.success(newCategory, "Budget category created successfully!");
            } else {
                return Result.error("400", "Failed to create budget category!");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to create budget category: " + e.getMessage());
        }
    }

    /**
     * Update budget category
     * PUT /api/budget/patient/{patientId}/categories/{categoryId}
     */
    @PutMapping("/patient/{patientId}/categories/{categoryId}")
    public Result<BudgetCategory> updateBudgetCategory(@PathVariable String patientId, @PathVariable String categoryId, @RequestBody BudgetCategory categoryData) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null && budget.getCategories() != null) {
                for (BudgetCategory category : budget.getCategories()) {
                    if (category.getId().equals(categoryId)) {
                        if (categoryData.getName() != null && !categoryData.getName().isEmpty()) {
                            category.setName(categoryData.getName());
                        }
                        if (categoryData.getDescription() != null) {
                            category.setDescription(categoryData.getDescription());
                        }
                        if (categoryData.getCategoryBudget() > 0) {
                            category.setCategoryBudget(categoryData.getCategoryBudget());
                        }
                        
                        budgetService.updateBudget(budget);
                        return Result.success(category, "Budget category updated successfully!");
                    }
                }
                return Result.error("404", "Category not found!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to update budget category: " + e.getMessage());
        }
    }

    /**
     * Delete budget category
     * DELETE /api/budget/patient/{patientId}/categories/{categoryId}
     */
    @DeleteMapping("/patient/{patientId}/categories/{categoryId}")
    public Result<String> deleteBudgetCategory(@PathVariable String patientId, @PathVariable String categoryId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null && budget.getCategories() != null) {
                budget.getCategories().removeIf(category -> category.getId().equals(categoryId));
                budgetService.updateBudget(budget);
                return Result.success("Category deleted", "Budget category deleted successfully!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to delete budget category: " + e.getMessage());
        }
    }

    /**
     * Get budget sub-elements by category
     * GET /api/budget/patient/{patientId}/categories/{categoryId}/sub-elements
     */
    @GetMapping("/patient/{patientId}/categories/{categoryId}/sub-elements")
    public Result<List<BudgetSubElement>> getBudgetSubElements(@PathVariable String patientId, @PathVariable String categoryId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null && budget.getCategories() != null) {
                for (BudgetCategory category : budget.getCategories()) {
                    if (category.getId().equals(categoryId)) {
                        List<BudgetSubElement> subElements = category.getSubElements();
                        return Result.success(subElements != null ? subElements : List.of(), "Budget sub-elements retrieved successfully!");
                    }
                }
                return Result.error("404", "Category not found!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget sub-elements: " + e.getMessage());
        }
    }

    /**
     * Create budget sub-element
     * POST /api/budget/patient/{patientId}/categories/{categoryId}/sub-elements
     */
    @PostMapping("/patient/{patientId}/categories/{categoryId}/sub-elements")
    public Result<BudgetSubElement> createBudgetSubElement(@PathVariable String patientId, @PathVariable String categoryId, @RequestBody BudgetSubElement subElementData) {
        try {
            Budget updatedBudget = budgetService.addSubElement(patientId, categoryId, subElementData);
            if (updatedBudget != null) {
                // Find the newly created sub-element
                for (BudgetCategory category : updatedBudget.getCategories()) {
                    if (category.getId().equals(categoryId) && category.getSubElements() != null) {
                        List<BudgetSubElement> subElements = category.getSubElements();
                        BudgetSubElement newSubElement = subElements.get(subElements.size() - 1);
                        return Result.success(newSubElement, "Budget sub-element created successfully!");
                    }
                }
            }
            return Result.error("400", "Failed to create budget sub-element!");
        } catch (Exception e) {
            return Result.error("400", "Failed to create budget sub-element: " + e.getMessage());
        }
    }

    /**
     * Update budget sub-element
     * PUT /api/budget/patient/{patientId}/categories/{categoryId}/sub-elements/{subElementId}
     */
    @PutMapping("/patient/{patientId}/categories/{categoryId}/sub-elements/{subElementId}")
    public Result<BudgetSubElement> updateBudgetSubElement(@PathVariable String patientId, @PathVariable String categoryId, @PathVariable String subElementId, @RequestBody BudgetSubElement subElementData) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null && budget.getCategories() != null) {
                for (BudgetCategory category : budget.getCategories()) {
                    if (category.getId().equals(categoryId) && category.getSubElements() != null) {
                        for (BudgetSubElement subElement : category.getSubElements()) {
                            if (subElement.getId().equals(subElementId)) {
                                // Only update fields that are provided (not null)
                                if (subElementData.getName() != null && !subElementData.getName().isEmpty()) {
                                    subElement.setName(subElementData.getName());
                                }
                                if (subElementData.getDescription() != null) {
                                    subElement.setDescription(subElementData.getDescription());
                                }
                                // Note: getSubElementBudget() returns double (primitive), so we check if it's > 0 to determine if it's provided
                                // For now, we only update if a new value is explicitly provided (non-zero)
                                if (subElementData.getSubElementBudget() > 0) {
                                    subElement.setSubElementBudget(subElementData.getSubElementBudget());
                                }
                                
                                // Handle monthly usage update - if provided, use bulk update
                                if (subElementData.getMonthlyUsage() != null && subElementData.getMonthlyUsage().size() == 12) {
                                    budget = budgetService.updateMonthlyUsageBulk(patientId, categoryId, subElementId, subElementData.getMonthlyUsage());
                                    // Re-find the updated sub-element
                                    for (BudgetCategory updatedCategory : budget.getCategories()) {
                                        if (updatedCategory.getId().equals(categoryId) && updatedCategory.getSubElements() != null) {
                                            for (BudgetSubElement updatedSubElement : updatedCategory.getSubElements()) {
                                                if (updatedSubElement.getId().equals(subElementId)) {
                                                    return Result.success(updatedSubElement, "Budget sub-element updated successfully!");
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Update other fields if monthlyUsage is not provided
                                    budget.setUpdatedAt(LocalDateTime.now());
                                    budget = budgetCalculationService.calculateBudgetMetrics(budget);
                                    budgetService.updateBudget(budget);
                                }
                                
                                return Result.success(subElement, "Budget sub-element updated successfully!");
                            }
                        }
                        return Result.error("404", "Sub-element not found!");
                    }
                }
                return Result.error("404", "Category not found!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to update budget sub-element: " + e.getMessage());
        }
    }

    /**
     * Delete budget sub-element
     * DELETE /api/budget/patient/{patientId}/categories/{categoryId}/sub-elements/{subElementId}
     */
    @DeleteMapping("/patient/{patientId}/categories/{categoryId}/sub-elements/{subElementId}")
    public Result<String> deleteBudgetSubElement(@PathVariable String patientId, @PathVariable String categoryId, @PathVariable String subElementId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null && budget.getCategories() != null) {
                for (BudgetCategory category : budget.getCategories()) {
                    if (category.getId().equals(categoryId) && category.getSubElements() != null) {
                        boolean removed = category.getSubElements().removeIf(subElement -> subElement.getId().equals(subElementId));
                        if (removed) {
                            budgetService.updateBudget(budget);
                            return Result.success("Sub-element deleted", "Budget sub-element deleted successfully!");
                        } else {
                            return Result.error("404", "Sub-element not found!");
                        }
                    }
                }
                return Result.error("404", "Category not found!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("400", "Failed to delete budget sub-element: " + e.getMessage());
        }
    }

    /**
     * Get budget calculations
     * GET /api/budget/patient/{patientId}/calculations
     */
    @GetMapping("/patient/{patientId}/calculations")
    public Result<Map<String, Object>> getBudgetCalculations(@PathVariable String patientId) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null) {
                Map<String, Object> calculations = Map.of(
                    "totalBudget", budget.getTotalBudget(),
                    "totalUsed", budgetCalculationService.calculateTotalUsed(budget),
                    "totalBalance", budgetCalculationService.calculateTotalBalance(budget),
                    "usagePercentage", budgetCalculationService.calculateUsagePercentage(budget),
                    "isOverBudget", budgetCalculationService.isOverBudget(budget)
                );
                return Result.success(calculations, "Budget calculations retrieved successfully!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget calculations: " + e.getMessage());
        }
    }

    /**
     * Get budget reports
     * GET /api/budget/patient/{patientId}/reports
     */
    @GetMapping("/patient/{patientId}/reports")
    public Result<List<Map<String, Object>>> getBudgetReports(@PathVariable String patientId, @RequestParam(required = false) String type, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
        try {
            Budget budget = budgetService.getBudgetByPatientId(patientId);
            if (budget != null) {
                // Generate mock reports based on the budget data
                List<Map<String, Object>> reports = List.of(
                    Map.of(
                        "reportId", "R001",
                        "reportType", type != null ? type : "monthly",
                        "generatedDate", java.time.LocalDate.now().toString(),
                        "totalBudget", budget.getTotalBudget(),
                        "totalUsed", budgetCalculationService.calculateTotalUsed(budget),
                        "categories", budget.getCategories() != null ? budget.getCategories().size() : 0
                    )
                );
                return Result.success(reports, "Budget reports retrieved successfully!");
            } else {
                return Result.error("404", "Budget not found!");
            }
        } catch (Exception e) {
            return Result.error("404", "Failed to retrieve budget reports: " + e.getMessage());
        }
    }

}
