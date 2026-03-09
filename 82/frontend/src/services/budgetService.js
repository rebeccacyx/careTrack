// src/services/budgetService.js
import api from "./api";

// Get budget by patient ID
export async function getBudgetByPatient(patientId) {
    try {
        const response = await api.get(`/budget/patient/${patientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get budget');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get budget');
        }
    }
}

// Create a new budget
export async function createBudget(budgetData) {
    try {
        const response = await api.post('/budget', budgetData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create budget');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create budget');
        }
    }
}

// Update budget
export async function updateBudget(budgetData) {
    try {
        // Backend expects full Budget object with ID
        if (!budgetData.id) {
            throw new Error('Budget ID is required for update');
        }
        
        const response = await api.put('/budget', budgetData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update budget');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update budget');
        }
    }
}

// Adjust total budget (uses dedicated endpoint)
export async function adjustTotalBudget(patientId, newTotalBudget, reason) {
    try {
        const response = await api.post('/budget/adjust-total', {
            patientId,
            newTotalBudget,
            reason
        });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to adjust total budget');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to adjust total budget');
        }
    }
}

// Delete budget
export async function deleteBudget(budgetId) {
    try {
        const response = await api.delete(`/budget/${budgetId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete budget');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete budget');
        }
    }
}

// Get budget categories
export async function getBudgetCategories(patientId) {
    try {
        const response = await api.get(`/budget/patient/${patientId}/categories`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get budget categories');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get budget categories');
        }
    }
}

// Create budget category
export async function createBudgetCategory(patientId, categoryData) {
    try {
        const response = await api.post(`/budget/patient/${patientId}/categories`, categoryData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create budget category');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create budget category');
        }
    }
}

// Update budget category
export async function updateBudgetCategory(patientId, categoryId, categoryData) {
    try {
        const response = await api.put(`/budget/patient/${patientId}/categories/${categoryId}`, categoryData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update budget category');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update budget category');
        }
    }
}

// Delete budget category
export async function deleteBudgetCategory(patientId, categoryId) {
    try {
        const response = await api.delete(`/budget/patient/${patientId}/categories/${categoryId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete budget category');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete budget category');
        }
    }
}

// Get budget sub-elements
export async function getBudgetSubElements(patientId, categoryId) {
    try {
        const response = await api.get(`/budget/patient/${patientId}/categories/${categoryId}/sub-elements`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get budget sub-elements');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get budget sub-elements');
        }
    }
}

// Create budget sub-element
export async function createBudgetSubElement(patientId, categoryId, subElementData) {
    try {
        const response = await api.post(`/budget/patient/${patientId}/categories/${categoryId}/sub-elements`, subElementData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create budget sub-element');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create budget sub-element');
        }
    }
}

// Update budget sub-element
export async function updateBudgetSubElement(patientId, categoryId, subElementId, subElementData) {
    try {
        const response = await api.put(`/budget/patient/${patientId}/categories/${categoryId}/sub-elements/${subElementId}`, subElementData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update budget sub-element');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update budget sub-element');
        }
    }
}

// Delete budget sub-element
export async function deleteBudgetSubElement(patientId, categoryId, subElementId) {
    try {
        const response = await api.delete(`/budget/patient/${patientId}/categories/${categoryId}/sub-elements/${subElementId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete budget sub-element');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete budget sub-element');
        }
    }
}

// Get budget calculations
export async function getBudgetCalculations(patientId) {
    try {
        const response = await api.get(`/budget/patient/${patientId}/calculations`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get budget calculations');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get budget calculations');
        }
    }
}

// Get budget reports
export async function getBudgetReports(patientId, reportType, startDate, endDate) {
    try {
        const params = new URLSearchParams();
        if (reportType) params.append('type', reportType);
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);
        
        const response = await api.get(`/budget/patient/${patientId}/reports?${params.toString()}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get budget reports');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get budget reports');
        }
    }
}
