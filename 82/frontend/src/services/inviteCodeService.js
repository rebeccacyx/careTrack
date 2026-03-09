// src/services/inviteCodeService.js
import api from "./api";

// Generate invite code
export async function generateInviteCode(inviteData) {
    try {
        const response = await api.post('/invite/generate', inviteData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to generate invite code');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to generate invite code');
        }
    }
}

// Use invite code
export async function useInviteCode(code, usedBy) {
    try {
        const response = await api.post('/invite/use', {
            code: code,
            usedBy: usedBy
        });
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to use invite code');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to use invite code');
        }
    }
}

// Get my invite codes
export async function getMyInviteCodes(creatorId) {
    try {
        const response = await api.get(`/invite/my-codes?creatorId=${creatorId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get invite codes');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get invite codes');
        }
    }
}

// Revoke invite code
export async function revokeInviteCode(codeId) {
    try {
        const response = await api.delete(`/invite/${codeId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to revoke invite code');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to revoke invite code');
        }
    }
}

// Get active invite codes for patient
export async function getActiveInviteCodesForPatient(patientId) {
    try {
        const response = await api.get(`/invite/patient/${patientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get active invite codes');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get active invite codes');
        }
    }
}
