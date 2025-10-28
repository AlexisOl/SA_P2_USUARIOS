/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.microservice.user.infrastructure.inputadapters.rest.dto;

import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@Email
        @NotBlank
        String email, @NotBlank
        String password) {

}


