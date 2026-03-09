package com.careapp.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    @Value("${sendgrid.api-key:}")
    private String apiKey;

    /**
     * Check if email service is configured
     * @return true if API key is configured and valid
     */
    public boolean isConfigured() {
        // SendGrid API Key usually starts with "SG." but can also start with "SG" (without dot)
        // Accept both formats: SG.xxxx or SGxxxx
        return StringUtils.hasText(apiKey) && (apiKey.startsWith("SG.") || apiKey.startsWith("SG"));
    }

    /**
     * Send plain text email
     * @param to Recipient email address
     * @param subject Email subject
     * @param text Email content (plain text)
     * @throws Exception If email sending fails
     */
    public void sendText(String to, String subject, String text) throws Exception {
        // Check if API key is configured
        if (!isConfigured()) {
            throw new IllegalStateException(
                "SendGrid API key is not configured. " +
                "Please set the SENDGRID_API_KEY environment variable. " +
                "Example: export SENDGRID_API_KEY=SG.your_api_key_here"
            );
        }

        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("Recipient email address cannot be empty");
        }

        Email from = new Email("caretrack3@gmail.com", "CareTrack");
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);

        Mail mail = new Mail(from, subject, toEmail, content);

        try {
            SendGrid sg = new SendGrid(apiKey);
            Request req = new Request();
            req.setMethod(Method.POST);
            req.setEndpoint("mail/send");
            req.setBody(mail.build());
            Response resp = sg.api(req);

            if (resp.getStatusCode() < 200 || resp.getStatusCode() >= 300) {
                throw new RuntimeException(
                    String.format("SendGrid API error [%d]: %s", resp.getStatusCode(), resp.getBody())
                );
            }
            
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send HTML email
     * @param to Recipient email address
     * @param subject Email subject
     * @param html Email content (HTML)
     * @throws Exception If email sending fails
     */
    public void sendHtml(String to, String subject, String html) throws Exception {
        // Check if API key is configured
        if (!isConfigured()) {
            throw new IllegalStateException(
                "SendGrid API key is not configured. " +
                "Please set the SENDGRID_API_KEY environment variable. " +
                "Example: export SENDGRID_API_KEY=SG.your_api_key_here"
            );
        }

        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("Recipient email address cannot be empty");
        }

        Email from = new Email("caretrack3@gmail.com", "CareTrack");
        Email toEmail = new Email(to);
        Content content = new Content("text/html", html);

        Mail mail = new Mail(from, subject, toEmail, content);

        try {
            SendGrid sg = new SendGrid(apiKey);
            Request req = new Request();
            req.setMethod(Method.POST);
            req.setEndpoint("mail/send");
            req.setBody(mail.build());
            Response resp = sg.api(req);

            if (resp.getStatusCode() < 200 || resp.getStatusCode() >= 300) {
                throw new RuntimeException(
                    String.format("SendGrid API error [%d]: %s", resp.getStatusCode(), resp.getBody())
                );
            }
            
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            throw e;
        }
    }
}
