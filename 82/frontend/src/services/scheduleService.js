// src/services/scheduleService.js
import api from "./api";

// Get all schedules
export async function getAllSchedules() {
    try {
        const response = await api.get('/schedules');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get schedules');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get schedules');
        }
    }
}

// Get schedule by ID
export async function getScheduleById(scheduleId) {
    try {
        const response = await api.get(`/schedules/${scheduleId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get schedule');
        }
    }
}

// Create a new schedule
export async function createSchedule(scheduleData) {
    try {
        const response = await api.post('/schedules', scheduleData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create schedule');
        }
    }
}

// Update schedule
export async function updateSchedule(scheduleId, scheduleData) {
    try {
        const response = await api.put(`/schedules/${scheduleId}`, scheduleData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update schedule');
        }
    }
}

// Delete schedule
export async function deleteSchedule(scheduleId) {
    try {
        const response = await api.delete(`/schedules/${scheduleId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete schedule');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete schedule');
        }
    }
}

// Get schedules by patient
export async function getSchedulesByPatient(patientId) {
    try {
        const response = await api.get(`/schedules/patient/${patientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get schedules by patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get schedules by patient');
        }
    }
}

// Get schedules by worker
export async function getSchedulesByWorker(workerId) {
    try {
        const response = await api.get(`/schedules/worker/${workerId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get schedules by worker');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get schedules by worker');
        }
    }
}

// Get schedules by date range
export async function getSchedulesByDateRange(startDate, endDate) {
    try {
        const params = new URLSearchParams();
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);
        
        const response = await api.get(`/schedules/date-range?${params.toString()}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get schedules by date range');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get schedules by date range');
        }
    }
}
