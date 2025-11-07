package com.user.microservice.user.application.inputports;

import java.util.UUID;

import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public interface ActualizarUsuarioInputPort {

    Usuario actualizar(UUID id, String nombre, String email, Rol rol, String dpi,  Boolean enabled);
}
