package com.careapp.controller;

import com.careapp.domain.Patient;
import com.careapp.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    private Patient mockPatient;

    @BeforeEach
    void setUp() {
        mockPatient = new Patient();
        mockPatient.setId("p001");
        mockPatient.setFirstName("Alice");
        mockPatient.setLastName("Johnson");
        mockPatient.setAge(65);
        mockPatient.setClientId("c123");
        mockPatient.setOrganizationId("org001");
    }

    // ========== Create Patient ==========
    @Test
    void testCreatePatientSuccess() throws Exception {
        Mockito.when(patientService.createPatient(Mockito.any(Patient.class))).thenReturn(mockPatient);

        String body = "{"
                + "\"firstName\":\"Alice\","
                + "\"lastName\":\"Johnson\","
                + "\"age\":65,"
                + "\"organizationId\":\"org001\""
                + "}";

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Patient created successfully!"))
                .andExpect(jsonPath("$.data.id").value("p001"));
    }

    @Test
    void testCreatePatientError() throws Exception {
        Mockito.when(patientService.createPatient(Mockito.any(Patient.class)))
                .thenThrow(new RuntimeException("DB error"));

        String body = "{ \"firstName\":\"Bob\" }";

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to create patient: DB error"));
    }

    // ========== Get All Patients ==========
    @Test
    void testGetAllPatientsSuccess() throws Exception {
        Mockito.when(patientService.getAllPatients()).thenReturn(Collections.singletonList(mockPatient));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Patients retrieved successfully!"))
                .andExpect(jsonPath("$.data[0].firstName").value("Alice"));
    }

    @Test
    void testGetAllPatientsError() throws Exception {
        Mockito.when(patientService.getAllPatients()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to retrieve patients: DB error"));
    }

    // ========== Get Patient By ID ==========
    @Test
    void testGetPatientByIdSuccess() throws Exception {
        Mockito.when(patientService.getPatientById("p001")).thenReturn(Optional.of(mockPatient));

        mockMvc.perform(get("/api/patients/p001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Patient retrieved successfully!"))
                .andExpect(jsonPath("$.data.firstName").value("Alice"));
    }

    @Test
    void testGetPatientByIdNotFound() throws Exception {
        Mockito.when(patientService.getPatientById("notExist")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/patients/notExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Patient not found!"));
    }

    // ========== Update Patient ==========
    @Test
    void testUpdatePatientSuccess() throws Exception {
        Mockito.when(patientService.getPatientById("p001")).thenReturn(Optional.of(mockPatient));
        Mockito.when(patientService.updatePatient(Mockito.any(Patient.class))).thenReturn(mockPatient);

        String body = "{ \"age\": 70 }";

        mockMvc.perform(put("/api/patients/p001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Patient updated successfully!"));
    }

    @Test
    void testUpdatePatientNotFound() throws Exception {
        Mockito.when(patientService.getPatientById("p999")).thenReturn(Optional.empty());

        String body = "{ \"age\": 70 }";

        mockMvc.perform(put("/api/patients/p999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Patient not found!"));
    }

    @Test
    void testUpdatePatientError() throws Exception {
        Mockito.when(patientService.getPatientById("p001")).thenReturn(Optional.of(mockPatient));
        Mockito.when(patientService.updatePatient(Mockito.any(Patient.class)))
                .thenThrow(new RuntimeException("Update failed"));

        String body = "{ \"firstName\": \"Eve\" }";

        mockMvc.perform(put("/api/patients/p001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to update patient: Update failed"));
    }

    // ========== Delete Patient ==========
    @Test
    void testDeletePatientSuccess() throws Exception {
        Mockito.when(patientService.deletePatient("p001")).thenReturn(true);

        mockMvc.perform(delete("/api/patients/p001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Patient deleted successfully!"));
    }

    @Test
    void testDeletePatientNotFound() throws Exception {
        Mockito.when(patientService.deletePatient("p001")).thenReturn(false);

        mockMvc.perform(delete("/api/patients/p001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Patient not found!"));
    }

    @Test
    void testDeletePatientError() throws Exception {
        Mockito.when(patientService.deletePatient("p001"))
                .thenThrow(new RuntimeException("Delete failed"));

        mockMvc.perform(delete("/api/patients/p001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.msg").value("Failed to delete patient: Delete failed"));
    }

    // ========== Assign Family Member ==========
    @Test
    void testAssignFamilyMemberSuccess() throws Exception {
        Mockito.when(patientService.assignFamilyMember("p001", "f001")).thenReturn(mockPatient);

        String body = "{ \"familyMemberId\": \"f001\" }";

        mockMvc.perform(post("/api/patients/p001/assign-family-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Family member assigned successfully!"));
    }

    @Test
    void testAssignFamilyMemberNotFound() throws Exception {
        Mockito.when(patientService.assignFamilyMember("p999", "f001")).thenReturn(null);

        String body = "{ \"familyMemberId\": \"f001\" }";

        mockMvc.perform(post("/api/patients/p999/assign-family-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Patient not found!"));
    }

    // ========== Assign POA ==========
    @Test
    void testAssignPOASuccess() throws Exception {
        Mockito.when(patientService.assignPOA("p001", "poa001")).thenReturn(mockPatient);

        String body = "{ \"poaId\": \"poa001\" }";

        mockMvc.perform(post("/api/patients/p001/assign-poa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("POA assigned successfully!"));
    }

    @Test
    void testAssignPOANotFound() throws Exception {
        Mockito.when(patientService.assignPOA("p999", "poa001")).thenReturn(null);

        String body = "{ \"poaId\": \"poa001\" }";

        mockMvc.perform(post("/api/patients/p999/assign-poa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("Patient not found!"));
    }
}

