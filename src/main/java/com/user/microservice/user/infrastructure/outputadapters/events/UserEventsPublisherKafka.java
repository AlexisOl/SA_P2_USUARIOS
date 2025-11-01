package com.user.microservice.user.infrastructure.outputadapters.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.domain.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventsPublisherKafka implements UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate; // <String, String>
    private final ObjectMapper objectMapper;

    // Si tienes en application.yml: app.kafka.topic.users: users.events.v1
    // inyecta el nombre del tópico:
    @org.springframework.beans.factory.annotation.Value("${app.kafka.topic.users:users.events.v1}")
    private String usersTopic;

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

    // payload compacto que espera notificaciones
    record RegisteredPayload(String userId, String nombre, String email) {}
}