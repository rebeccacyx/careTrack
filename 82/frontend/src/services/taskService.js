// src/services/taskService.js
import api from "./api";

// Get all tasks
export async function getAllTasks() {
    try {
        const response = await api.get('/tasks');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get tasks');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get tasks');
        }
    }
}

// Get tasks by patient
export async function getTasksByPatient(patientId) {
    try {
        const response = await api.get(`/tasks/patient/${patientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get tasks by patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get tasks by patient');
        }
    }
}

// Get tasks by worker
export async function getTasksByWorker(workerId) {
    try {
        const response = await api.get(`/tasks/worker/${workerId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get tasks by worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get tasks by worker');
        }
    }
}

// Create a new task
export async function createTask(taskData) {
    try {
        const response = await api.post('/tasks', taskData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create task');
        }
    }
}

// Update task
export async function updateTask(taskData) {
    try {
        // Extract taskId from taskData
        const taskId = taskData.id;
        if (!taskId) {
            throw new Error('Task ID is required');
        }
        
        // Remove id from taskData before sending (it's in the URL)
        const { id, ...updateData } = taskData;
        
        const response = await api.put(`/tasks/${taskId}`, updateData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update task');
        }
    }
}

// Delete task
export async function deleteTask(taskId) {
    try {
        const response = await api.delete(`/tasks/${taskId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete task');
        }
    }
}

// Assign task to worker
export async function assignTask(taskId, workerId) {
    try {
        const response = await api.post(`/tasks/${taskId}/assign`, { workerId });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to assign task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to assign task');
        }
    }
}

// Complete task
export async function completeTask(taskId, completionData) {
    try {
        const response = await api.post(`/tasks/${taskId}/complete`, completionData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to complete task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to complete task');
        }
    }
}

// Approve task completion
export async function approveTask(taskId, approvalData) {
    try {
        const response = await api.post(`/tasks/${taskId}/approve`, approvalData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to approve task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to approve task');
        }
    }
}

// Reject task completion
export async function rejectTask(taskId, rejectionData) {
    try {
        const response = await api.post(`/tasks/${taskId}/reject`, rejectionData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to reject task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to reject task');
        }
    }
}

// Get recurring tasks
export async function getRecurringTasks() {
    try {
        const response = await api.get('/tasks/recurring');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get recurring tasks');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get recurring tasks');
        }
    }
}

// Create recurring task
export async function createRecurringTask(taskData) {
    try {
        const response = await api.post('/tasks/recurring', taskData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create recurring task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create recurring task');
        }
    }
}

// Update recurring task
export async function updateRecurringTask(taskId, taskData) {
    try {
        const response = await api.put(`/tasks/recurring/${taskId}`, taskData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update recurring task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update recurring task');
        }
    }
}

// Delete recurring task
export async function deleteRecurringTask(taskId) {
    try {
        const response = await api.delete(`/tasks/recurring/${taskId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete recurring task');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete recurring task');
        }
    }
}

// ==================== Task Request APIs ====================

// Create a new task request (POA submits request)
export async function createTaskRequest(requestData) {
    try {
        const response = await api.post('/tasks/requests', requestData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create task request');
        }
    }
}

// Get pending task requests (Manager view)
export async function getPendingTaskRequests() {
    try {
        const response = await api.get('/tasks/requests/pending');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get pending task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get pending task requests');
        }
    }
}

// Get all task requests by requester (POA view)
export async function getMyTaskRequests() {
    try {
        const response = await api.get('/tasks/requests/my-requests');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get my task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get my task requests');
        }
    }
}

// Get all task requests by organization (Manager view all)
export async function getAllTaskRequests() {
    try {
        const response = await api.get('/tasks/requests');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get all task requests');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get all task requests');
        }
    }
}

// Approve a task request (Manager action)
export async function approveTaskRequest(requestId, approvalData) {
    try {
        const response = await api.post(`/tasks/requests/${requestId}/approve`, approvalData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to approve task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to approve task request');
        }
    }
}

// Reject a task request (Manager action)
export async function rejectTaskRequest(requestId, rejectionData) {
    try {
        const response = await api.post(`/tasks/requests/${requestId}/reject`, rejectionData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to reject task request');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to reject task request');
        }
    }
}
