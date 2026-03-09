package com.careapp.service.impl;

import com.careapp.domain.Authorization;
import com.careapp.domain.User;
import com.careapp.repository.AuthorizationRepository;
import com.careapp.repository.UserRepository;
import com.careapp.service.BudgetPermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of BudgetPermissionService
 * Handles all budget-related permission checks and access control
 */
@Service
public class BudgetPermissionServiceImpl implements BudgetPermissionService {

    @Resource
    private UserRepository userRepository;
    
    @Resource
    private AuthorizationRepository authorizationRepository;

    @Override
    public boolean canViewBudget(String userId, String patientId) {
        // Get user information
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        // Check user role and permissions
        String userType = user.getUserType();
        String userPatientId = user.getPatientId();
        String organizationId = user.getOrganizationId();
        
        // POA and Family Members can always view their patient's budget
        if (("POA".equals(userType) || "FM".equals(userType)) && patientId.equals(userPatientId)) {
            return true;
        }
        
        // Admin can view all budgets
        if ("Admin".equals(user.getRole())) {
            return true;
        }
        
        // Manager and Worker can view budgets with explicit authorization
        if ("MANAGER".equals(userType) || "WORKER".equals(userType)) {
            return hasActiveAuthorization(userId, patientId, organizationId);
        }
        
        return false;
    }

    @Override
    public boolean canCreateBudget(String userId, String patientId) {
        // Get user information
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        String userType = user.getUserType();
        String organizationId = user.getOrganizationId();
        
        // Only Manager can create budgets (with proper authorization)
        if ("MANAGER".equals(userType)) {
            return hasActiveAuthorization(userId, patientId, organizationId);
        }
        
        // Admin can create budgets for any patient
        if ("Admin".equals(user.getRole())) {
            return true;
        }
        
        return false;
    }

    @Override
    public boolean canUpdateBudget(String userId, String patientId) {
        // Same permissions as create budget
        return canCreateBudget(userId, patientId);
    }

    @Override
    public boolean canDeleteBudget(String userId, String patientId) {
        // Only Manager and Admin can delete budgets
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        String userType = user.getUserType();
        String organizationId = user.getOrganizationId();
        
        // Manager can delete budgets (with proper authorization)
        if ("MANAGER".equals(userType)) {
            return hasActiveAuthorization(userId, patientId, organizationId);
        }
        
        // Admin can delete any budget
        if ("Admin".equals(user.getRole())) {
            return true;
        }
        
        return false;
    }

    @Override
    public boolean canManageBudgetItems(String userId, String patientId) {
        // Same permissions as update budget
        return canUpdateBudget(userId, patientId);
    }

    @Override
    public boolean canUpdateUsage(String userId, String patientId) {
        // Get user information
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        String userType = user.getUserType();
        String organizationId = user.getOrganizationId();
        
        // Only Manager can update usage (with proper authorization)
        if ("MANAGER".equals(userType)) {
            return hasActiveAuthorization(userId, patientId, organizationId);
        }
        
        // Admin can update usage for any patient
        if ("Admin".equals(user.getRole())) {
            return true;
        }
        
        return false;
    }

    @Override
    public List<String> getAccessiblePatients(String userId) {
        List<String> accessiblePatients = new ArrayList<>();
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return accessiblePatients;
        }
        
        String userType = user.getUserType();
        String userPatientId = user.getPatientId();
        
        // POA and Family Members can access their own patient
        if (("POA".equals(userType) || "FM".equals(userType)) && userPatientId != null) {
            accessiblePatients.add(userPatientId);
        }
        
        // Admin can access all patients (we'll need to implement this based on your patient repository)
        if ("Admin".equals(user.getRole())) {
            // For now, return empty list - you might want to implement getAllPatientIds() method
            // accessiblePatients.addAll(patientRepository.getAllPatientIds());
        }
        
        // Manager and Worker can access patients they're authorized for
        if ("MANAGER".equals(userType) || "WORKER".equals(userType)) {
            List<Authorization> authorizations = authorizationRepository.findByAuthorizedToAndIsActiveTrue(userId);
            for (Authorization auth : authorizations) {
                if (!accessiblePatients.contains(auth.getPatientId())) {
                    accessiblePatients.add(auth.getPatientId());
                }
            }
        }
        
        return accessiblePatients;
    }

    @Override
    public boolean isBudgetAdministrator(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        
        String userType = user.getUserType();
        String role = user.getRole();
        
        // Only Manager and Admin are budget administrators
        return "MANAGER".equals(userType) || "Admin".equals(role);
    }

    /**
     * Check if user has active authorization for a patient
     * @param userId User ID
     * @param patientId Patient ID
     * @param organizationId Organization ID
     * @return true if user has active authorization
     */
    private boolean hasActiveAuthorization(String userId, String patientId, String organizationId) {
        List<Authorization> authorizations = authorizationRepository.findByAuthorizedToAndPatientIdAndOrganizationIdAndIsActiveTrue(
            userId, patientId, organizationId);
        return !authorizations.isEmpty();
    }
}
