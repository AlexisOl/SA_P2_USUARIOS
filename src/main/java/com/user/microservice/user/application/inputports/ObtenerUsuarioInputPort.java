// user/application/inputports/*.java
package com.user.microservice.user.application.inputports;

import java.util.Optional;
import java.util.UUID;

import com.user.microservice.user.domain.Usuario;

public interface ObtenerUsuarioInputPort {

    Optional<Usuario> porId(UUID id);
}
