package com.careapp.repository;

import com.careapp.domain.InviteCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InviteCodeRepository extends MongoRepository<InviteCode, String> {
    // Find invite code by code string
    InviteCode findByCode(String code);
    
    // Find invite codes by creator
    List<InviteCode> findByCreatedBy(String createdBy);
    
    // Find unused invite codes by creator
    List<InviteCode> findByCreatedByAndIsUsedFalse(String createdBy);
    
    // Find expired invite codes
    List<InviteCode> findByExpiresAtBeforeAndIsUsedFalse(LocalDateTime now);
    
    // Find active invite codes for a patient
    List<InviteCode> findByPatientIdAndIsUsedFalseAndExpiresAtAfter(String patientId, LocalDateTime now);
    
    // Find invite codes used by a specific user
    List<InviteCode> findByUsedBy(String usedBy);
}
