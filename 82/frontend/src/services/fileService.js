// src/services/fileService.js
import api from "./api";

// Upload any file (generic file upload)
export async function uploadFile(file, options = {}) {
    try {
        const formData = new FormData();
        formData.append('file', file);
        
        // Optional parameters
        if (options.category) {
            formData.append('category', options.category);
        }
        if (options.uploadedBy) {
            formData.append('uploadedBy', options.uploadedBy);
        }
        if (options.comment) {
            formData.append('comment', options.comment);
        }
        
        const response = await api.post('/files/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to upload file');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to upload file');
        }
    }
}

// Get all files
export async function getAllFiles(category = null, uploadedBy = null) {
    try {
        const params = {};
        if (category) params.category = category;
        if (uploadedBy) params.uploadedBy = uploadedBy;
        
        const response = await api.get('/files', { params });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get files');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get files');
        }
    }
}

// Get file by ID
export async function getFileById(fileId) {
    try {
        const response = await api.get(`/files/${fileId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get file');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get file');
        }
    }
}

// Update file comment
export async function updateFileComment(fileId, comment) {
    try {
        const response = await api.put(`/files/${fileId}/comment`, { comment });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update file comment');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update file comment');
        }
    }
}

// Delete file
export async function deleteFile(fileId) {
    try {
        const response = await api.delete(`/files/${fileId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete file');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete file');
        }
    }
}

