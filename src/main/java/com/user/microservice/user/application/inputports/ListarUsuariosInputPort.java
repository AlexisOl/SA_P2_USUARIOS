// user/application/inputports/*.java
package com.user.microservice.user.application.inputports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.domain.Usuario;


public interface ListarUsuariosInputPort {

    Page<Usuario> listar(String q, Rol rol, Boolean enabled, Pageable p);
}


