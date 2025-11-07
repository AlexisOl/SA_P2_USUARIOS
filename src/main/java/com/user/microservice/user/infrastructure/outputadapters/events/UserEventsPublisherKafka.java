package com.user.microservice.user.infrastructure.outputadapters.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.domain.Usuario;
import com.user.microservice.user.domain.events.PasswordResetRequestedEvent;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.RegisteredPayload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventsPublisherKafka implements UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // inyecta el nombre del tópico:
//    @org.springframework.beans.factory.annotation.Value("${app.kafka.topic.users:users.events.v1}")
//    private String usersTopic;
    @Value("${app.kafka.topic.users:users-events-v1}")
    private String usersTopic;

    @Value("${app.kafka.topic.reset-password:reset-user-password}")
    private String resetPasswordTopic;
    
    @Transactional
    @Override
    public void userRegistered(Usuario usuario) {
        try {
            String json = objectMapper.writeValueAsString(
                    new RegisteredPayload(usuario.getId().toString(), usuario.getNombre(), usuario.getEmail())
            );

            Message<String> msg = MessageBuilder
                    .withPayload(json)
                    .setHeader(KafkaHeaders.TOPIC, usersTopic)
                    .setHeader("kafka_messageKey", usuario.getId().toString())
                    .setHeader("eventType", "user.registered.v1")
                    .build();

            kafkaTemplate.send(msg).whenComplete((r, ex) -> {
                if (ex != null) {
                    log.error("Error enviando evento Kafka", ex);
                } else {
                    log.info("Evento Kafka enviado. topic={} partition={} offset={} headers={}",
                            r.getRecordMetadata().topic(),
                            r.getRecordMetadata().partition(),
                            r.getRecordMetadata().offset(),
                            msg.getHeaders());
                }
            });
        } catch (Exception e) {
            log.error("Error enviando evento Kafka: {}", e.getMessage(), e);
        }
    }
    
    @Transactional
    @Override
    public void passwordResetRequested(PasswordResetRequestedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            Message<String> msg = MessageBuilder
                    .withPayload(json)
                    .setHeader(KafkaHeaders.TOPIC, resetPasswordTopic)
                    .setHeader(KafkaHeaders.KEY, event.userId())
                    .setHeader("eventType", "user.password.reset.requested.v1")
                    .build();

            kafkaTemplate.send(msg); // <-- SIN CAST
            log.info("Evento 'passwordResetRequested' publicado para {}", event.email());
        } catch (Exception e) {
            log.error("Error publicando passwordResetRequested", e);
        }
    }

}
