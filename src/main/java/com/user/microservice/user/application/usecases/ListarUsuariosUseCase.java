package com.user.microservice.user.application.usecases;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.user.microservice.user.application.inputports.ListarUsuariosInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public class ListarUsuariosUseCase implements ListarUsuariosInputPort {

    private final UsuarioRepositorioPort repo;

    public ListarUsuariosUseCase(UsuarioRepositorioPort repo) {
        this.repo = repo;
    }

    @Override
    public Page<Usuario> listar(String q, Rol rol, Boolean enabled, Pageable p) {
        return repo.buscar(q, rol, enabled, p);
    }
}
