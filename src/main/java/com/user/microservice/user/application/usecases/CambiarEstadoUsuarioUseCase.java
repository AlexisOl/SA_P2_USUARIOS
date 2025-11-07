package com.user.microservice.user.application.usecases;

import java.util.UUID;

import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.CambiarEstadoUsuarioInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Usuario;

public class CambiarEstadoUsuarioUseCase implements CambiarEstadoUsuarioInputPort {
  private final UsuarioRepositorioPort repo;
  public CambiarEstadoUsuarioUseCase(UsuarioRepositorioPort repo){ this.repo = repo; }

  @Override
  public Usuario habilitar(UUID id, boolean enabled) {
    var u = repo.porId(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    u.actualizar(null, null, null, null, enabled);
    return repo.guardar(u);
  }
}