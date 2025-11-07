package com.user.microservice.user.infrastructure.inputadapters.rest.dto;


import com.user.microservice.user.domain.Rol;

public record ActualizarRequest(String nombre, String email, Rol rol, String dpi, Boolean enabled) {

}

