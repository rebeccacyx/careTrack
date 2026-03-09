package com.careapp.service;

import com.careapp.domain.InviteCode;

import java.util.List;

public interface InviteCodeService {
    // Generate invite code
    String generateInviteCode(String createdBy, String createdByType, String targetType, String patientId, String organizationId);
    
    // Validate invite code
    boolean validateInviteCode(String code);
    
    // Use invite code
    boolean useInviteCode(String code, String usedBy);
    
    // Get invite codes by creator
    List<InviteCode> getInviteCodesByCreator(String creatorId);
    
    // Revoke invite code
    boolean revokeInviteCode(String codeId);
    
    // Get active invite codes for patient
    List<InviteCode> getActiveInviteCodesForPatient(String patientId);
    
    // Get invite codes used by a specific user
    List<InviteCode> getInviteCodesUsedByUser(String userId);
    
    // Clean up expired invite codes
    void cleanupExpiredInviteCodes();
}
