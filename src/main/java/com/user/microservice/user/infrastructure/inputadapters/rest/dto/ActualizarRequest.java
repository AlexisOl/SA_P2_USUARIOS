package com.user.microservice.user.infrastructure.inputadapters.rest.dto;


import com.user.microservice.user.domain.Rol;

public record ActualizarRequest(String nombre, Rol rol, Boolean enabled) {

}

