// user/infrastructure/config/UserBeansConfig.java
package com.user.microservice.user.infrastructure.config;

import com.user.microservice.user.application.inputports.*;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.application.usecases.*;
import com.user.microservice.user.infrastructure.outputadapters.persistence.repository.ResetTokenJpaRepository;
import com.user.microservice.user.infrastructure.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserBeansConfig {

    @Bean
    public RegistrarUsuarioInputPort registrarUsuario(UsuarioRepositorioPort repo, PasswordEncoder encoder) {
        return new RegistrarUsuarioUseCase(repo, encoder);
    }

    @Bean
    public LoginInputPort login(UsuarioRepositorioPort repo, PasswordEncoder encoder, JwtService jwt) {
        return new LoginUseCase(repo, encoder, jwt);
    }

    @Bean
    public ListarUsuariosInputPort listarUsuarios(UsuarioRepositorioPort repo) {
        return new ListarUsuariosUseCase(repo);
    }

    @Bean
    public ObtenerUsuarioInputPort obtenerUsuario(UsuarioRepositorioPort repo) {
        return new ObtenerUsuarioUseCase(repo);
    }

    @Bean
    public ActualizarUsuarioInputPort actualizarUsuario(UsuarioRepositorioPort repo) {
        return new ActualizarUsuarioUseCase(repo);
    }

    @Bean
    public CambiarEstadoUsuarioInputPort cambiarEstado(UsuarioRepositorioPort repo) {
        return new CambiarEstadoUsuarioUseCase(repo);
    }

    @Bean
    public SolicitarResetPasswordInputPort solicitarReset(UsuarioRepositorioPort repo, ResetTokenJpaRepository tokens) {
        return new SolicitarResetPasswordUseCase(repo, tokens);
    }

    @Bean
    public ResetPasswordInputPort resetPassword(UsuarioRepositorioPort repo, ResetTokenJpaRepository tokens, PasswordEncoder encoder) {
        return new ResetPasswordUseCase(repo, tokens, encoder);
    }
}
