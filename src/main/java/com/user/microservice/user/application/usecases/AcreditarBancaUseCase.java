package com.user.microservice.user.application.usecases;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.user.microservice.common.errors.BadRequestException;
import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.AcreditarBancaInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AcreditarBancaUseCase implements AcreditarBancaInputPort {
    private final UsuarioRepositorioPort repo;
    
    @Transactional
    @Override
    public Usuario acreditar(UUID userId, BigDecimal monto, String motivo) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("Monto inválido");
        var user = repo.porId(userId).orElseThrow(() -> new NotFoundException("Usuario no existe"));
        var nuevo = user.toBuilder()
                .bancaVirtual(user.getBancaVirtual().add(monto))
                .build();
        return repo.guardar(nuevo);
    }
}

