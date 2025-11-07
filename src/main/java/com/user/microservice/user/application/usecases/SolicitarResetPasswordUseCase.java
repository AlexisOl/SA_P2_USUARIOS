package com.user.microservice.user.application.usecases;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.SolicitarResetPasswordInputPort;
import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Usuario;
import com.user.microservice.user.domain.events.PasswordResetRequestedEvent;
import com.user.microservice.user.infrastructure.outputadapters.persistence.entity.ResetTokenDbEntity;
import com.user.microservice.user.infrastructure.outputadapters.persistence.repository.ResetTokenJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Caso de uso: Solicitar reset de contraseña.
 * - Persiste token (30 min)
 * - Publica evento a Kafka para que Notificaciones envíe el correo
 * - Devuelve el token solo en modo dev (configurable)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitarResetPasswordUseCase implements SolicitarResetPasswordInputPort {

    private final UsuarioRepositorioPort usuarios;
    private final ResetTokenJpaRepository tokens;
    private final UserEventPublisher eventPublisher;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.security.dev-return-token:false}")
    private boolean devReturnToken;

    @Override
    public String solicitar(String email) {
        final Usuario u = usuarios.porEmail(email.toLowerCase().trim())
            .orElseThrow(() -> new NotFoundException("Email no registrado"));

        // 1) generar y guardar token
        var t = new ResetTokenDbEntity();
        t.setId(UUID.randomUUID()); // token
        t.setUserId(u.getId());
        t.setExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES));
        t.setUsed(false);
        tokens.save(t);

        // 2) construir link para el frontend
        String token = t.getId().toString();
        String link  = frontendBaseUrl + "reset-password/" + token;

        // 3) publicar evento para notificaciones
        try {
            eventPublisher.passwordResetRequested(
                new PasswordResetRequestedEvent(
                    u.getId().toString(),
                    u.getEmail(),
                    u.getNombre(),
                    token,
                    link,
                    t.getExpiresAt()
                )
            );
            log.info("Evento 'user.password.reset.requested.v1' publicado para {}", u.getEmail());
        } catch (Exception ex) {
            log.error("Error publicando evento de reset password", ex);
        }

        // 4) respuesta
        return devReturnToken ? token : "ok";
    }
}