package com.user.microservice.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.microservice.user.application.inputports.ActualizarUsuarioInputPort;
import com.user.microservice.user.application.inputports.CambiarEstadoUsuarioInputPort;
import com.user.microservice.user.application.inputports.ListarUsuariosInputPort;
import com.user.microservice.user.application.inputports.LoginInputPort;
import com.user.microservice.user.application.inputports.ObtenerUsuarioInputPort;
import com.user.microservice.user.application.inputports.RegistrarUsuarioInputPort;
import com.user.microservice.user.application.inputports.ResetPasswordInputPort;
import com.user.microservice.user.application.inputports.SolicitarResetPasswordInputPort;
import com.user.microservice.user.application.outputports.UserEventPublisher;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.application.usecases.ActualizarUsuarioUseCase;
import com.user.microservice.user.application.usecases.CambiarEstadoUsuarioUseCase;
import com.user.microservice.user.application.usecases.ListarUsuariosUseCase;
import com.user.microservice.user.application.usecases.LoginUseCase;
import com.user.microservice.user.application.usecases.ObtenerUsuarioUseCase;
import com.user.microservice.user.application.usecases.RegistrarUsuarioUseCase;
import com.user.microservice.user.application.usecases.ResetPasswordUseCase;
import com.user.microservice.user.application.usecases.SolicitarResetPasswordUseCase;
import com.user.microservice.user.infrastructure.outputadapters.persistence.repository.ResetTokenJpaRepository;
import com.user.microservice.user.infrastructure.security.JwtService;

@Configuration
public class UserBeansConfig {

    @Bean
    public RegistrarUsuarioInputPort registrarUsuario(UsuarioRepositorioPort repo, PasswordEncoder encoder, UserEventPublisher publisher) {
        return new RegistrarUsuarioUseCase(repo, encoder, publisher);
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

    /*@Bean
    public SolicitarResetPasswordInputPort solicitarReset(UsuarioRepositorioPort repo, ResetTokenJpaRepository tokens) {
        return new SolicitarResetPasswordUseCase(repo, tokens);
    }*/

    @Bean
    public ResetPasswordInputPort resetPassword(UsuarioRepositorioPort repo, ResetTokenJpaRepository tokens, PasswordEncoder encoder) {
        return new ResetPasswordUseCase(repo, tokens, encoder);
    }
}
