package com.careapp.controller;

import com.careapp.service.impl.EmailService;
import com.careapp.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    private final EmailService emailService;
    
    public MailController(EmailService emailService) { 
        this.emailService = emailService; 
    }

    /**
     * Test email sending endpoint
     */
    @PostMapping("/test")
    public Result<String> testEmail(@RequestParam String to) {
        try {
            emailService.sendText(
                to, 
                "CareTrack Test Email", 
                "Hello from CareTrack!\n\nThis is a test email sent via SendGrid.\n\nIf you received this email, your email service is configured correctly."
            );
            return Result.success("Email sent successfully", "Test email sent to " + to);
        } catch (IllegalStateException e) {
            return Result.error("400", e.getMessage());
        } catch (Exception e) {
            return Result.error("500", "Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Check email service configuration status
     */
    @GetMapping("/status")
    public Result<Map<String, Object>> getEmailStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("configured", emailService.isConfigured());
        status.put("message", emailService.isConfigured() 
            ? "Email service is configured and ready" 
            : "Email service is not configured. Please set SENDGRID_API_KEY environment variable.");
        
        return Result.success(status, emailService.isConfigured() 
            ? "Email service is ready" 
            : "Email service needs configuration");
    }
}
