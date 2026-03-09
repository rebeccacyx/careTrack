// src/services/notificationService.js
import api from "./api";

// Get all notifications for current user
export async function getMyNotifications() {
    try {
        const response = await api.get('/notifications/my');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get notifications');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get notifications');
        }
    }
}

// Get notifications by recipient ID
export async function getNotificationsByRecipient(recipientId) {
    try {
        const response = await api.get(`/notifications/recipient/${recipientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get notifications by recipient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get notifications by recipient');
        }
    }
}

// Get unread notifications
export async function getUnreadNotifications() {
    try {
        const response = await api.get('/notifications/unread');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get unread notifications');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get unread notifications');
        }
    }
}

// Get unread notification count
export async function getUnreadCount() {
    try {
        const response = await api.get('/notifications/unread/count');
        const result = response.data;
        
        if (result.code === "0" && result.data !== undefined) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get unread count');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get unread count');
        }
    }
}

// Mark notification as read
export async function markAsRead(notificationId) {
    try {
        const response = await api.put(`/notifications/${notificationId}/read`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to mark notification as read');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to mark notification as read');
        }
    }
}

// Mark all notifications as read
export async function markAllAsRead() {
    try {
        const response = await api.put('/notifications/read-all');
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to mark all notifications as read');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to mark all notifications as read');
        }
    }
}

// Create notification
export async function createNotification(notificationData) {
    try {
        const response = await api.post('/notifications', notificationData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create notification');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create notification');
        }
    }
}

// Delete notification
export async function deleteNotification(notificationId) {
    try {
        const response = await api.delete(`/notifications/${notificationId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete notification');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete notification');
        }
    }
}

// Get notifications by type
export async function getNotificationsByType(type) {
    try {
        const response = await api.get(`/notifications/type/${type}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get notifications by type');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get notifications by type');
        }
    }
}

// Get notifications by category
export async function getNotificationsByCategory(category) {
    try {
        const response = await api.get(`/notifications/category/${category}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get notifications by category');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get notifications by category');
        }
    }
}

