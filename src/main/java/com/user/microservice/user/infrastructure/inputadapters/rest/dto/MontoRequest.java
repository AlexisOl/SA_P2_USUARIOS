package com.user.microservice.user.infrastructure.inputadapters.rest.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MontoRequest {
    private BigDecimal monto;
    private String motivo;
}
