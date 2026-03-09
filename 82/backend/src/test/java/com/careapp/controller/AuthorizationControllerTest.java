package com.careapp.controller;

import com.careapp.domain.Authorization;
import com.careapp.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorizationController.class)
class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorizationService authorizationService;

    private Authorization mockAuth;

    @BeforeEach
    void setUp() {
        mockAuth = new Authorization();
        mockAuth.setId("auth-001");
        mockAuth.setPatientId("p1");
        mockAuth.setAuthorizedBy("userA");
        mockAuth.setAuthorizedTo("userB");
        mockAuth.setAuthorizationType("READ");
    }

    // ------------------ grantAccess ------------------
    @Test
    void testGrantAccessSuccess() throws Exception {
        Mockito.when(authorizationService.grantAccess("p1", "userA", "userB", "READ", "org1"))
                .thenReturn(true);

        String body = "{"
                + "\"patientId\":\"p1\","
                + "\"authorizedBy\":\"userA\","
                + "\"authorizedTo\":\"userB\","
                + "\"authorizationType\":\"READ\","
                + "\"organizationId\":\"org1\""
                + "}";

        mockMvc.perform(post("/api/authorization/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Access granted successfully!"));
    }

    @Test
    void testGrantAccessFailure() throws Exception {
        Mockito.when(authorizationService.grantAccess("p1", "userA", "userB", "READ", "org1"))
                .thenReturn(false);

        String body = "{"
                + "\"patientId\":\"p1\","
                + "\"authorizedBy\":\"userA\","
                + "\"authorizedTo\":\"userB\","
                + "\"authorizationType\":\"READ\","
                + "\"organizationId\":\"org1\""
                + "}";

        mockMvc.perform(post("/api/authorization/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Failed to grant access!"));
    }

    @Test
    void testGrantAccessMissingFields() throws Exception {
        String body = "{"
                + "\"patientId\":\"p1\""
                + "}";

        mockMvc.perform(post("/api/authorization/grant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Missing required fields!"));
    }

    // ------------------ revokeAccess ------------------
    @Test
    void testRevokeAccessSuccess() throws Exception {
        Mockito.when(authorizationService.revokeAccess("p1", "userA", "userB"))
                .thenReturn(true);

        String body = "{"
                + "\"patientId\":\"p1\","
                + "\"authorizedBy\":\"userA\","
                + "\"authorizedTo\":\"userB\""
                + "}";

        mockMvc.perform(post("/api/authorization/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Access revoked successfully!"));
    }

    @Test
    void testRevokeAccessFailure() throws Exception {
        Mockito.when(authorizationService.revokeAccess("p1", "userA", "userB"))
                .thenReturn(false);

        String body = "{"
                + "\"patientId\":\"p1\","
                + "\"authorizedBy\":\"userA\","
                + "\"authorizedTo\":\"userB\""
                + "}";

        mockMvc.perform(post("/api/authorization/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Failed to revoke access!"));
    }

    @Test
    void testRevokeAccessMissingFields() throws Exception {
        String body = "{"
                + "\"patientId\":\"p1\""
                + "}";

        mockMvc.perform(post("/api/authorization/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Missing required fields!"));
    }

    // ------------------ getAuthorizationsByPatient ------------------
    @Test
    void testGetAuthorizationsByPatient() throws Exception {
        List<Authorization> list = Collections.singletonList(mockAuth);
        Mockito.when(authorizationService.getAuthorizationsByPatient("p1"))
                .thenReturn(list);

        mockMvc.perform(get("/api/authorization/patient/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Authorizations retrieved successfully!"))
                .andExpect(jsonPath("$.data[0].patientId").value("p1"));
    }

    // ------------------ getAuthorizationsByUser ------------------
    @Test
    void testGetAuthorizationsByUser() throws Exception {
        List<Authorization> list = Collections.singletonList(mockAuth);
        Mockito.when(authorizationService.getAuthorizationsByUser("u1"))
                .thenReturn(list);

        mockMvc.perform(get("/api/authorization/user/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].authorizationType").value("READ"));
    }

    // ------------------ checkAccess ------------------
    @Test
    void testCheckAccess() throws Exception {
        Mockito.when(authorizationService.hasAccess("u1", "p1")).thenReturn(true);

        mockMvc.perform(get("/api/authorization/check")
                        .param("userId", "u1")
                        .param("patientId", "p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.msg").value("Access check completed!"));
    }

    // ------------------ accessible-patients ------------------
    @Test
    void testGetAccessiblePatients() throws Exception {
        List<String> patients = Arrays.asList("p1", "p2");
        Mockito.when(authorizationService.getAccessiblePatientIds("u1"))
                .thenReturn(patients);

        mockMvc.perform(get("/api/authorization/accessible-patients/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("p1"))
                .andExpect(jsonPath("$.data[1]").value("p2"));
    }

    // ------------------ revokeAllAccessForUser ------------------
    @Test
    void testRevokeAllAccessForUserSuccess() throws Exception {
        Mockito.when(authorizationService.revokeAllAccessForUser("u1")).thenReturn(true);

        mockMvc.perform(post("/api/authorization/revoke-all/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("All access revoked successfully!"));
    }

    @Test
    void testRevokeAllAccessForUserFailure() throws Exception {
        Mockito.when(authorizationService.revokeAllAccessForUser("u1")).thenReturn(false);

        mockMvc.perform(post("/api/authorization/revoke-all/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Failed to revoke all access!"));
    }

    // ------------------ revokeAllAccessByAuthorizer ------------------
    @Test
    void testRevokeAllAccessByAuthorizerSuccess() throws Exception {
        Mockito.when(authorizationService.revokeAllAccessByAuthorizer("authZ"))
                .thenReturn(true);

        mockMvc.perform(post("/api/authorization/revoke-by-authorizer/authZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("All access revoked successfully!"));
    }

    @Test
    void testRevokeAllAccessByAuthorizerFailure() throws Exception {
        Mockito.when(authorizationService.revokeAllAccessByAuthorizer("authZ"))
                .thenReturn(false);

        mockMvc.perform(post("/api/authorization/revoke-by-authorizer/authZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Failed to revoke all access!"));
    }

    // ------------------ getAccessibleOrganizations ------------------
    @Test
    void testGetAccessibleOrganizations() throws Exception {
        List<String> orgs = Arrays.asList("org1", "org2");
        Mockito.when(authorizationService.getAccessibleOrganizations("family1"))
                .thenReturn(orgs);

        mockMvc.perform(get("/api/authorization/accessible-orgs/family1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("org1"))
                .andExpect(jsonPath("$.data[1]").value("org2"))
                .andExpect(jsonPath("$.msg").value("Accessible organizations retrieved successfully!"));
    }
}
