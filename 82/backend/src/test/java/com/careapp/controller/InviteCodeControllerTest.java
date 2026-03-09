package com.careapp.controller;

import com.careapp.domain.InviteCode;
import com.careapp.service.InviteCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InviteCodeController.class)
class InviteCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InviteCodeService inviteCodeService;

    private InviteCode mockCode;

    @BeforeEach
    void setUp() {
        mockCode = new InviteCode();
        mockCode.setId("code001");
        mockCode.setCode("ABC123");
        mockCode.setPatientId("p1");
        mockCode.setCreatedBy("manager001");
    }

    @Test
    void testGenerateInviteCodeSuccess() throws Exception {
        Mockito.when(inviteCodeService.generateInviteCode(
                Mockito.anyString(), Mockito.anyString(),
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("ABC123");

        String body = "{"
                + "\"createdBy\":\"manager001\","
                + "\"createdByType\":\"MANAGER\","
                + "\"targetType\":\"WORKER\","
                + "\"patientId\":\"p1\","
                + "\"organizationId\":\"org1\""
                + "}";

        mockMvc.perform(post("/api/invite/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ABC123"))
                .andExpect(jsonPath("$.msg").value("Invite code generated successfully!"));
    }

    @Test
    void testGenerateInviteCodeMissingFields() throws Exception {
        String body = "{"
                + "\"createdBy\":\"manager001\","
                + "\"createdByType\":\"MANAGER\""
                + "}";

        mockMvc.perform(post("/api/invite/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Missing required fields!"));
    }

    @Test
    void testGenerateInviteCodeInvalidCreatorType() throws Exception {
        String body = "{"
                + "\"createdBy\":\"user1\","
                + "\"createdByType\":\"INVALID\","
                + "\"targetType\":\"WORKER\","
                + "\"patientId\":\"p1\","
                + "\"organizationId\":\"org1\""
                + "}";

        mockMvc.perform(post("/api/invite/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invalid creator type! Must be FM, POA, or MANAGER"));
    }

    @Test
    void testGenerateInviteCodeInvalidTargetType() throws Exception {
        String body = "{"
                + "\"createdBy\":\"user1\","
                + "\"createdByType\":\"MANAGER\","
                + "\"targetType\":\"PATIENT\","
                + "\"patientId\":\"p1\","
                + "\"organizationId\":\"org1\""
                + "}";

        mockMvc.perform(post("/api/invite/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invalid target type! Must be MANAGER or WORKER"));
    }

    @Test
    void testUseInviteCodeSuccess() throws Exception {
        Mockito.when(inviteCodeService.validateInviteCode("ABC123")).thenReturn(true);
        Mockito.when(inviteCodeService.useInviteCode("ABC123", "user001")).thenReturn(true);

        String body = "{"
                + "\"code\":\"ABC123\","
                + "\"usedBy\":\"user001\""
                + "}";

        mockMvc.perform(post("/api/invite/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invite code used successfully!"))
                .andExpect(jsonPath("$.data").value("Access granted!"));
    }

    @Test
    void testUseInviteCodeMissingParams() throws Exception {
        String body = "{"
                + "\"code\":null"
                + "}";

        mockMvc.perform(post("/api/invite/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Code and usedBy are required!"));
    }

    @Test
    void testUseInviteCodeInvalid() throws Exception {
        Mockito.when(inviteCodeService.validateInviteCode("INVALID")).thenReturn(false);

        String body = "{"
                + "\"code\":\"INVALID\","
                + "\"usedBy\":\"user001\""
                + "}";

        mockMvc.perform(post("/api/invite/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invalid or expired invite code!"));
    }

    @Test
    void testUseInviteCodeFailure() throws Exception {
        Mockito.when(inviteCodeService.validateInviteCode("ABC123")).thenReturn(true);
        Mockito.when(inviteCodeService.useInviteCode("ABC123", "user001")).thenReturn(false);

        String body = "{"
                + "\"code\":\"ABC123\","
                + "\"usedBy\":\"user001\""
                + "}";

        mockMvc.perform(post("/api/invite/use")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(
                        "Failed to use invite code! The token type may not match your user role, or the token may be invalid."));
    }

    @Test
    void testGetMyInviteCodes() throws Exception {
        Mockito.when(inviteCodeService.getInviteCodesByCreator("manager001"))
                .thenReturn(List.of(mockCode));

        mockMvc.perform(get("/api/invite/my-codes")
                        .param("creatorId", "manager001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("ABC123"))
                .andExpect(jsonPath("$.msg").value("Invite codes retrieved successfully!"));
    }

    @Test
    void testRevokeInviteCodeSuccess() throws Exception {
        Mockito.when(inviteCodeService.revokeInviteCode("code001")).thenReturn(true);

        mockMvc.perform(delete("/api/invite/code001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invite code revoked successfully!"));
    }

    @Test
    void testRevokeInviteCodeFailure() throws Exception {
        Mockito.when(inviteCodeService.revokeInviteCode("code001")).thenReturn(false);

        mockMvc.perform(delete("/api/invite/code001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Failed to revoke invite code!"));
    }

    @Test
    void testGetActiveInviteCodesForPatient() throws Exception {
        Mockito.when(inviteCodeService.getActiveInviteCodesForPatient("p1"))
                .thenReturn(List.of(mockCode));

        mockMvc.perform(get("/api/invite/patient/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].code").value("ABC123"))
                .andExpect(jsonPath("$.msg").value("Active invite codes retrieved successfully!"));
    }
}


