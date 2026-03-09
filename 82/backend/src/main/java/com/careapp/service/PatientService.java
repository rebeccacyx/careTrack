package com.careapp.service;

import com.careapp.domain.Patient;
import com.careapp.repository.PatientRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    
    @Resource
    private PatientRepository patientRepository;
    
    // Create a new patient
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    // Get patient by ID
    public Optional<Patient> getPatientById(String id) {
        return patientRepository.findById(id);
    }
    
    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    // Update patient
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }
    
    // Delete patient
    public boolean deletePatient(String id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Get patients by family member ID
    public List<Patient> getPatientsByFamilyMember(String familyMemberId) {
        return patientRepository.findByFamilyMemberId(familyMemberId);
    }
    
    // Get patients by POA ID
    public List<Patient> getPatientsByPOA(String poaId) {
        return patientRepository.findByPoaId(poaId);
    }
    
    // Get patient by medical record number
    public Optional<Patient> getPatientByMedicalRecordNumber(String medicalRecordNumber) {
        return patientRepository.findByMedicalRecordNumber(medicalRecordNumber);
    }
    
    // Get patient by Client ID
    public Optional<Patient> getPatientByClientId(String clientId) {
        return patientRepository.findByClientId(clientId);
    }
    
    // Assign patient to family member
    public Patient assignFamilyMember(String patientId, String familyMemberId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setFamilyMemberId(familyMemberId);
            return patientRepository.save(patient);
        }
        return null;
    }
    
    // Assign patient to POA
    public Patient assignPOA(String patientId, String poaId) {
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            patient.setPoaId(poaId);
            return patientRepository.save(patient);
        }
        return null;
    }
    
    // Get patients by organization ID
    public List<Patient> getPatientsByOrganization(String organizationId) {
        return patientRepository.findByOrganizationId(organizationId);
    }
}