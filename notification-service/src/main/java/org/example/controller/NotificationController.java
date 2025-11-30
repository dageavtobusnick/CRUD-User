package org.example.controller;

import org.example.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String body) {

        try {
            emailService.sendCustomEmail(toEmail, subject, body);
            return ResponseEntity.ok("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcomeEmail(@RequestParam String toEmail) {
        try {
            emailService.sendWelcomeEmail(toEmail);
            return ResponseEntity.ok("Welcome email sent successfully to: " + toEmail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send welcome email: " + e.getMessage());
        }
    }

    @PostMapping("/deletion")
    public ResponseEntity<String> sendDeletionEmail(@RequestParam String toEmail) {
        try {
            emailService.sendDeletionEmail(toEmail);
            return ResponseEntity.ok("Deletion email sent successfully to: " + toEmail);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send deletion email: " + e.getMessage());
        }
    }
}
