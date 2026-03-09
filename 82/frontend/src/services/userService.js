// src/services/userService.js
import api from "./api";  // This api.js is the previously created axios configuration

// List of removed workers (in real app, this would be stored in backend)
const removedWorkers = [
    // This list will be populated when workers are removed
    // Format: { email: 'worker@example.com', name: 'Worker Name', removedAt: '2024-01-15' }
];

// Login - Connect to real backend API
export async function login(credentials) {
    try {
        // Check if the user is in the removed workers list
        const isRemovedWorker = removedWorkers.some(worker => 
            worker.email === credentials.email || worker.name === credentials.email
        );
        
        if (isRemovedWorker) {
            throw new Error("Access denied: This account has been removed from the system.");
        }
        
        // Call backend login API
        const response = await api.post('/auth/login-email', {
            email: credentials.email,
            password: credentials.password
        });
        
        const result = response.data;
        
        // Check if login was successful
        if (result.code === "0" && result.data) {
            const user = result.data;
            
            // Generate a simple token (in real app, this would come from backend)
            const token = `token_${user.id}_${Date.now()}`;
            
            // Get invite status
            let inviteStatus = { valid: false, reason: "missing" };
            try {
                const inviteResponse = await api.get(`/auth/invite-status?userId=${user.id}`);
                if (inviteResponse.data.code === "0") {
                    inviteStatus = inviteResponse.data.data;
                }
            } catch (e) {
                console.warn('Failed to get invite status:', e);
            }
            
            const userInfo = {
                id: user.id,
                role: user.role || user.userType || 'worker',
                email: user.email,
                name: `${user.firstName || ''} ${user.middleName || ''} ${user.lastName || ''}`.trim() || user.email,
                firstName: user.firstName,
                middleName: user.middleName,
                lastName: user.lastName,
                userType: user.userType,
                organizationId: user.organizationId,
                patientId: user.patientId,
                status: user.status
            };
            
            // Store user ID and organization ID in sessionStorage for API calls
            sessionStorage.setItem('userId', user.id);
            if (user.organizationId) {
                sessionStorage.setItem('organizationId', user.organizationId);
            }
            
            return {
                data: {
                    token: token,
                    user: userInfo,
                    inviteStatus: inviteStatus
                }
            };
        } else {
            throw new Error(result.msg || 'Login failed');
        }
    } catch (error) {
        // Handle network errors or API errors
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Login failed. Please check your credentials and try again.');
        }
    }
}

// Get current user information - Connect to real backend API
export async function getMe() {
    // Get user ID from sessionStorage
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
        console.warn('⚠️ No user ID found in sessionStorage');
        return {
            data: {
                role: "guest",
                email: "guest@example.com",
                name: "Guest User"
            }
        };
    }
    
    try {
        const response = await api.get(`/auth/me?userId=${userId}`);

        const result = response.data;
        
        if (result.code === "0" && result.data) {
            const user = result.data;
            console.log('🔍 userService.js - Raw user data from backend:', user);
            console.log('🔍 userService.js - PatientId from backend:', user.patientId);
            
            // Debug: Check notification settings in raw backend response
            console.log('🔔 userService.js - Raw notification settings from backend:', {
                taskReminders: user.taskReminders,
                approvalNotifications: user.approvalNotifications,
                budgetWarning: user.budgetWarning,
                emailNotifications: user.emailNotifications,
                // Also check if Jackson serialized with 'is' prefix
                'user.taskReminders (typeof)': typeof user.taskReminders,
                'user.approvalNotifications (typeof)': typeof user.approvalNotifications,
                'All user keys': Object.keys(user)
            });
            
            const userInfo = {
                id: user.id,
                role: user.role || user.userType || 'worker',
                email: user.email,
                name: `${user.firstName || ''} ${user.middleName || ''} ${user.lastName || ''}`.trim() || user.email,
                firstName: user.firstName,
                middleName: user.middleName,
                lastName: user.lastName,
                userType: user.userType,
                organizationId: user.organizationId,
                patientId: user.patientId || null, // Explicitly set to null if undefined
                status: user.status,
                managerId: user.managerId || null,
                // Notification settings - include from backend response
                // Handle both possible field names (taskReminders or taskReminders from isTaskReminders())
                taskReminders: user.taskReminders !== undefined ? user.taskReminders : (user.isTaskReminders !== undefined ? user.isTaskReminders : true),
                approvalNotifications: user.approvalNotifications !== undefined ? user.approvalNotifications : (user.isApprovalNotifications !== undefined ? user.isApprovalNotifications : true),
                budgetWarning: user.budgetWarning !== undefined ? user.budgetWarning : (user.isBudgetWarning !== undefined ? user.isBudgetWarning : true),
                emailNotifications: user.emailNotifications !== undefined ? user.emailNotifications : (user.isEmailNotifications !== undefined ? user.isEmailNotifications : true),
                // Additional fields that might be useful
                hobbies: user.hobbies || null
            };
            
            // 绑定检查逻辑 (方案3)
            // 对于 worker 或 manager，如果 patientId 为 null，给出警告
            if ((userInfo.userType === 'WORKER' || userInfo.userType === 'MANAGER' || 
                 userInfo.role === 'worker' || userInfo.role === 'manager') && 
                !userInfo.patientId) {
                console.warn('⚠️ User not bound to a patient yet.', {
                    userId: userInfo.id,
                    userType: userInfo.userType,
                    role: userInfo.role
                });
                
                // 对于 worker，如果已经绑定了 manager，可以尝试自动同步
                if (userInfo.userType === 'WORKER' && userInfo.managerId) {
                    console.log('💡 Worker has manager, you can call sync-patient-from-manager API to fix this.');
                }
            }
            
            console.log('Retrieved user info from backend:', userInfo);
            console.log('🔍 userService.js - Final patientId:', userInfo.patientId);
            return {
                data: userInfo
            };
        } else {
            throw new Error(result.msg || 'Failed to get user information');
        }
    } catch (error) {
        console.error('❌ Failed to get user info from backend:', error);
        
        // Fallback: return basic guest info
        return {
            data: {
                role: "guest",
                email: "guest@example.com",
                name: "Guest User",
                patientId: null
            }
        };
    }
}

// Sync worker patientId from manager (手动同步 API)
export async function syncWorkerPatientFromManager(workerId) {
    try {
        const response = await api.post(`/auth/worker/${workerId}/sync-patient-from-manager`);
        const result = response.data;
        
        if (result.code === "0") {
            console.log('✅ Worker patientId synced from manager successfully');
            return {
                data: result.data,
                success: true
            };
        } else {
            throw new Error(result.msg || 'Failed to sync worker patientId');
        }
    } catch (error) {
        console.error('❌ Failed to sync worker patientId:', error);
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to sync worker patientId from manager');
        }
    }
}

// Get invite status - Connect to real backend API
export async function getInviteStatus() {
    try {
        // Get user ID from sessionStorage
        const userId = sessionStorage.getItem('userId');
        if (!userId) {
            console.warn('No user ID found in sessionStorage for invite status check');
            return {
                data: {
                    valid: false,
                    reason: "missing"
                }
            };
        }
        
        const response = await api.get(`/auth/invite-status?userId=${userId}`);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to get invite status');
        }
    } catch (error) {
        console.error('Failed to get invite status from backend:', error);
        
        // Fallback to sessionStorage check
        const inviteSubmitted = sessionStorage.getItem('inviteSubmitted') === 'true';
        
        return {
            data: {
                valid: inviteSubmitted,
                reason: inviteSubmitted ? "already_used" : "missing"
            }
        };
    }
}

// Submit invite code - Connect to real backend API
export async function submitInviteCode(inviteCode) {
    try {
        const response = await api.post('/auth/submit-invite-code', {
            inviteCode: inviteCode
        });
        
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            // Mark invite code as submitted in sessionStorage for fallback
            sessionStorage.setItem('inviteSubmitted', 'true');
            
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Invalid invite code');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to submit invite code');
        }
    }
}

// Add removed worker to the list (called when a worker is removed)
export function addRemovedWorker(workerInfo) {
    const removedWorker = {
        email: workerInfo.email,
        name: workerInfo.name,
        workerId: workerInfo.workerId,
        removedAt: new Date().toISOString()
    };
    
    removedWorkers.push(removedWorker);
    
    // In a real app, this would be sent to the backend
    console.log('Worker removed from system:', removedWorker);
}

// Get list of removed workers (for debugging/admin purposes)
export function getRemovedWorkers() {
    return [...removedWorkers];
}

// Register function - Connect to real backend API
export async function register(userData) {
    try {
        const response = await api.post('/auth/register', {
            firstName: userData.firstName,
            middleName: userData.middleName,
            lastName: userData.lastName,
            email: userData.email,
            password: userData.password,
            role: userData.role || 'worker',
            userType: userData.userType || 'WORKER'
        });
        
        const result = response.data;
        
        if (result.code === "0" && result.data) {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Registration failed');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Registration failed. Please try again.');
        }
    }
}

// Forgot password function - Connect to real backend API
export async function forgotPassword(identifier) {
    try {
        const response = await api.post('/auth/forgot-password', {
            identifier: identifier
        });
        
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to send reset token');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to send reset token');
        }
    }
}

// Reset password function - Connect to real backend API
export async function resetPassword(token, newPassword) {
    try {
        const response = await api.post('/auth/reset-password', {
            token: token,
            newPassword: newPassword
        });
        
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to reset password');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to reset password');
        }
    }
}

// Update user patientId - Connect to real backend API
export async function updateUserPatientId(userId, patientId) {
    try {
        const response = await api.put(`/auth/user/${userId}/patient`, {
            patientId: patientId
        });
        
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data
            };
        } else {
            throw new Error(result.msg || 'Failed to update user patientId');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update user patientId');
        }
    }
}

// Logout function - clears user session and redirects to login
export function logout() {
    try {
        console.log('Logging out user...');
        
        // Clear all session data
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('userId');
        sessionStorage.removeItem('inviteSubmitted');
        
        console.log('User session cleared successfully');
        
        // In a real app, you might also call a logout API endpoint
        // await api.post('/auth/logout');
        
        // Redirect to login page
        window.location.href = '/login';
        
    } catch (error) {
        console.error('Error during logout:', error);
        // Still redirect to login page even if there's an error
        window.location.href = '/login';
    }
}

// Update user profile
export async function updateUserProfile(userId, profileData) {
    try {
        const response = await api.put(`/auth/profile/${userId}`, profileData);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data,
                success: true
            };
        } else {
            throw new Error(result.msg || 'Failed to update profile');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update profile');
        }
    }
}

// Update user notification settings
export async function updateUserNotificationSettings(userId, notificationData) {
    try {
        const response = await api.put(`/auth/notifications/${userId}`, notificationData);
        const result = response.data;
        
        if (result.code === "0") {
            return {
                data: result.data,
                success: true
            };
        } else {
            throw new Error(result.msg || 'Failed to update notification settings');
        }
    } catch (error) {
        if (error.response?.data?.msg) {
            throw new Error(error.response.data.msg);
        } else if (error.message) {
            throw error;
        } else {
            throw new Error('Failed to update notification settings');
        }
    }
}
