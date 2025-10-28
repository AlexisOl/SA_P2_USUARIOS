package com.user.microservice.user.infrastructure.inputadapters.rest.dto;

import java.math.BigDecimal;

import com.user.microservice.user.domain.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarRequest(
        @NotBlank
        String nombre,
        @Email
        @NotBlank
        String email,
        @Size(min = 6)
        String password,
        @NotBlank
        String dpi,
        Rol rol,
        BigDecimal bancaVirtual
        ) {

}

