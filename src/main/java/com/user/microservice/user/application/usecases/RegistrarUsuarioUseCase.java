// user/application/usecases/RegistrarUsuarioUseCase.java
package com.user.microservice.user.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.microservice.common.errors.AlreadyExistsException;
import com.user.microservice.user.application.inputports.RegistrarUsuarioInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public class RegistrarUsuarioUseCase implements RegistrarUsuarioInputPort {

    private final UsuarioRepositorioPort repo;
    private final PasswordEncoder encoder;

    public RegistrarUsuarioUseCase(UsuarioRepositorioPort repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public Usuario registrar(String nombre, String email, String passwordPlano, String dpi, Rol rol) {
        email = email.toLowerCase().trim();
        if (repo.emailOcupado(email)) {
            throw new AlreadyExistsException("Email ya registrado");
        }
        if (repo.dpiOcupado(dpi)) {
            throw new AlreadyExistsException("DPI ya registrado");
        }
        var hash = encoder.encode(passwordPlano);
        var u = Usuario.nuevo(nombre, email, hash, dpi.trim(), rol == null ? Rol.CLIENTE : rol);
        return repo.guardar(u);
    }
}
