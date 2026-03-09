package com.careapp.service;

import com.careapp.domain.User;

public interface UserService {
    // Login services
    User loginService(String uname, String password);
    User loginByEmailService(String email, String password);

    // Registration service
    User registerService(User user);
    // worker bind and status services
    String getWorkerStatus(String userId);
    boolean bindWorkerToPatient(String userId, String patientId);
    String getWorkerPatient(String userId);
    
    // manager bind service
    boolean bindManagerToPatient(String managerId, String patientId);
    
    // worker-manager binding service
    boolean bindWorkerToManager(String workerId, String managerId);

    // Change password with old password verification
    boolean changePassword(String identifier, String oldPassword, String newPassword);

    // Forgot password: generate token and set expiry, return token
    String createPasswordResetToken(String identifier);
    
    // Forgot password: generate token, send email, return success status
    boolean createPasswordResetTokenAndSendEmail(String identifier);

    // Reset password by token
    boolean resetPasswordByToken(String token, String newPassword);

    // Update user patientId
    boolean updateUserPatientId(String userId, String patientId);

    // Get user by ID
    User getUserById(String userId);
    
    // Find user by email
    User findUserByEmail(String email);
    
    // Mark user as having used invite code
    boolean markUserAsUsedInviteCode(String userId);
    
    // Update user profile
    boolean updateUserProfile(String userId, String firstName, String middleName, String lastName, String email);
    
    // Update user notification settings
    boolean updateUserNotificationSettings(String userId, boolean taskReminders, boolean approvalNotifications, boolean budgetWarning, boolean emailNotifications);
    
    // Update manager shift time settings
    boolean updateShiftTimeSettings(String userId, String morningStart, String morningEnd, String afternoonStart, String afternoonEnd, String eveningStart, String eveningEnd);
    
    // Get user by organization ID and user type
    User getUserByOrganizationAndType(String organizationId, String userType);

}
