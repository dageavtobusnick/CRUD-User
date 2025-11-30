import org.example.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {EmailService.class})
@TestPropertySource(properties = {
        "app.email.templates.welcome-subject=Test Welcome",
        "app.email.templates.welcome-body=Welcome test body",
        "app.email.templates.deletion-subject=Test Deletion",
        "app.email.templates.deletion-body=Deletion test body",
        "spring.mail.host=localhost",
        "spring.mail.port=1025",
        "spring.mail.username=test",
        "spring.mail.password=test",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.cloud.config.enabled=false"
})
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSendWelcomeEmail() {
        String testEmail = "test@example.com";

        emailService.sendWelcomeEmail(testEmail);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionEmail() {
        String testEmail = "test@example.com";

        emailService.sendDeletionEmail(testEmail);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCustomEmail() {
        String testEmail = "test@example.com";
        String subject = "Custom Subject";
        String body = "Custom Body";

        emailService.sendCustomEmail(testEmail, subject, body);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
