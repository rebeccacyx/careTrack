package com.careapp.service.impl;

import com.careapp.domain.Authorization;
import com.careapp.repository.AuthorizationRepository;
import com.careapp.service.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Resource
    private AuthorizationRepository authorizationRepository;

    @Override
    public boolean grantAccess(String patientId, String authorizedBy, String authorizedTo, String authorizationType, String organizationId) {
        // Check if authorization already exists
        Authorization existingAuth = authorizationRepository.findByPatientIdAndAuthorizedToAndIsActiveTrue(patientId, authorizedTo);
        if (existingAuth != null) {
            return true; // Already authorized
        }
        
        // Create new authorization
        Authorization authorization = new Authorization();
        authorization.setPatientId(patientId);
        authorization.setAuthorizedBy(authorizedBy);
        authorization.setAuthorizedTo(authorizedTo);
        authorization.setAuthorizationType(authorizationType);
        authorization.setOrganizationId(organizationId);
        authorization.setGrantedAt(LocalDateTime.now());
        authorization.setActive(true);
        
        authorizationRepository.save(authorization);
        return true;
    }

    @Override
    public boolean revokeAccess(String patientId, String authorizedBy, String authorizedTo) {
        Authorization authorization = authorizationRepository.findByPatientIdAndAuthorizedToAndIsActiveTrue(patientId, authorizedTo);
        if (authorization == null) {
            return false;
        }
        
        // Check if the revoker is the original authorizer
        if (!authorization.getAuthorizedBy().equals(authorizedBy)) {
            return false;
        }
        
        // Revoke the authorization
        authorization.setActive(false);
        authorization.setRevokedAt(LocalDateTime.now());
        authorizationRepository.save(authorization);
        
        // If revoking a Manager, also revoke all Workers under this Manager
        if ("MANAGER".equals(authorization.getAuthorizationType())) {
            revokeAllWorkerAccessUnderManager(authorizedTo, patientId);
        }
        
        return true;
    }

    @Override
    public boolean revokeAllAccessForUser(String userId) {
        List<Authorization> userAuthorizations = authorizationRepository.findByAuthorizedToAndIsActiveTrue(userId);
        
        for (Authorization auth : userAuthorizations) {
            auth.setActive(false);
            auth.setRevokedAt(LocalDateTime.now());
            authorizationRepository.save(auth);
            
            // If revoking a Manager, also revoke all Workers under this Manager
            if ("MANAGER".equals(auth.getAuthorizationType())) {
                revokeAllWorkerAccessUnderManager(userId, auth.getPatientId());
            }
        }
        
        return true;
    }

    @Override
    public List<Authorization> getAuthorizationsByPatient(String patientId) {
        return authorizationRepository.findByPatientId(patientId);
    }

    @Override
    public List<Authorization> getAuthorizationsByUser(String userId) {
        return authorizationRepository.findByAuthorizedTo(userId);
    }

    @Override
    public boolean hasAccess(String userId, String patientId) {
        Authorization authorization = authorizationRepository.findByPatientIdAndAuthorizedToAndIsActiveTrue(patientId, userId);
        return authorization != null;
    }

    @Override
    public List<String> getAccessiblePatientIds(String userId) {
        List<Authorization> authorizations = authorizationRepository.findByAuthorizedToAndIsActiveTrue(userId);
        return authorizations.stream()
                .map(Authorization::getPatientId)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean revokeAllAccessByAuthorizer(String authorizerId) {
        List<Authorization> authorizations = authorizationRepository.findByAuthorizedByAndIsActiveTrue(authorizerId);
        
        for (Authorization auth : authorizations) {
            auth.setActive(false);
            auth.setRevokedAt(LocalDateTime.now());
            authorizationRepository.save(auth);
            
            // If revoking a Manager, also revoke all Workers under this Manager
            if ("MANAGER".equals(auth.getAuthorizationType())) {
                revokeAllWorkerAccessUnderManager(auth.getAuthorizedTo(), auth.getPatientId());
            }
        }
        
        return true;
    }

    private void revokeAllWorkerAccessUnderManager(String managerId, String patientId) {
        // Find all Workers under this Manager for this patient
        List<Authorization> workerAuthorizations = authorizationRepository.findByPatientIdAndIsActiveTrue(patientId)
                .stream()
                .filter(auth -> "WORKER".equals(auth.getAuthorizationType()))
                .filter(auth -> isWorkerUnderManager(auth.getAuthorizedTo(), managerId))
                .toList();
        
        // Revoke all worker access
        for (Authorization auth : workerAuthorizations) {
            auth.setActive(false);
            auth.setRevokedAt(LocalDateTime.now());
            authorizationRepository.save(auth);
        }
    }

    private boolean isWorkerUnderManager(String workerId, String managerId) {
        // This is a simplified check - in a real system, you might have a separate table
        // to track manager-worker relationships
        // For now, we'll assume workers are under managers in the same organization
        Authorization workerAuth = authorizationRepository.findByAuthorizedToAndIsActiveTrue(workerId)
                .stream()
                .filter(auth -> "WORKER".equals(auth.getAuthorizationType()))
                .findFirst()
                .orElse(null);
        
        if (workerAuth == null) return false;
        
        Authorization managerAuth = authorizationRepository.findByAuthorizedToAndIsActiveTrue(managerId)
                .stream()
                .filter(auth -> "MANAGER".equals(auth.getAuthorizationType()))
                .findFirst()
                .orElse(null);
        
        if (managerAuth == null) return false;
        
        // Check if they're in the same organization
        return workerAuth.getOrganizationId().equals(managerAuth.getOrganizationId());
    }

    @Override
    public boolean revokeAllAccessForOrganization(String organizationId) {
        if (!StringUtils.hasText(organizationId)) {
            return false;
        }
        
        // Find all authorizations for users in this organization
        List<Authorization> organizationAuthorizations = authorizationRepository.findAll()
                .stream()
                .filter(auth -> auth.isActive())
                .filter(auth -> organizationId.equals(auth.getOrganizationId()))
                .toList();
        
        // Revoke all authorizations for this organization
        for (Authorization auth : organizationAuthorizations) {
            auth.setActive(false);
            auth.setRevokedAt(LocalDateTime.now());
            authorizationRepository.save(auth);
        }
        
        return true;
    }
    
    public List<String> getAccessibleOrganizations(String familyId) {
        List<Authorization> authorizations = authorizationRepository.findByAuthorizedTo(familyId);
        return authorizations.stream()
                .map(Authorization::getOrganizationId)
                .distinct()
                .toList();
    }

}
