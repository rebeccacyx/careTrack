// src/services/workerService.js
import api from "./api";

// Get all workers
export async function getAllWorkers() {
    try {
        const response = await api.get('/workers');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get workers');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get workers');
        }
    }
}

// Get workers by organization
export async function getWorkersByOrganization(organizationId) {
    try {
        const response = await api.get(`/workers/organization/${organizationId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get workers by organization');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get workers by organization');
        }
    }
}

// Create a new worker
export async function createWorker(workerData) {
    try {
        const response = await api.post('/workers', workerData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create worker');
        }
    }
}

// Update worker
export async function updateWorker(workerId, workerData) {
    try {
        const response = await api.put(`/workers/${workerId}`, workerData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update worker');
        }
    }
}

// Delete worker
export async function deleteWorker(workerId) {
    try {
        const response = await api.delete(`/workers/${workerId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete worker');
        }
    }
}

// Activate worker
export async function activateWorker(workerId) {
    try {
        const response = await api.post(`/workers/${workerId}/activate`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to activate worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to activate worker');
        }
    }
}

// Deactivate worker
export async function deactivateWorker(workerId) {
    try {
        const response = await api.post(`/workers/${workerId}/deactivate`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to deactivate worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to deactivate worker');
        }
    }
}

// Get available workers for scheduling
export async function getAvailableWorkers(organizationId) {
    try {
        const response = await api.get(`/workers/organization/${organizationId}/available`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get available workers');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get available workers');
        }
    }
}

// Create daily schedule
export async function createDailySchedule(scheduleData, managerId) {
    try {
        const response = await api.post('/workers/daily-schedule', scheduleData, {
            headers: {
                'X-Manager-Id': managerId
            }
        });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create daily schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create daily schedule');
        }
    }
}

// Get daily schedule for a specific date
export async function getDailySchedule(organizationId, date) {
    try {
        const response = await api.get(`/workers/organization/${organizationId}/daily-schedule/${date}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get daily schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get daily schedule');
        }
    }
}

// Clear daily schedule for a specific date
export async function clearDailySchedule(organizationId, date) {
    try {
        const response = await api.delete(`/workers/organization/${organizationId}/daily-schedule/${date}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to clear daily schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to clear daily schedule');
        }
    }
}

// Upload worker photo (for worker profile photo)
export async function uploadWorkerPhoto(workerId, photoData) {
    try {
        const formData = new FormData();
        formData.append('photo', photoData);
        
        // Don't set Content-Type header - let browser set it automatically with boundary
        const response = await api.post(`/workers/${workerId}/photo`, formData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to upload worker photo');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to upload worker photo');
        }
    }
}

// Get workers by manager ID
export async function getWorkersByManagerId(managerId) {
    try {
        const response = await api.get(`/workers/manager/${managerId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get workers by manager ID');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get workers by manager ID');
        }
    }
}
