package com.careapp.service.impl;

import com.careapp.domain.InviteCode;
import com.careapp.domain.User;
import com.careapp.domain.Worker;
import com.careapp.repository.InviteCodeRepository;
import com.careapp.service.InviteCodeService;
import com.careapp.service.UserService;
import com.careapp.service.WorkerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class InviteCodeServiceImpl implements InviteCodeService {

    @Resource
    private InviteCodeRepository inviteCodeRepository;
    
    @Resource
    private UserService userService;
    
    @Resource
    private WorkerService workerService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    @Override
    public String generateInviteCode(String createdBy, String createdByType, String targetType, String patientId, String organizationId) {
        // Generate unique code
        String code = generateUniqueCode();
        
        // Create invite code
        InviteCode inviteCode = new InviteCode();
        inviteCode.setCode(code);
        inviteCode.setCreatedBy(createdBy);
        inviteCode.setCreatedByType(createdByType);
        inviteCode.setTargetType(targetType);
        inviteCode.setPatientId(patientId);
        inviteCode.setOrganizationId(organizationId);
        inviteCode.setCreatedAt(LocalDateTime.now());
        inviteCode.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days expiry
        inviteCode.setUsed(false);
        
        inviteCodeRepository.save(inviteCode);
        return code;
    }

    @Override
    public boolean validateInviteCode(String code) {
        System.out.println("🔍 Backend - validateInviteCode called with code: " + code);
        
        InviteCode inviteCode = inviteCodeRepository.findByCode(code);
        if (inviteCode == null) {
            System.out.println("❌ Backend - Invite code not found: " + code);
            return false;
        }
        
        System.out.println("🔍 Backend - Found invite code:");
        System.out.println("  - Code: " + inviteCode.getCode());
        System.out.println("  - Created: " + inviteCode.getCreatedAt());
        System.out.println("  - Expires: " + inviteCode.getExpiresAt());
        System.out.println("  - Is Used: " + inviteCode.isUsed());
        System.out.println("  - Current Time: " + LocalDateTime.now());
        System.out.println("  - Is Expired: " + inviteCode.getExpiresAt().isBefore(LocalDateTime.now()));
        
        // Only check if code is expired, not if it's used
        // The same invite code can be used by multiple users within the expiration period
        boolean isValid = inviteCode.getExpiresAt().isAfter(LocalDateTime.now());
        System.out.println("🔍 Backend - Invite code validation result: " + isValid);
        
        return isValid;
    }

    @Override
    public boolean useInviteCode(String code, String usedBy) {
        System.out.println("🔍 Backend - useInviteCode called with code: " + code + ", usedBy: " + usedBy);
        
        InviteCode inviteCode = inviteCodeRepository.findByCode(code);
        if (inviteCode == null) {
            System.out.println("❌ Backend - Invite code not found: " + code);
            return false;
        }
        
        if (inviteCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            System.out.println("❌ Backend - Invite code expired: " + code);
            return false;
        }
        
        System.out.println("✅ Backend - Invite code is valid, proceeding to use it");
        
        // Don't mark the invite code as used since it can be used by multiple users
        // Just mark the user as having used an invite code
        
        // Mark user as having used an invite code
        try {
            boolean markSuccess = userService.markUserAsUsedInviteCode(usedBy);
            if (markSuccess) {
                System.out.println("✅ Successfully marked user " + usedBy + " as having used invite code");
            } else {
                System.out.println("❌ Failed to mark user " + usedBy + " as having used invite code");
            }
        } catch (Exception e) {
            System.err.println("❌ Error marking user as having used invite code: " + e.getMessage());
            // Don't fail the invite code usage if marking fails
        }
        
        // If this is a MANAGER invite code, bind the manager to the patient
        if ("MANAGER".equals(inviteCode.getTargetType()) && inviteCode.getPatientId() != null) {
            try {
                // Bind the manager to the patient
                boolean bindSuccess = userService.bindManagerToPatient(usedBy, inviteCode.getPatientId());
                if (bindSuccess) {
                    System.out.println("✅ Successfully bound manager " + usedBy + " to patient " + inviteCode.getPatientId());
                    System.out.println("🔍 Manager can now access client information for patient: " + inviteCode.getPatientId());
                } else {
                    System.out.println("❌ Failed to bind manager " + usedBy + " to patient " + inviteCode.getPatientId());
                }
            } catch (Exception e) {
                System.err.println("❌ Error binding manager to patient: " + e.getMessage());
                // Don't fail the invite code usage if binding fails
            }
        }
        
        // If this is a WORKER invite code, bind the worker to the manager and patient
        if ("WORKER".equals(inviteCode.getTargetType())) {
            try {
                // Get the manager who created this token (createdBy)
                String managerId = inviteCode.getCreatedBy();
                
                System.out.println("🔍 Worker token - Manager ID: " + managerId);
                
                if (managerId != null) {
                    // Bind worker to manager using UserService (for User entity)
                    boolean bindManagerSuccess = userService.bindWorkerToManager(usedBy, managerId);
                    if (bindManagerSuccess) {
                        System.out.println("✅ Successfully bound worker " + usedBy + " to manager " + managerId + " in User entity");
                        
                        // Also bind in Worker entity (for Worker API queries)
                        try {
                            // First check if Worker entity exists
                            Optional<Worker> existingWorker = workerService.getWorkerById(usedBy);
                            Worker worker;
                            
                            if (existingWorker.isPresent()) {
                                // Worker exists, just bind manager
                                worker = workerService.bindWorkerToManager(usedBy, managerId);
                                System.out.println("✅ Successfully bound existing worker " + usedBy + " to manager " + managerId + " in Worker entity");
                            } else {
                                // Worker doesn't exist, create it from User entity
                                System.out.println("⚠️ Worker entity not found for ID: " + usedBy + ", creating from User entity...");
                                User user = userService.getUserById(usedBy);
                                if (user != null) {
                                    // Create Worker entity from User info
                                    Worker newWorker = new Worker();
                                    newWorker.setId(usedBy); // Use same ID as User
                                    newWorker.setName(user.getFirstName() + " " + (user.getLastName() != null ? user.getLastName() : ""));
                                    newWorker.setEmail(user.getEmail());
                                    newWorker.setOrganizationId(inviteCode.getOrganizationId() != null ? inviteCode.getOrganizationId() : user.getOrganizationId());
                                    newWorker.setManagerId(managerId); // Set manager directly
                                    newWorker.setStatus("active"); // Set status to active
                                    
                                    // Generate workerId if not exists (W001, W002, etc.)
                                    if (newWorker.getWorkerId() == null) {
                                        // Try to find a unique worker ID
                                        String workerIdPrefix = "W";
                                        int workerNumber = 1;
                                        String workerId = workerIdPrefix + String.format("%03d", workerNumber);
                                        while (workerService.getWorkerByWorkerId(workerId).isPresent()) {
                                            workerNumber++;
                                            workerId = workerIdPrefix + String.format("%03d", workerNumber);
                                        }
                                        newWorker.setWorkerId(workerId);
                                    }
                                    
                                    worker = workerService.createWorker(newWorker);
                                    System.out.println("✅ Created new Worker entity for " + usedBy + " and bound to manager " + managerId);
                                } else {
                                    System.out.println("❌ User entity not found for ID: " + usedBy);
                                    worker = null;
                                }
                            }
                            
                            if (worker != null) {
                                System.out.println("✅ Successfully bound worker " + usedBy + " to manager " + managerId + " in Worker entity");
                            }
                        } catch (Exception e) {
                            System.err.println("⚠️ Failed to bind worker in Worker entity (User entity binding succeeded): " + e.getMessage());
                            e.printStackTrace();
                            // Don't fail if Worker entity creation fails
                        }
                    } else {
                        System.out.println("❌ Failed to bind worker " + usedBy + " to manager " + managerId);
                    }
                    
                    // Get manager's patientId from database
                    User manager = userService.getUserById(managerId);
                    if (manager != null) {
                        String managerPatientId = manager.getPatientId();
                        System.out.println("🔍 Manager's patientId from database: " + managerPatientId);
                        
                        if (managerPatientId != null && !managerPatientId.trim().isEmpty()) {
                            // Bind worker to patient using manager's patientId
                            boolean bindPatientSuccess = userService.bindWorkerToPatient(usedBy, managerPatientId);
                            if (bindPatientSuccess) {
                                System.out.println("✅ Successfully bound worker " + usedBy + " to patient " + managerPatientId + " (from manager)");
                                System.out.println("🔍 Worker can now access client information for patient: " + managerPatientId);
                            } else {
                                System.out.println("❌ Failed to bind worker " + usedBy + " to patient " + managerPatientId);
                            }
                        } else {
                            System.out.println("⚠️ Manager " + managerId + " does not have a patientId assigned");
                        }
                    } else {
                        System.out.println("❌ Manager " + managerId + " not found in database");
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Error binding worker to manager/patient: " + e.getMessage());
                e.printStackTrace();
                // Don't fail the invite code usage if binding fails
            }
        }
        
        return true;
    }

    @Override
    public List<InviteCode> getInviteCodesByCreator(String creatorId) {
        return inviteCodeRepository.findByCreatedBy(creatorId);
    }

    @Override
    public boolean revokeInviteCode(String codeId) {
        try {
            inviteCodeRepository.deleteById(codeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<InviteCode> getActiveInviteCodesForPatient(String patientId) {
        return inviteCodeRepository.findByPatientIdAndIsUsedFalseAndExpiresAtAfter(patientId, LocalDateTime.now());
    }

    @Override
    public List<InviteCode> getInviteCodesUsedByUser(String userId) {
        return inviteCodeRepository.findByUsedBy(userId);
    }

    @Override
    public void cleanupExpiredInviteCodes() {
        List<InviteCode> expiredCodes = inviteCodeRepository.findByExpiresAtBeforeAndIsUsedFalse(LocalDateTime.now());
        inviteCodeRepository.deleteAll(expiredCodes);
    }

    private String generateUniqueCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        do {
            code.setLength(0);
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
        } while (inviteCodeRepository.findByCode(code.toString()) != null);
        
        return code.toString();
    }
}
