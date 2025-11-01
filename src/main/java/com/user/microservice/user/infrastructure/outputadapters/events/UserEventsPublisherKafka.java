package com.user.microservice.user.infrastructure.outputadapters.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.domain.Usuario;

import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class UserEventsPublisherKafka implements UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void userRegistered(Usuario usuario) {
        try {
            String json = objectMapper.writeValueAsString(usuario);
            kafkaTemplate.send("users.events.v1", json);
            System.out.println("Evento Kafka enviado: " + usuario.getEmail());
        } catch (Exception e) {
            System.err.println("Error enviando evento Kafka: " + e.getMessage());
        }
    }
}