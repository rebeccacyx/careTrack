package com.careapp.service;

import com.careapp.domain.Authorization;

import java.util.List;

public interface AuthorizationService {
    // Grant access to patient data
    boolean grantAccess(String patientId, String authorizedBy, String authorizedTo, String authorizationType, String organizationId);
    
    // Revoke access to patient data
    boolean revokeAccess(String patientId, String authorizedBy, String authorizedTo);
    
    // Revoke all access for a user (when Manager is revoked, all Workers under them lose access)
    boolean revokeAllAccessForUser(String userId);
    
    // Get authorizations by patient
    List<Authorization> getAuthorizationsByPatient(String patientId);
    
    // Get authorizations by user
    List<Authorization> getAuthorizationsByUser(String userId);
    
    // Check if user has access to patient
    boolean hasAccess(String userId, String patientId);
    
    // Get all patients user has access to
    List<String> getAccessiblePatientIds(String userId);
    
    // Revoke all access by authorizer
    boolean revokeAllAccessByAuthorizer(String authorizerId);
    
    // Revoke all access for organization (revoke all users/workers in the organization)
    boolean revokeAllAccessForOrganization(String organizationId);

    List<String> getAccessibleOrganizations(String familyId);
}
