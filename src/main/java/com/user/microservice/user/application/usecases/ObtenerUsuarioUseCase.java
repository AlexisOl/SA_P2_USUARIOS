package com.user.microservice.user.application.usecases;

import java.util.Optional;
import java.util.UUID;

import com.user.microservice.user.application.inputports.ObtenerUsuarioInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Usuario;

public class ObtenerUsuarioUseCase implements ObtenerUsuarioInputPort {

    private final UsuarioRepositorioPort repo;

    public ObtenerUsuarioUseCase(UsuarioRepositorioPort repo) {
        this.repo = repo;
    }

    @Override
    public Optional<Usuario> porId(UUID id) {
        return repo.porId(id);
    }
}
