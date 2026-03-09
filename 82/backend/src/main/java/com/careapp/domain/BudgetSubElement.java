package com.careapp.domain;

import java.util.List;

/**
 * BudgetSubElement represents a specific item within a budget category
 * For example: "Soap" under "Hygiene Products" category
 */
public class BudgetSubElement {
    private String id;                    // Unique identifier
    private String name;                  // Sub-element name (e.g., "Soap")
    private String description;           // Description of the sub-element
    private double subElementBudget;      // Budget allocated for this sub-element
    private List<Double> monthlyUsage;    // Monthly usage for 12 months [Jan, Feb, Mar, ...]
    private double totalUtilised;         // Total amount used so far
    private double balance;               // Remaining balance (budget - used)
    private String comments;              // Additional comments
    private String warningLevel;          // Warning level: normal, warning, critical
    
    /**
     * Default constructor - initializes with empty values
     */
    public BudgetSubElement() {
        // Initialize monthly usage with 12 zeros (Jan-Dec)
        this.monthlyUsage = List.of(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        this.totalUtilised = 0.0;
        this.balance = 0.0;
        this.warningLevel = "normal";
    }
    
    /**
     * Constructor with name and budget
     * @param name Sub-element name
     * @param subElementBudget Budget amount
     */
    public BudgetSubElement(String name, double subElementBudget) {
        this();  // Call default constructor first
        this.name = name;
        this.subElementBudget = subElementBudget;
        this.balance = subElementBudget;  // Initially, balance equals budget
    }
    
    /**
     * Calculate total amount used by summing all monthly usage
     */
    public void calculateTotalUtilised() {
        this.totalUtilised = monthlyUsage.stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    }
    
    /**
     * Calculate remaining balance (budget - used)
     */
    public void calculateBalance() {
        this.balance = this.subElementBudget - this.totalUtilised;
    }
    
    /**
     * Calculate warning level based on usage percentage
     * - normal: < 80% used
     * - warning: 80-99% used  
     * - critical: 100%+ used
     */
    public void calculateWarningLevel() {
        if (this.subElementBudget <= 0) {
            this.warningLevel = "normal";
            return;
        }
        
        double percentage = (this.totalUtilised / this.subElementBudget) * 100;
        if (percentage >= 100) {
            this.warningLevel = "critical";
        } else if (percentage >= 80) {
            this.warningLevel = "warning";
        } else {
            this.warningLevel = "normal";
        }
    }
    
    /**
     * Update monthly usage for a specific month
     * @param month Month index (0-11, where 0=Jan, 11=Dec)
     * @param amount Amount used in that month
     */
    public void updateMonthlyUsage(int month, double amount) {
        if (month >= 0 && month < 12) {
            this.monthlyUsage.set(month, amount);
            calculateTotalUtilised();    // Recalculate total
            calculateBalance();          // Recalculate balance
            calculateWarningLevel();     // Recalculate warning level
        }
    }
    
    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getSubElementBudget() { return subElementBudget; }
    public void setSubElementBudget(double subElementBudget) { 
        this.subElementBudget = subElementBudget;
        calculateBalance();
        calculateWarningLevel();
    }

    public List<Double> getMonthlyUsage() { return monthlyUsage; }
    public void setMonthlyUsage(List<Double> monthlyUsage) { 
        this.monthlyUsage = monthlyUsage;
        calculateTotalUtilised();
        calculateBalance();
        calculateWarningLevel();
    }

    public double getTotalUtilised() { return totalUtilised; }
    public void setTotalUtilised(double totalUtilised) { this.totalUtilised = totalUtilised; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getWarningLevel() { return warningLevel; }
    public void setWarningLevel(String warningLevel) { this.warningLevel = warningLevel; }
}
