package com.user.microservice.user.infrastructure.outputadapters.events;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.domain.Usuario;
import com.user.microservice.user.domain.events.PasswordResetRequestedEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class UserEventPublisherNoOp implements UserEventPublisher {

    @Override
    public void userRegistered(Usuario usuario) {
        log.warn("[NoOp] Kafka deshabilitado. No se envía userRegistered para {}", usuario.getEmail());
    }

    @Override
    public void passwordResetRequested(PasswordResetRequestedEvent event) {
        log.warn("[NoOp] Kafka deshabilitado. No se envía passwordResetRequested para {}", event.email());
    }
}