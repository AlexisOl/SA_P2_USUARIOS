package com.user.microservice.user.infrastructure.Eventos;

import com.example.comun.DTO.FacturaBoleto.CobroCineDTO;
import com.example.comun.DTO.FacturaBoleto.DebitoCine.DebitoCineDTO;
import com.example.comun.DTO.FacturaBoleto.DebitoUsuario;
import com.example.comun.DTO.FacturaBoleto.RespuestaFacturaBoletoCreadoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.microservice.user.application.inputports.DebitarBancaInputPort;
import com.user.microservice.user.domain.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UsuariosKafkaAdaptador {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final DebitarBancaInputPort debitarBancaInputPort;

    // extraccion de dinero al usuario
    @KafkaListener(topics = "debito-usuario", groupId = "usuarios-group")
    @Transactional
    public void debitarDinero(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {
        DebitoUsuario evento = objectMapper.readValue(mensaje, DebitoUsuario.class);
        System.out.println("Intentando debitar dinero: " + evento.getMonto() + " del usuario: " + evento.getUserId());

        boolean exito = false;
        String motivoFallo = "Error desconocido";

        try {
            Usuario usuarioDebitado = debitarBancaInputPort.debitar(
                    evento.getUserId(),
                    BigDecimal.valueOf(evento.getMonto()),
                    evento.getMotivo()
            );
            exito = true;
            motivoFallo = "Débito exitoso. Nuevo saldo: " + usuarioDebitado.getBancaVirtual();

        } catch (Exception e) {
            motivoFallo = e.getMessage();
            System.err.println("Error al debitar usuario " + evento.getUserId() + ": " + motivoFallo);
            System.out.println("se debita al cine");
            DebitoCineDTO debito = new DebitoCineDTO();
            debito.setMonto(evento.getMonto());
            debito.setCorrelationId(evento.getCorrelationId());
            debito.setIdCine(evento.getIdCine());
            String jsonDebitar = objectMapper.writeValueAsString(debito);
            String debitarCine="debito-cine";

            Message<String> mensajeDebito = MessageBuilder
                    .withPayload(jsonDebitar)
                    .setHeader(KafkaHeaders.TOPIC, debitarCine)
                    .setHeader(KafkaHeaders.CORRELATION_ID, evento.getCorrelationId())
                    .build();
            kafkaTemplate.send(mensajeDebito);
        }

        RespuestaFacturaBoletoCreadoDTO respuesta = new RespuestaFacturaBoletoCreadoDTO();
        respuesta.setMotivoFallo(motivoFallo);
        respuesta.setCorrelationId(evento.getCorrelationId());
        respuesta.setExito(exito);
        respuesta.setVentaId(evento.getVentaId());
        respuesta.setFactura(evento.getFactura());

        String json = objectMapper.writeValueAsString(respuesta);

        String debitarCine="";
        String topicVenta = exito ? "venta-actualizada" : "venta-fallido";
        String topicFactura = exito ? "factura-actualizada" : "factura-fallido";

        System.out.println(exito+" tuvo exito?");
        kafkaTemplate.send(topicVenta, json);
        kafkaTemplate.send(topicFactura, json);
    }


    @KafkaListener(topics = "debito-usuario-snacks", groupId = "usuarios-group")
    @Transactional
    public void debitarDineroSnacks(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {
        DebitoUsuario evento = objectMapper.readValue(mensaje, DebitoUsuario.class);
        System.out.println("Intentando debitar dinero: " + evento.getMonto() + " del usuario: " + evento.getUserId());

        boolean exito = false;
        String motivoFallo = "Error desconocido";

        try {
            Usuario usuarioDebitado = debitarBancaInputPort.debitar(
                    evento.getUserId(),
                    BigDecimal.valueOf(evento.getMonto()),
                    evento.getMotivo()
            );
            exito = true;
            motivoFallo = "Débito exitoso. Nuevo saldo: " + usuarioDebitado.getBancaVirtual();

        } catch (Exception e) {
            motivoFallo = e.getMessage();
            System.err.println("Error al debitar usuario " + evento.getUserId() + ": " + motivoFallo);
        }

        RespuestaFacturaBoletoCreadoDTO respuesta = new RespuestaFacturaBoletoCreadoDTO();
        respuesta.setMotivoFallo(motivoFallo);
        respuesta.setCorrelationId(evento.getCorrelationId());
        respuesta.setExito(exito);
        respuesta.setVentaId(evento.getVentaId());
        respuesta.setFactura(evento.getFactura());

        String json = objectMapper.writeValueAsString(respuesta);

       // String topicVenta = exito ? "venta-actualizada" : "venta-fallido";
        String topicFactura = exito ? "factura-snacks-actualizada" : "factura-snacks-fallido";
        System.out.println(exito+" tuvo exito?");
        //kafkaTemplate.send(topicVenta, json);
        kafkaTemplate.send(topicFactura, json);
    }




    @KafkaListener(topics = "acreditacion-usuario", groupId = "usuarios-group")
    @Transactional
    public void acreditarDinero(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {
        DebitoUsuario evento = objectMapper.readValue(mensaje, DebitoUsuario.class);

    }


}
