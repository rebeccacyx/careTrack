package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.service.UserService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Resource
    private UserService userService;

    // based on uname
    @PostMapping("/login")
    public Result<User> loginController(@RequestBody User user) {
        User u = userService.loginService(user.getUname(), user.getPassword());
        if (u != null) {
            return Result.<User>success(u, "Login successful!");
        } else {
            return Result.<User>error("401", "Invalid username or password!");
        }
    }

    // based on email
    @PostMapping("/login-email")
    public Result<User> loginByEmailController(@RequestBody User user) {
        User u = userService.loginByEmailService(user.getEmail(), user.getPassword());
        if (u != null) {
            return Result.<User>success(u, "Login successful!");
        } else {
            return Result.<User>error("401", "Invalid email or password!");
        }
    }

    @PostMapping("/register")
    public Result<User> registerController(@RequestBody User newUser) {
        User user = userService.registerService(newUser);
        if (user != null) {
            return Result.<User>success(user, "Registration successful!");
        } else {
            return Result.<User>error("409", "Email already exists!");
        }
    }

    @GetMapping("/worker/{userId}/status")
    public Result<String> getWorkerStatus(@PathVariable String userId) {
        String status = userService.getWorkerStatus(userId);
        return Result.success(status, "Worker status retrieved!");
    }

    // Worker bind patient
    @PostMapping("/worker/{userId}/bind/{patientId}")
    public Result<String> bindWorkerToPatient(@PathVariable String userId,
                                              @PathVariable String patientId) {
        boolean success = userService.bindWorkerToPatient(userId, patientId);
        if (success) {
            return Result.success("Worker bound to patient successfully!");
        } else {
            return Result.<String>error("400", "Failed to bind worker to patient!");
        }
    }

    // Worker get bound patient
    @GetMapping("/worker/{userId}/patient")
    public Result<String> getWorkerPatient(@PathVariable String userId) {
        String patientId = userService.getWorkerPatient(userId);
        if (patientId != null) {
            return Result.success(patientId, "Worker patient retrieved successfully!");
        } else {
            return Result.<String>error("404", "Worker not bound to any patient!");
        }
    }

    // Worker bind to manager
    @PostMapping("/worker/{workerId}/bind-manager/{managerId}")
    public Result<String> bindWorkerToManager(@PathVariable String workerId,
                                               @PathVariable String managerId) {
        boolean success = userService.bindWorkerToManager(workerId, managerId);
        if (success) {
            return Result.success("Worker bound to manager successfully!");
        } else {
            return Result.<String>error("400", "Failed to bind worker to manager! Worker or manager not found, or invalid user types.");
        }
    }

    // Sync worker patientId from manager's patientId
    @PostMapping("/worker/{workerId}/sync-patient-from-manager")
    public Result<String> syncWorkerPatientFromManager(@PathVariable String workerId) {
        try {
            User worker = userService.getUserById(workerId);
            if (worker == null) {
                return Result.<String>error("404", "Worker not found!");
            }
            
            if (worker.getManagerId() == null || worker.getManagerId().trim().isEmpty()) {
                return Result.<String>error("400", "Worker is not bound to any manager!");
            }
            
            User manager = userService.getUserById(worker.getManagerId());
            if (manager == null) {
                return Result.<String>error("404", "Manager not found!");
            }
            
            String managerPatientId = manager.getPatientId();
            if (managerPatientId == null || managerPatientId.trim().isEmpty()) {
                return Result.<String>error("400", "Manager does not have a patientId assigned!");
            }
            
            boolean success = userService.bindWorkerToPatient(workerId, managerPatientId);
            if (success) {
                return Result.success("Worker patientId synced from manager successfully! PatientId: " + managerPatientId);
            } else {
                return Result.<String>error("400", "Failed to sync worker patientId from manager!");
            }
        } catch (Exception e) {
            return Result.<String>error("500", "Error syncing worker patientId: " + e.getMessage());
        }
    }

    // Change password with old password verification
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier"); // email or uname
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        boolean ok = userService.changePassword(identifier, oldPassword, newPassword);
        if (ok) {
            return Result.success(true, "Password updated successfully!");
        }
        return Result.<Boolean>error("400", "Invalid credentials or parameters");
    }

    // Forgot password: generate token and send email
    @PostMapping("/forgot-password")
    public Result<String> forgotPassword(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier"); // email or uname
        boolean success = userService.createPasswordResetTokenAndSendEmail(identifier);
        if (success) {
            return Result.success("Email sent", "Password reset email sent successfully!");
        }
        return Result.<String>error("404", "User not found or email sending failed");
    }

    // Reset password by token
    @PostMapping("/reset-password")
    public Result<Boolean> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        boolean ok = userService.resetPasswordByToken(token, newPassword);
        if (ok) {
            return Result.success(true, "Password reset successfully!");
        }
        return Result.<Boolean>error("400", "Invalid or expired token");
    }

    // Get password reset token for testing (temporary endpoint)
    @GetMapping("/test-token/{email}")
    public Result<String> getTestToken(@PathVariable String email) {
        User user = userService.getUserById(userService.findUserByEmail(email).getId());
        if (user != null && user.getPasswordResetToken() != null) {
            return Result.success(user.getPasswordResetToken(), "Token retrieved for testing");
        }
        return Result.<String>error("404", "No reset token found for user");
    }

    // Update user patientId
    @PutMapping("/user/{userId}/patient")
    public Result<Boolean> updateUserPatientId(@PathVariable String userId, @RequestBody Map<String, String> body) {
        String patientId = body.get("patientId");
        boolean success = userService.updateUserPatientId(userId, patientId);
        if (success) {
            return Result.success(true, "User patientId updated successfully!");
        } else {
            return Result.<Boolean>error("400", "Failed to update user patientId!");
        }
    }

    // Get invite status - Check if user has already used an invite code
    @GetMapping("/invite-status")
    public Result<Map<String, Object>> getInviteStatus(@RequestParam(required = false) String userId) {
        System.out.println("🔍 Backend - getInviteStatus called with userId: " + userId);
        
        if (userId == null || userId.trim().isEmpty()) {
            System.out.println("❌ Backend - No userId provided");
            return Result.error("400", "User ID is required!");
        }
        
        try {
            // Get user information
            User user = userService.getUserById(userId);
            if (user == null) {
                System.out.println("❌ Backend - User not found: " + userId);
                return Result.error("404", "User not found!");
            }
            
            System.out.println("🔍 Backend - User hasUsedInviteCode: " + user.isHasUsedInviteCode());
            
            Map<String, Object> inviteStatus;
            if (user.isHasUsedInviteCode()) {
                // User has already used an invite code
                System.out.println("✅ Backend - User has already used invite code, returning valid=true");
                inviteStatus = Map.of(
                    "valid", true,
                    "reason", "already_used"
                );
            } else {
                // User hasn't used any invite code yet
                System.out.println("📝 Backend - User hasn't used any invite code yet, returning valid=false");
                inviteStatus = Map.of(
                    "valid", false,
                    "reason", "missing"
                );
            }
            
            System.out.println("🔍 Backend - Returning invite status: " + inviteStatus);
            return Result.success(inviteStatus, "Invite status retrieved!");
        } catch (Exception e) {
            System.err.println("❌ Backend - Error getting invite status: " + e.getMessage());
            // Fallback to invalid status
            Map<String, Object> inviteStatus = Map.of(
                "valid", false,
                "reason", "error"
            );
            return Result.success(inviteStatus, "Invite status retrieved with fallback!");
        }
    }

    // Submit invite code - Mock implementation for frontend testing
    @PostMapping("/submit-invite-code")
    public Result<Map<String, Object>> submitInviteCode(@RequestBody Map<String, String> body) {
        String inviteCode = body.get("inviteCode");
        
        // Mock invite code validation
        if (inviteCode != null && !inviteCode.trim().isEmpty()) {
            // Define valid invite codes for testing
            String[] validCodes = {"123", "abc", "test", "valid", "code"};
            boolean isValid = false;
            for (String validCode : validCodes) {
                if (validCode.equalsIgnoreCase(inviteCode.trim())) {
                    isValid = true;
                    break;
                }
            }
            
            if (isValid) {
                Map<String, Object> result = Map.of(
                    "success", true,
                    "message", "Invite code accepted"
                );
                return Result.success(result, "Invite code validated successfully!");
            } else {
                return Result.error("400", "Invalid invite code. Please check and try again.");
            }
        } else {
            return Result.error("400", "Please enter an invite code");
        }
    }

    // Get current user information (for frontend)
    @GetMapping("/me")
    public Result<User> getMe(@RequestParam(required = false) String userId) {
        if (userId != null && !userId.trim().isEmpty()) {
            // Get user by ID
            User user = userService.getUserById(userId);
            if (user != null) {
                return Result.success(user, "User information retrieved successfully!");
            } else {
                return Result.error("404", "User not found!");
            }
        } else {
            // Return error if no userId provided
            return Result.error("400", "User ID is required!");
        }
    }

    // Logout endpoint
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                @RequestParam(required = false) String token) {
        try {
            // In a real implementation, you would:
            // 1. Validate the token from Authorization header or request parameter
            // 2. Invalidate the token on the server side (if using JWT blacklist)
            // 3. Clear any server-side session data
            // 4. Log the logout event for security purposes
            
            // For now, we'll just return a success message
            // TODO: Implement proper token invalidation and session cleanup
            
            return Result.success("Logout successful", "User logged out successfully!");
        } catch (Exception e) {
            return Result.error("500", "Logout failed: " + e.getMessage());
        }
    }

    // Update user profile
    @PutMapping("/profile/{userId}")
    public Result<String> updateUserProfile(@PathVariable String userId, @RequestBody Map<String, String> profileData) {
        try {
            String firstName = profileData.get("firstName");
            String middleName = profileData.get("middleName");
            String lastName = profileData.get("lastName");
            String email = profileData.get("email");
            
            boolean success = userService.updateUserProfile(userId, firstName, middleName, lastName, email);
            if (success) {
                return Result.success("Profile updated successfully", "User profile updated!");
            } else {
                return Result.error("400", "Failed to update profile!");
            }
        } catch (Exception e) {
            return Result.error("500", "Profile update failed: " + e.getMessage());
        }
    }

    // Update user notification settings
    @PutMapping("/notifications/{userId}")
    public Result<String> updateUserNotificationSettings(@PathVariable String userId, @RequestBody Map<String, Object> notificationData) {
        try {
            boolean taskReminders = (Boolean) notificationData.getOrDefault("taskReminders", true);
            boolean approvalNotifications = (Boolean) notificationData.getOrDefault("approvalNotifications", true);
            boolean budgetWarning = (Boolean) notificationData.getOrDefault("budgetWarning", true);
            boolean emailNotifications = (Boolean) notificationData.getOrDefault("emailNotifications", true);
            
            boolean success = userService.updateUserNotificationSettings(userId, taskReminders, approvalNotifications, budgetWarning, emailNotifications);
            if (success) {
                return Result.success("Notification settings updated successfully", "Notification settings updated!");
            } else {
                return Result.error("400", "Failed to update notification settings!");
            }
        } catch (Exception e) {
            return Result.error("500", "Notification settings update failed: " + e.getMessage());
        }
    }

    // Update manager shift time settings
    @PutMapping("/shift-times/{userId}")
    public Result<String> updateShiftTimeSettings(@PathVariable String userId, @RequestBody Map<String, String> shiftTimeData) {
        try {
            String morningStart = shiftTimeData.get("morningShiftStart");
            String morningEnd = shiftTimeData.get("morningShiftEnd");
            String afternoonStart = shiftTimeData.get("afternoonShiftStart");
            String afternoonEnd = shiftTimeData.get("afternoonShiftEnd");
            String eveningStart = shiftTimeData.get("eveningShiftStart");
            String eveningEnd = shiftTimeData.get("eveningShiftEnd");
            
            boolean success = userService.updateShiftTimeSettings(userId, morningStart, morningEnd, afternoonStart, afternoonEnd, eveningStart, eveningEnd);
            if (success) {
                return Result.success("Shift time settings updated successfully", "Shift time settings updated!");
            } else {
                return Result.error("400", "Failed to update shift time settings! User not found or invalid data.");
            }
        } catch (Exception e) {
            return Result.error("500", "Shift time settings update failed: " + e.getMessage());
        }
    }
    
    // Get users by organization ID and user type (for communication)
    @GetMapping("/organization/{organizationId}/userType/{userType}")
    public Result<User> getUserByOrganizationAndType(@PathVariable String organizationId, @PathVariable String userType) {
        try {
            User user = userService.getUserByOrganizationAndType(organizationId, userType);
            if (user != null) {
                return Result.success(user, "User retrieved successfully!");
            } else {
                return Result.error("404", "User not found!");
            }
        } catch (Exception e) {
            return Result.error("500", "Failed to retrieve user: " + e.getMessage());
        }
    }

}