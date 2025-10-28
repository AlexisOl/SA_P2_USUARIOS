// user/application/outputports/UsuarioRepositorioPort.java
package com.user.microservice.user.application.outputports;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public interface UsuarioRepositorioPort {

    Usuario guardar(Usuario u);

    Optional<Usuario> porId(UUID id);

    Optional<Usuario> porEmail(String email);

    boolean emailOcupado(String email);

    boolean dpiOcupado(String dpi);

    Page<Usuario> buscar(String q, Rol rol, Boolean enabled, Pageable p);
}
