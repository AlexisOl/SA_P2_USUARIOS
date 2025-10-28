package com.user.microservice.user.infrastructure.inputadapters.rest.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public record UsuarioResponse(UUID id, String nombre, String email, String dpi,
        Rol rol, boolean enabled, BigDecimal bancaVirtual, Instant createdAt) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getDpi(),
                u.getRol(), u.isEnabled(), u.getBancaVirtual(), u.getCreatedAt());
    }
}