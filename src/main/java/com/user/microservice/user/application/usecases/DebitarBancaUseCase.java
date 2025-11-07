package com.user.microservice.user.application.usecases;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.user.microservice.common.errors.BadRequestException;
import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.DebitarBancaInputPort;
import com.user.microservice.user.application.outputports.UsuarioRepositorioPort;
import com.user.microservice.user.domain.Usuario;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DebitarBancaUseCase implements DebitarBancaInputPort {
    private final UsuarioRepositorioPort repo;
    
    @Transactional
    @Override
    public Usuario debitar(UUID userId, BigDecimal monto, String motivo) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new BadRequestException("Monto inválido");
        var user = repo.porId(userId).orElseThrow(() -> new NotFoundException("Usuario no existe"));
        if (((Usuario) user).getBancaVirtual().compareTo(monto) < 0)
            throw new BadRequestException("Fondos insuficientes");
        var nuevo = ((Usuario) user).toBuilder()
                .bancaVirtual(((Usuario) user).getBancaVirtual().subtract(monto))
                .build();
        return repo.guardar(nuevo);
    }
}
