package com.user.microservice.user.application.usecases;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.SolicitarResetPasswordInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.infrastructure.outputadapters.persistence.entity.ResetTokenDbEntity;
import com.user.microservice.user.infrastructure.outputadapters.persistence.repository.ResetTokenJpaRepository;

public class SolicitarResetPasswordUseCase implements SolicitarResetPasswordInputPort {

    private final UsuarioRepositorioPort usuarios;
    private final ResetTokenJpaRepository tokens;

    public SolicitarResetPasswordUseCase(UsuarioRepositorioPort usuarios, ResetTokenJpaRepository tokens) {
        this.usuarios = usuarios;
        this.tokens = tokens;
    }

    @Override
    public String solicitar(String email) {
        var u = usuarios.porEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new NotFoundException("Email no registrado"));

        var t = new ResetTokenDbEntity();
        t.setId(UUID.randomUUID());
        t.setUserId(u.getId());
        t.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        t.setUsed(false);
        tokens.save(t);
        // Dev-mode: devolvemos el token; en prod, se envía por email
        return t.getId().toString();
    }
}
