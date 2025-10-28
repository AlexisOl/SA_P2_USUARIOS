// user/infrastructure/inputadapters/rest/AuthControllerAdapter.java
package com.user.microservice.user.infrastructure.inputadapters.rest;

import com.user.microservice.user.application.inputports.*;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
