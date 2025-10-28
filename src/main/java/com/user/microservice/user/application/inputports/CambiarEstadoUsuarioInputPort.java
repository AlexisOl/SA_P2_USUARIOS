package com.user.microservice.user.application.inputports;

import java.util.UUID;

import com.user.microservice.user.domain.Usuario;

public interface CambiarEstadoUsuarioInputPort {

    Usuario habilitar(UUID id, boolean enabled);
}
