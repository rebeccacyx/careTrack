package com.careapp.repository;

import com.careapp.domain.Authorization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizationRepository extends MongoRepository<Authorization, String> {
    // Find authorizations by patient
    List<Authorization> findByPatientId(String patientId);
    
    // Find active authorizations by patient
    List<Authorization> findByPatientIdAndIsActiveTrue(String patientId);
    
    // Find authorizations by authorized user
    List<Authorization> findByAuthorizedTo(String authorizedTo);
    
    // Find active authorizations by authorized user
    List<Authorization> findByAuthorizedToAndIsActiveTrue(String authorizedTo);
    
    // Find authorizations by authorizer
    List<Authorization> findByAuthorizedBy(String authorizedBy);
    
    // Find active authorizations by authorizer
    List<Authorization> findByAuthorizedByAndIsActiveTrue(String authorizedBy);
    
    // Find specific authorization
    Authorization findByPatientIdAndAuthorizedToAndIsActiveTrue(String patientId, String authorizedTo);
    
    // Find authorizations by organization
    List<Authorization> findByOrganizationId(String organizationId);
    
    // Find active authorizations by organization
    List<Authorization> findByOrganizationIdAndIsActiveTrue(String organizationId);
    
    // Find active authorization by user, patient, and organization
    List<Authorization> findByAuthorizedToAndPatientIdAndOrganizationIdAndIsActiveTrue(
        String authorizedTo, String patientId, String organizationId);

}
