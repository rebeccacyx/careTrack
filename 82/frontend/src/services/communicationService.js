// src/services/communicationService.js
import api from "./api";

// Helper function to map backend Message to frontend format
function mapMessage(message) {
    return {
        id: message.id,
        subject: message.subject,
        from: message.fromUserName || message.fromUserId || 'Unknown',
        date: message.createdAt ? formatDate(message.createdAt) : '',
        status: message.status || 'sent',
        content: message.content || ''
    };
}

// Helper function to format date
function formatDate(dateString) {
    if (!dateString) return '';
    try {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    } catch (e) {
        return dateString;
    }
}

// Get all messages for current user (both sent and received)
export async function getAllMessages() {
    try {
        // Get all messages for current user (sent and received)
        const response = await api.get('/messages');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            const mappedMessages = result.data.map(mapMessage);
            return {
                data: mappedMessages
            };
        } else {
            throw new Error(result.msg || 'Failed to get messages');
        }
    } catch (error) {
        console.error('Failed to get messages:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        }
        throw new Error('Failed to get messages');
    }
}

// Send a new message
export async function sendMessage(messageData) {
    try {
        // Prepare request body
        const requestBody = {
            subject: messageData.subject,
            content: messageData.content,
            toUserId: messageData.to,
            toUserName: messageData.toUserName || messageData.to,
            fromUserName: messageData.from || 'Current User',
            category: messageData.category || 'general'
        };
        
        const response = await api.post('/messages', requestBody);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: mapMessage(result.data)
            };
        } else {
            throw new Error(result.msg || 'Failed to send message');
        }
    } catch (error) {
        console.error('Failed to send message:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        }
        throw new Error('Failed to send message');
    }
}

// Reply to a message
export async function replyToMessage(messageId, replyData) {
    try {
        const requestBody = {
            content: replyData.content,
            subject: replyData.subject || `Re: ${replyData.originalSubject}`,
            fromUserName: replyData.from || 'Current User'
        };
        
        const response = await api.post(`/messages/${messageId}/reply`, requestBody);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: mapMessage(result.data)
            };
        } else {
            throw new Error(result.msg || 'Failed to reply to message');
        }
    } catch (error) {
        console.error('Failed to reply to message:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        }
        throw new Error('Failed to reply to message');
    }
}

// Mark message as read
export async function markMessageAsRead(messageId) {
    try {
        const response = await api.put(`/messages/${messageId}/read`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: { success: true }
            };
        } else {
            throw new Error(result.msg || 'Failed to mark message as read');
        }
    } catch (error) {
        console.error('Failed to mark message as read:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        }
        throw new Error('Failed to mark message as read');
    }
}

// Delete a message
export async function deleteMessage(messageId) {
    try {
        const response = await api.delete(`/messages/${messageId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: { success: true }
            };
        } else {
            throw new Error(result.msg || 'Failed to delete message');
        }
    } catch (error) {
        console.error('Failed to delete message:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        }
        throw new Error('Failed to delete message');
    }
}

// Get message by ID
export async function getMessageById(messageId) {
    try {
        const response = await api.get(`/messages/${messageId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: mapMessage(result.data)
            };
        } else {
            throw new Error(result.msg || 'Failed to get message');
        }
    } catch (error) {
        console.error('Failed to get message:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        }
        throw new Error('Failed to get message');
    }
}
