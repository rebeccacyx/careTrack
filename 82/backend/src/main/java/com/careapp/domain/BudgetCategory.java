package com.careapp.domain;

import java.util.ArrayList;
import java.util.List;

public class BudgetCategory {
    private String id;
    private String name;
    private String description;
    private double categoryBudget;      // category budget
    private List<BudgetSubElement> subElements; // sub-element list
    
    // constructor
    public BudgetCategory() {}
    
    public BudgetCategory(String name, double categoryBudget) {
        this.name = name;
        this.categoryBudget = categoryBudget;
    }
    
    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getCategoryBudget() { return categoryBudget; }
    public void setCategoryBudget(double categoryBudget) { this.categoryBudget = categoryBudget; }

    public List<BudgetSubElement> getSubElements() {
        // Lazy initialization: ensure subElements is never null
        if (subElements == null) {
            subElements = new ArrayList<>();
        }
        return subElements;
    }
    public void setSubElements(List<BudgetSubElement> subElements) { this.subElements = subElements; }
}
