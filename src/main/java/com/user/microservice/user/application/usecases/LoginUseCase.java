package com.user.microservice.user.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.microservice.common.errors.BadRequestException;
import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.LoginInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.infrastructure.security.JwtService;

public class LoginUseCase implements LoginInputPort {

    private final UsuarioRepositorioPort repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public LoginUseCase(UsuarioRepositorioPort repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Override
    public String login(String email, String passwordPlano) {
        var u = repo.porEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new NotFoundException("Usuario/Password inválidos"));
        if (!u.isEnabled()) {
            throw new BadRequestException("Usuario deshabilitado");
        }
        if (!encoder.matches(passwordPlano, u.getPassword())) {
            throw new BadRequestException("Usuario/Password inválidos");
        }
        return jwt.generarToken(u.getId(), u.getEmail(), u.getRol().name());
    }
}
