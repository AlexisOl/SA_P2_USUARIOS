package com.user.microservice.user.infrastructure.inputadapters.rest.dto;


import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record ResetRequest(@NotBlank
        String token, @Size(min = 6)
        String newPassword) {

}

