package com.user.microservice.user.application.inputports;

import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;

public interface RegistrarUsuarioInputPort {

    Usuario registrar(String nombre, String email, String passwordPlano, String dpi, Rol rol);
}
