package com.careapp.repository;

import com.careapp.domain.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    
    // Find patients by family member ID
    List<Patient> findByFamilyMemberId(String familyMemberId);
    
    // Find patients by POA ID
    List<Patient> findByPoaId(String poaId);
    
    // Find patient by medical record number
    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);
    
    // Find patient by Client ID
    Optional<Patient> findByClientId(String clientId);
    
    // Find patients by first name
    List<Patient> findByFirstName(String firstName);
    
    // Find patients by last name
    List<Patient> findByLastName(String lastName);
    
    // Find patients by first name and last name
    List<Patient> findByFirstNameAndLastName(String firstName, String lastName);
    
    // Find patients by current status
    List<Patient> findByCurrentStatus(String currentStatus);
    
    // Find patients by date of birth
    List<Patient> findByDateOfBirth(String dateOfBirth);
    
    // Count patients by family member
    long countByFamilyMemberId(String familyMemberId);
    
    // Count patients by POA
    long countByPoaId(String poaId);
    
    // Count patients by current status
    long countByCurrentStatus(String currentStatus);
    
    // Find patients by organization ID
    List<Patient> findByOrganizationId(String organizationId);
    
    // Count patients by organization
    long countByOrganizationId(String organizationId);
}