package com.user.microservice.user.application.outputports;

import com.user.microservice.user.domain.Usuario;
import com.user.microservice.user.domain.events.PasswordResetRequestedEvent;

public interface UserEventPublisher {
    void userRegistered(Usuario usuario);
    void passwordResetRequested(PasswordResetRequestedEvent event);
}