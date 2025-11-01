package com.user.microservice.user.application.outputports;

import com.user.microservice.user.domain.Usuario;

public interface UserEventPublisher {
    void userRegistered(Usuario usuario);
}