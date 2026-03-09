// src/services/patientService.js
import api from "./api";

// Get all patients
export async function getAllPatients() {
    try {
        const response = await api.get('/patients');
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get patients');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get patients');
        }
    }
}

// Get patient by ID
export async function getPatientById(patientId) {
    try {
        const response = await api.get(`/patients/${patientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get patient');
        }
    }
}

// Create a new patient
export async function createPatient(patientData) {
    try {
        const response = await api.post('/patients', patientData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to create patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to create patient');
        }
    }
}

// Update patient
export async function updatePatient(patientId, patientData) {
    try {
        const response = await api.put(`/patients/${patientId}`, patientData);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update patient');
        }
    }
}

// Delete patient
export async function deletePatient(patientId) {
    try {
        const response = await api.delete(`/patients/${patientId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to delete patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to delete patient');
        }
    }
}

// Get patients by organization
export async function getPatientsByOrganization(organizationId) {
    try {
        const response = await api.get(`/patients/organization/${organizationId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get patients by organization');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get patients by organization');
        }
    }
}

// Get patients by family member/POA
export async function getPatientsByFamilyMember(familyMemberId) {
    try {
        const response = await api.get(`/patients/family-member/${familyMemberId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get patients by family member');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get patients by family member');
        }
    }
}

// Get patient by Client ID
export async function getPatientByClientId(clientId) {
    try {
        const response = await api.get(`/patients/client/${clientId}`);
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get patient by Client ID');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to get patient by Client ID');
        }
    }
}

// Remove organization from patient
export async function removeOrganizationFromPatient(patientId, removalData) {
    try {
        const response = await api.post(`/patients/${patientId}/remove-organization`, removalData);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to remove organization from patient');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to remove organization from patient');
        }
    }
}
