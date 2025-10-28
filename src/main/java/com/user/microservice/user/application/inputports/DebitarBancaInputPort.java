package com.user.microservice.user.application.inputports;

import java.math.BigDecimal;
import java.util.UUID;

import com.user.microservice.user.domain.Usuario;

public interface DebitarBancaInputPort {
    Usuario debitar(UUID userId, BigDecimal monto, String motivo);
}
