package com.user.microservice.user.infrastructure.inputadapters.rest.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(@Email
        @NotBlank
        String email) {

}
