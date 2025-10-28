package com.user.microservice.user.application.usecases;

import java.util.UUID;

import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.ActualizarUsuarioInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public class ActualizarUsuarioUseCase implements ActualizarUsuarioInputPort {

    private final UsuarioRepositorioPort repo;

    public ActualizarUsuarioUseCase(UsuarioRepositorioPort repo) {
        this.repo = repo;
    }

    @Override
    public Usuario actualizar(UUID id, String nombre, Rol rol, Boolean enabled) {
        var u = repo.porId(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        u.actualizar(nombre, rol, enabled);
        return repo.guardar(u);
    }
}
