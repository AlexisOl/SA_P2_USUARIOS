package com.user.microservice.user.infrastructure.inputadapters.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.microservice.user.application.inputports.LoginInputPort;
import com.user.microservice.user.application.inputports.RegistrarUsuarioInputPort;
import com.user.microservice.user.application.inputports.ResetPasswordInputPort;
import com.user.microservice.user.application.inputports.SolicitarResetPasswordInputPort;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.EmailRequest;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.LoginRequest;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.RegistrarRequest;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.ResetRequest;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.TokenResponse;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.UsuarioResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthControllerAdapter {

    
    private final RegistrarUsuarioInputPort registrar;
    private final LoginInputPort login;
    private final SolicitarResetPasswordInputPort solicitarReset;
    private final ResetPasswordInputPort reset;

    @Operation(summary = "Registro")
    @PostMapping("/register")
    public UsuarioResponse register(@Valid @RequestBody RegistrarRequest req) {
        var u = registrar.registrar(req.nombre(), req.email(), req.password(), req.dpi(), req.rol());
        return UsuarioResponse.from(u);
    }

    @Operation(summary = "Login (retorna JWT)")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return new TokenResponse(login.login(req.email(), req.password()));
    }

    @Operation(summary = "Solicitar reset password (dev: devuelve token)")
    @PostMapping("/password/forgot")
    public TokenResponse forgot(@Valid @RequestBody EmailRequest req) {
        return new TokenResponse(solicitarReset.solicitar(req.email()));
    }

    @Operation(summary = "Reset password con token")
    @PostMapping("/password/reset")
    public void reset(@Valid @RequestBody ResetRequest req) {
        reset.reset(req.token(), req.newPassword());
    }
}
