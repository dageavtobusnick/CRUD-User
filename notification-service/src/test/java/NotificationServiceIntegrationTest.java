import org.example.notification.NotificationServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "app.email.templates.welcome-subject=Test Welcome",
        "app.email.templates.welcome-body=Welcome test body",
        "app.email.templates.deletion-subject=Test Deletion",
        "app.email.templates.deletion-body=Deletion test body",
        "spring.mail.host=localhost",
        "spring.mail.port=1025",
        "spring.mail.username=test",
        "spring.mail.password=test",
        "spring.mail.properties.mail.smtp.auth=false",
        "spring.mail.properties.mail.smtp.starttls.enable=false",
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.kafka.enabled=false"
})
@ContextConfiguration(classes = NotificationServiceApplication.class)
class NotificationServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSendWelcomeEmail() {
        String toEmail = "test@example.com";

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/notifications/welcome?toEmail=" + toEmail,
                null, String.class);

        assert response.getStatusCode().is2xxSuccessful();
        verify(mailSender, timeout(5000)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendDeletionEmail() {
        String toEmail = "test@example.com";

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/notifications/deletion?toEmail=" + toEmail,
                null, String.class);

        assert response.getStatusCode().is2xxSuccessful();
        verify(mailSender, timeout(5000)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendCustomEmail() {
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/notifications/email?toEmail=" + toEmail +
                        "&subject=" + subject + "&body=" + body,
                null, String.class);

        assert response.getStatusCode().is2xxSuccessful();
        verify(mailSender, timeout(5000)).send(any(SimpleMailMessage.class));
    }
}
