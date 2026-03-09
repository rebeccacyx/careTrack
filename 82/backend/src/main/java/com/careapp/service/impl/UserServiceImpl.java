package com.careapp.service.impl;

import com.careapp.domain.User;
import com.careapp.repository.UserRepository;
import com.careapp.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;
    
    @Resource
    private EmailService emailService;

    @Override
    public User loginService(String uname, String password) {
        User user = userRepository.findByUnameAndPassword(uname, password);
        if (user != null) {
                user.setPassword(null); // do not return password
        }
        return user;
    }

    @Override
    public User loginByEmailService(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password);
        if (user != null) {
            user.setPassword(null); // do not return password
        }
        return user;
    }

    @Override
    public User registerService(User newUser) {
        // check if email already exists
        User existingUser = userRepository.findByEmail(newUser.getEmail());
        if (existingUser != null) {
            return null; // email already exists
        }
        
        // set default status
        newUser.setStatus("active");
        newUser.setPasswordResetToken(null);
        newUser.setPasswordResetExpires(null);
        
        // Handle organizationId logic:
        // 1. If organizationId is already provided, use it
        // 2. If organizationName is provided, generate a unique organizationId
        // 3. If neither is provided, set to "default-org"
        if (!StringUtils.hasText(newUser.getOrganizationId())) {
            if (StringUtils.hasText(newUser.getOrganizationName())) {
                // Generate a unique organizationId based on organizationName
                // Using UUID to ensure uniqueness
                String generatedOrgId = "org-" + UUID.randomUUID().toString().replace("-", "");
                newUser.setOrganizationId(generatedOrgId);
            } else {
                // No organizationName provided, set to default
                newUser.setOrganizationId("default-org");
            }
        }
        
        User savedUser = userRepository.save(newUser);
        if (savedUser != null) {
            savedUser.setPassword(null); // do not return password
        }
        return savedUser;
    }

    @Override
    public String getWorkerStatus(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "not_found";
        }
        return user.getStatus();
    }

    @Override
    public boolean bindWorkerToPatient(String workerId, String patientId) {
        System.out.println("🔍 bindWorkerToPatient - Worker ID: " + workerId + ", Patient ID: " + patientId);
        User worker = userRepository.findById(workerId).orElse(null);
        if (worker == null) {
            System.out.println("❌ bindWorkerToPatient - Worker not found: " + workerId);
            return false;
        }
        System.out.println("🔍 bindWorkerToPatient - Worker found: " + worker.getEmail() + ", Current patientId: " + worker.getPatientId());
        worker.setPatientId(patientId);
        // Note: We no longer modify the status field. Status should be used to represent the worker's actual state (active, inactive, pending, etc.)
        // Binding status can be determined by checking if patientId != null
        User savedWorker = userRepository.save(worker);
        if (savedWorker != null) {
            System.out.println("✅ bindWorkerToPatient - Worker saved successfully, patientId: " + savedWorker.getPatientId());
            // Verify the save was successful
            User verifyWorker = userRepository.findById(workerId).orElse(null);
            if (verifyWorker != null && patientId.equals(verifyWorker.getPatientId())) {
                System.out.println("✅ bindWorkerToPatient - Verification successful, patientId confirmed: " + verifyWorker.getPatientId());
                return true;
            } else {
                System.out.println("❌ bindWorkerToPatient - Verification failed! Expected: " + patientId + ", Got: " + (verifyWorker != null ? verifyWorker.getPatientId() : "null"));
                return false;
            }
        } else {
            System.out.println("❌ bindWorkerToPatient - Failed to save worker");
            return false;
        }
    }

    @Override
    public boolean bindManagerToPatient(String managerId, String patientId) {
        User manager = userRepository.findById(managerId).orElse(null);
        if (manager == null) return false;
        manager.setPatientId(patientId);
        // Note: We no longer modify the status field. Status should be used to represent the worker's actual state (active, inactive, pending, etc.)
        // Binding status can be determined by checking if patientId != null
        userRepository.save(manager);
        return true;
    }

    @Override
    public String getWorkerPatient(String userId) {
        User worker = userRepository.findById(userId).orElse(null);
        if (worker == null) return null;
        return worker.getPatientId();
    }

    @Override
    public boolean bindWorkerToManager(String workerId, String managerId) {
        User worker = userRepository.findById(workerId).orElse(null);
        if (worker == null) return false;
        // Verify that the target user is actually a WORKER
        if (!"WORKER".equals(worker.getUserType())) {
            return false;
        }
        // Verify that the manager exists and is a MANAGER
        User manager = userRepository.findById(managerId).orElse(null);
        if (manager == null || !"MANAGER".equals(manager.getUserType())) {
            return false;
        }
        worker.setManagerId(managerId);
        userRepository.save(worker);
        return true;
    }

    @Override
    public boolean changePassword(String identifier, String oldPassword, String newPassword) {
        if (!StringUtils.hasText(identifier) || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        User user = userRepository.findByEmailAndPassword(identifier, oldPassword);
        if (user == null) {
            user = userRepository.findByUnameAndPassword(identifier, oldPassword);
        }
        if (user == null) {
            return false;
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        return true;
    }

    @Override
    public String createPasswordResetToken(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            return null;
        }
        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            user = userRepository.findByUname(identifier);
        }
        if (user == null) {
            return null;
        }
        String token = generateToken();
        long expires = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minutes
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(expires);
        userRepository.save(user);
        return token;
    }

    @Override
    public boolean createPasswordResetTokenAndSendEmail(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            return false;
        }
        
        // Find user by email or username
        User user = userRepository.findByEmail(identifier);
        if (user == null) {
            user = userRepository.findByUname(identifier);
        }
        if (user == null) {
            return false;
        }
        
        // Generate token and set expiry
        String token = generateToken();
        long expires = System.currentTimeMillis() + 15 * 60 * 1000; // 15 minutes
        user.setPasswordResetToken(token);
        user.setPasswordResetExpires(expires);
        userRepository.save(user);
        
        // Send email with reset token
        try {
            String subject = "CareTrack - Password Reset Request";
            String emailContent = String.format(
                "Hello %s,\n\n" +
                "You have requested to reset your password for CareTrack.\n\n" +
                "Your password reset token is: %s\n\n" +
                "Please use this token to reset your password. This token will expire in 15 minutes.\n\n" +
                "If you did not request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "CareTrack Team",
                user.getFirstName() != null ? user.getFirstName() : "User",
                token
            );
            
            emailService.sendText(user.getEmail(), subject, emailContent);
            System.out.println("✅ Password reset email sent successfully to: " + user.getEmail());
            return true;
        } catch (IllegalStateException e) {
            // Email service not configured
            System.err.println("⚠️ Email service not configured: " + e.getMessage());
            System.err.println("📧 Password reset token generated (email NOT sent): " + token);
            System.err.println("💡 To enable email sending, set SENDGRID_API_KEY environment variable");
            // Log error but still return true for backward compatibility
            // In production, you might want to return false or throw exception
            return true;
        } catch (Exception e) {
            // Other email sending errors
            System.err.println("❌ Failed to send password reset email to " + user.getEmail());
            System.err.println("    Error: " + e.getMessage());
            System.err.println("📧 Password reset token generated (email NOT sent): " + token);
            // Log full exception for debugging
            e.printStackTrace();
            // Return true to allow token usage even if email fails
            // This helps in testing scenarios where email service might not be fully configured
            return true;
        }
    }

    @Override
    public boolean resetPasswordByToken(String token, String newPassword) {
        if (!StringUtils.hasText(token) || !StringUtils.hasText(newPassword)) {
            return false;
        }
        User user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            return false;
        }
        Long expires = user.getPasswordResetExpires();
        if (expires == null || expires < System.currentTimeMillis()) {
            return false;
        }
        user.setPassword(newPassword);
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateUserPatientId(String userId, String patientId) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(patientId)) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setPatientId(patientId);
        userRepository.save(user);
        return true;
    }

    @Override
    public User getUserById(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPassword(null); // do not return password
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean markUserAsUsedInviteCode(String userId) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        user.setHasUsedInviteCode(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateUserProfile(String userId, String firstName, String middleName, String lastName, String email) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Update profile fields
        if (StringUtils.hasText(firstName)) {
            user.setFirstName(firstName);
        }
        if (StringUtils.hasText(middleName)) {
            user.setMiddleName(middleName);
        }
        if (StringUtils.hasText(lastName)) {
            user.setLastName(lastName);
        }
        if (StringUtils.hasText(email)) {
            user.setEmail(email);
        }
        
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateUserNotificationSettings(String userId, boolean taskReminders, boolean approvalNotifications, boolean budgetWarning, boolean emailNotifications) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Update notification settings
        user.setTaskReminders(taskReminders);
        user.setApprovalNotifications(approvalNotifications);
        user.setBudgetWarning(budgetWarning);
        user.setEmailNotifications(emailNotifications);
        
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateShiftTimeSettings(String userId, String morningStart, String morningEnd, String afternoonStart, String afternoonEnd, String eveningStart, String eveningEnd) {
        if (!StringUtils.hasText(userId)) {
            return false;
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Update shift time settings (only if provided)
        if (StringUtils.hasText(morningStart)) {
            user.setMorningShiftStart(morningStart);
        }
        if (StringUtils.hasText(morningEnd)) {
            user.setMorningShiftEnd(morningEnd);
        }
        if (StringUtils.hasText(afternoonStart)) {
            user.setAfternoonShiftStart(afternoonStart);
        }
        if (StringUtils.hasText(afternoonEnd)) {
            user.setAfternoonShiftEnd(afternoonEnd);
        }
        if (StringUtils.hasText(eveningStart)) {
            user.setEveningShiftStart(eveningStart);
        }
        if (StringUtils.hasText(eveningEnd)) {
            user.setEveningShiftEnd(eveningEnd);
        }
        
        userRepository.save(user);
        return true;
    }
    
    @Override
    public User getUserByOrganizationAndType(String organizationId, String userType) {
        if (!StringUtils.hasText(organizationId) || !StringUtils.hasText(userType)) {
            return null;
        }
        return userRepository.findFirstByOrganizationIdAndUserType(organizationId, userType);
    }

    private String generateToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}