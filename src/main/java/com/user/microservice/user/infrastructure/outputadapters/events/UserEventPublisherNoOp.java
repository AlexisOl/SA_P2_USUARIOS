package com.user.microservice.user.infrastructure.outputadapters.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.domain.Usuario;

@ConditionalOnProperty(value = "app.kafka.enabled", havingValue = "false", matchIfMissing = true)
@Component
public class UserEventPublisherNoOp implements UserEventPublisher {

    @Override
    public void userRegistered(Usuario usuario) {
        // No hace nada (Kafka está deshabilitado)
        System.out.println("Evento Kafka omitido (Kafka deshabilitado)");
    }
}
