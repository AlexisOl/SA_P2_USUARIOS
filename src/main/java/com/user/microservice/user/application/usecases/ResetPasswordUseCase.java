package com.user.microservice.user.application.usecases;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.microservice.common.errors.BadRequestException;
import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.ResetPasswordInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.infrastructure.outputadapters.persistence.repository.ResetTokenJpaRepository;

public class ResetPasswordUseCase implements ResetPasswordInputPort {

    private final UsuarioRepositorioPort usuarios;
    private final ResetTokenJpaRepository tokens;
    private final PasswordEncoder encoder;

    public ResetPasswordUseCase(UsuarioRepositorioPort usuarios, ResetTokenJpaRepository tokens, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
    }

    @Override
    public void reset(String token, String newPassword) {
        var t = tokens.findById(UUID.fromString(token)).orElseThrow(() -> new NotFoundException("Token inválido"));
        if (t.isUsed() || t.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Token expirado/uso inválido");
        }
        var u = usuarios.porId(t.getUserId()).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        u.cambiarPasswordHash(encoder.encode(newPassword));
        usuarios.guardar(u);
        t.setUsed(true);
        tokens.save(t);
    }
}
