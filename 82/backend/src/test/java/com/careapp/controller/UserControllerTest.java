package com.careapp.controller;

import com.careapp.domain.User;
import com.careapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("family-001");
        mockUser.setUname("bob.family@example.com");
        mockUser.setPassword("abc12345");
        mockUser.setRole("FAMILY");
    }

    // ------------------ Register ------------------

    @Test
    void testRegisterSuccess() throws Exception {
        Mockito.when(userService.registerService(Mockito.any(User.class)))
                .thenReturn(mockUser);

        String requestBody = "{"
                + "\"id\":\"family-001\","
                + "\"uname\":\"bob.family@example.com\","
                + "\"password\":\"abc12345\","
                + "\"role\":\"FAMILY\""
                + "}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Registration successful!"))
                .andExpect(jsonPath("$.data.id").value("family-001"));
    }

    // ------------------ Login ------------------

    @Test
    void testLoginSuccess() throws Exception {
        Mockito.when(userService.loginService("bob.family@example.com", "abc12345"))
                .thenReturn(mockUser);

        String requestBody = "{"
                + "\"uname\":\"bob.family@example.com\","
                + "\"password\":\"abc12345\""
                + "}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Login successful!"))
                .andExpect(jsonPath("$.data.uname").value("bob.family@example.com"));
    }

    @Test
    void testLoginFailure() throws Exception {
        Mockito.when(userService.loginService("wrongUser", "wrongPass"))
                .thenReturn(null);

        String requestBody = "{"
                + "\"uname\":\"wrongUser\","
                + "\"password\":\"wrongPass\""
                + "}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.msg").value("Invalid username or password!"));
    }

    // ------------------ Login by Email ------------------

    @Test
    void testLoginByEmailSuccess() throws Exception {
        Mockito.when(userService.loginByEmailService("bob.family@example.com", "abc12345"))
                .thenReturn(mockUser);

        String body = "{"
                + "\"email\":\"bob.family@example.com\","
                + "\"password\":\"abc12345\""
                + "}";

        mockMvc.perform(post("/api/auth/login-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Login successful!"));
    }

    @Test
    void testLoginByEmailFailure() throws Exception {
        Mockito.when(userService.loginByEmailService(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(null);

        String body = "{"
                + "\"email\":\"x@x.com\","
                + "\"password\":\"wrong\""
                + "}";

        mockMvc.perform(post("/api/auth/login-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.msg").value("Invalid email or password!"));
    }

    // ------------------ Invite Code ------------------

    @Test
    void testSubmitInviteCodeValid() throws Exception {
        String body = "{"
                + "\"inviteCode\":\"abc\""
                + "}";

        mockMvc.perform(post("/api/auth/submit-invite-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invite code validated successfully!"))
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    void testSubmitInviteCodeInvalid() throws Exception {
        String body = "{"
                + "\"inviteCode\":\"invalidCode\""
                + "}";

        mockMvc.perform(post("/api/auth/submit-invite-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Invalid invite code. Please check and try again."));
    }

    @Test
    void testSubmitInviteCodeEmpty() throws Exception {
        String body = "{"
                + "\"inviteCode\":\"\""
                + "}";

        mockMvc.perform(post("/api/auth/submit-invite-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Please enter an invite code"));
    }

    // ------------------ Invite Status ------------------

    @Test
    void testGetInviteStatus_UserUsedCode() throws Exception {
        User user = new User();
        user.setHasUsedInviteCode(true);
        Mockito.when(userService.getUserById("u1")).thenReturn(user);

        mockMvc.perform(get("/api/auth/invite-status")
                        .param("userId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Invite status retrieved!"))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.reason").value("already_used"));
    }

    @Test
    void testGetInviteStatus_UserMissingCode() throws Exception {
        User user = new User();
        user.setHasUsedInviteCode(false);
        Mockito.when(userService.getUserById("u2")).thenReturn(user);

        mockMvc.perform(get("/api/auth/invite-status")
                        .param("userId", "u2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false))
                .andExpect(jsonPath("$.data.reason").value("missing"));
    }

    @Test
    void testGetInviteStatus_UserNotFound() throws Exception {
        Mockito.when(userService.getUserById("notExist")).thenReturn(null);

        mockMvc.perform(get("/api/auth/invite-status")
                        .param("userId", "notExist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.msg").value("User not found!"));
    }

    @Test
    void testGetInviteStatus_NoUserId() throws Exception {
        mockMvc.perform(get("/api/auth/invite-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("User ID is required!"));
    }

    // ------------------ Change Password ------------------

    @Test
    void testChangePasswordSuccess() throws Exception {
        Mockito.when(userService.changePassword("bob", "oldpass", "newpass"))
                .thenReturn(true);

        String body = "{"
                + "\"identifier\":\"bob\","
                + "\"oldPassword\":\"oldpass\","
                + "\"newPassword\":\"newpass\""
                + "}";

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Password updated successfully!"));
    }

    @Test
    void testChangePasswordFailure() throws Exception {
        Mockito.when(userService.changePassword("bob", "oldpass", "newpass"))
                .thenReturn(false);

        String body = "{"
                + "\"identifier\":\"bob\","
                + "\"oldPassword\":\"oldpass\","
                + "\"newPassword\":\"newpass\""
                + "}";

        mockMvc.perform(post("/api/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.msg").value("Invalid credentials or parameters"));
    }
}
