package org.example.service;

import org.example.event.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    @Value("${app.kafka.topics.user-events}")
    private String userEventsTopic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserEvent(String operation, String email) {
        UserEvent event = new UserEvent(operation, email);
        kafkaTemplate.send(userEventsTopic, event);
    }
}
