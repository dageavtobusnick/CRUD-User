package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.templates.welcome-subject}")
    private String welcomeSubject;

    @Value("${app.email.templates.welcome-body}")
    private String welcomeBody;

    @Value("${app.email.templates.deletion-subject}")
    private String deletionSubject;

    @Value("${app.email.templates.deletion-body}")
    private String deletionBody;

    public void sendWelcomeEmail(String toEmail) {
        sendEmail(toEmail, welcomeSubject, welcomeBody);
    }

    public void sendDeletionEmail(String toEmail) {
        sendEmail(toEmail, deletionSubject, deletionBody);
    }

    public void sendCustomEmail(String toEmail, String subject, String body) {
        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
