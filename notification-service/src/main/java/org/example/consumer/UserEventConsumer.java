package org.example.consumer;

import org.example.event.UserEvent;
import org.example.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "${app.kafka.topics.user-events}")
    public void consumeUserEvent(UserEvent event) {
        logger.info("Received user event: {} for email: {}", event.getOperation(), event.getEmail());

        try {
            switch (event.getOperation().toUpperCase()) {
                case "CREATE":
                    emailService.sendWelcomeEmail(event.getEmail());
                    logger.info("Welcome email sent to: {}", event.getEmail());
                    break;
                case "DELETE":
                    emailService.sendDeletionEmail(event.getEmail());
                    logger.info("Deletion email sent to: {}", event.getEmail());
                    break;
                default:
                    logger.warn("Unknown operation: {}", event.getOperation());
            }
        } catch (Exception e) {
            logger.error("Failed to process user event for email: {}", event.getEmail(), e);
        }
    }
}
