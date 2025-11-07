package com.user.microservice.user.infrastructure.Eventos;

import com.example.comun.DTO.BloqueoAnuncios.CreditoUsuarioBloqueo;
import com.example.comun.DTO.BloqueoAnuncios.CreditoUsuarioBloqueoEspecifico;
import com.example.comun.DTO.FacturaAnuncio.CambioEstadoAnuncioDTO;
import com.example.comun.DTO.FacturaAnuncio.DebitoUsuarioAnuncio;
import com.example.comun.DTO.FacturaAnuncio.DiasDescuentoAnunciosBloqueados;
import com.example.comun.DTO.FacturaAnuncio.RespuestaFacturaAnuncioCreadaDTO;
import com.example.comun.DTO.FacturaBoleto.CobroCineDTO;
import com.example.comun.DTO.FacturaBoleto.DebitoCine.DebitoCineDTO;
import com.example.comun.DTO.FacturaBoleto.DebitoUsuario;
import com.example.comun.DTO.FacturaBoleto.RespuestaFacturaBoletoCreadoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.microservice.user.application.inputports.AcreditarBancaInputPort;
import com.user.microservice.user.application.inputports.DebitarBancaInputPort;
import com.user.microservice.user.domain.Usuario;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final AcreditarBancaInputPort acreditarBancaInputPort;
    //aca necesitamos saber los cines


    // extraccion de dinero al usuario
    private static final Logger log = LoggerFactory.getLogger(UsuariosKafkaAdaptador.class);

    @KafkaListener(topics = "debito-usuario-boleto", groupId = "usuarios-group")
    @Transactional
    public void debitarDinero(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {
        DebitoUsuario evento = objectMapper.readValue(mensaje, DebitoUsuario.class);

        log.info("DEBITO RECIBIDO | User: {} | Monto: Q{} | CorrID: {}",
                evento.getUserId(), evento.getMonto(), correlationId);

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
            log.info("DÉBITO EXITOSO | User: {} | Saldo: {}", evento.getUserId(), usuarioDebitado.getBancaVirtual());

        } catch (Exception e) {
            motivoFallo = e.getMessage();
            log.error("ERROR AL DEBITAR | User: {} | Motivo: {}", evento.getUserId(), motivoFallo, e);

            // Enviar a cine
            DebitoCineDTO debito = new DebitoCineDTO();
            debito.setMonto(evento.getMonto());
            debito.setCorrelationId(evento.getCorrelationId());
            debito.setIdCine(evento.getIdCine());
            String jsonDebitar = objectMapper.writeValueAsString(debito);

            Message<String> mensajeDebito = MessageBuilder
                    .withPayload(jsonDebitar)
                    .setHeader(KafkaHeaders.TOPIC, "debito-cine")
                    .setHeader(KafkaHeaders.CORRELATION_ID, evento.getCorrelationId())
                    .build();
            kafkaTemplate.send(mensajeDebito);
            log.info("ENVIADO A CINE | Monto: Q{} | Cine: {}", evento.getMonto(), evento.getIdCine());
        }

        RespuestaFacturaBoletoCreadoDTO respuesta = new RespuestaFacturaBoletoCreadoDTO();
        respuesta.setMotivoFallo(motivoFallo);
        respuesta.setCorrelationId(evento.getCorrelationId());
        respuesta.setExito(exito);
        respuesta.setVentaId(evento.getVentaId());
        respuesta.setFactura(evento.getFactura());

        String json = objectMapper.writeValueAsString(respuesta);
        String topicVenta = exito ? "venta-actualizada" : "venta-fallido";
        String topicFactura = exito ? "factura-actualizada" : "factura-fallido";

        log.info("RESPUESTA | Exito: {} | Enviando a {}/{}", exito, topicVenta, topicFactura);
        kafkaTemplate.send(topicVenta, json);
        kafkaTemplate.send(topicFactura, json);
    }

    @KafkaListener(topics = "debito-usuario-snacks", groupId = "usuarios-group")
    @Transactional
    public void debitarDineroSnacks(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {
        DebitoUsuario evento = objectMapper.readValue(mensaje, DebitoUsuario.class);
        System.out.println("Intentando debitar dinero anucnio: " + evento.getMonto() + " del usuario: " + evento.getUserId());

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


    @KafkaListener(topics = "acreditacion-usuario-anuncio-facturacion", groupId = "usuarios-group")
    @Transactional
    public void debitarDineroAnuncio(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {

        DebitoUsuarioAnuncio evento = objectMapper.readValue(mensaje, DebitoUsuarioAnuncio.class);
        System.out.println("Intentando debitar dinero: " + evento.getMonto() + " del usuario: " + evento.getUserId());

        boolean exito = false;
        String motivoFallo = "Error desconocido";
        Double montoUsuario = evento.getMonto();
        try {
            Usuario usuarioDebitado = debitarBancaInputPort.debitar(
                    evento.getUserId(),
                    BigDecimal.valueOf(evento.getMonto()),
                    evento.getMotivo()
            );
            exito = true;
            motivoFallo = "Débito exitoso. Nuevo saldo: " + usuarioDebitado.getBancaVirtual();

        } catch (Exception e) {
            // aca falla entonces quitar dinero
            for (DiasDescuentoAnunciosBloqueados cines: evento.getDineroCines()) {
                System.out.println(cines.getPrecio()+ " -- " +cines.getCine() );
                String debitarCine="debito-cine";
                if(!cines.isEstado()) {
                    // fue debito entonces se acredita
                    debitarCine="credito-cine";
                }
                    // aca fue ingreso entonces se debita
                    DebitoCineDTO debito = new DebitoCineDTO();
                    debito.setMonto(cines.getPrecio());
                    debito.setCorrelationId(evento.getCorrelationId());
                    debito.setIdCine(cines.getCine());
                    String jsonDebitar = objectMapper.writeValueAsString(debito);

                    Message<String> mensajeDebito = MessageBuilder
                            .withPayload(jsonDebitar)
                            .setHeader(KafkaHeaders.TOPIC, debitarCine)
                            .setHeader(KafkaHeaders.CORRELATION_ID, evento.getCorrelationId())
                            .build();
                    kafkaTemplate.send(mensajeDebito);

            }
        }

        // aca regresa info, en caso de estar correcto o no

        RespuestaFacturaAnuncioCreadaDTO respuesta = new RespuestaFacturaAnuncioCreadaDTO();
        respuesta.setMotivoFallo(motivoFallo);
        respuesta.setCorrelationId(evento.getCorrelationId());
        respuesta.setExito(exito);
        respuesta.setFactura(evento.getFactura());
        respuesta.setDineroCines(evento.getDineroCines());
        respuesta.setAnuncioId(evento.getIdAnuncio());
        respuesta.setMonto(evento.getMonto());

        String json = objectMapper.writeValueAsString(respuesta);

        // para el cambio del anuncip


        CambioEstadoAnuncioDTO respuestaAnuncio = new CambioEstadoAnuncioDTO();
        respuestaAnuncio.setMotivoFallo(motivoFallo);
        respuestaAnuncio.setCorrelationId(evento.getCorrelationId());
        respuestaAnuncio.setAnuncioId(evento.getIdAnuncio());

        String jsonAnuncio = objectMapper.writeValueAsString(respuestaAnuncio);

        //si todo bien ver que enviar
        //generar factura

        String topicFactura = exito ? "creacion-factura-anuncio-especifica" : "creacion-factura-anuncio-fallido";
        String topicAnuncio = exito ? "cambio-estado-exitoso-anuncio" : "cambio-estado-fallido-anuncio";
        System.out.println(exito+" tuvo exito?");
        kafkaTemplate.send(topicAnuncio, jsonAnuncio);
        kafkaTemplate.send(topicFactura, json);
    }




    @KafkaListener(topics = "acreditacion-usuario-bloqueo", groupId = "usuarios-group")
    @Transactional
    public void acreditarDinero(@Payload String mensaje, @Header(KafkaHeaders.CORRELATION_ID) String correlationId) throws Exception {
        CreditoUsuarioBloqueo evento = objectMapper.readValue(mensaje, CreditoUsuarioBloqueo.class);
        try {
            for (CreditoUsuarioBloqueoEspecifico listado: evento.getListado()) {
                Usuario usuarioAcreditado = acreditarBancaInputPort.acreditar(
                        listado.getUserId(),
                        BigDecimal.valueOf(listado.getMonto()),
                        "Pago de devolucion por bloqueo de anuncios en el cine"
                );
            }


        } catch (Exception e) {
            System.err.println("Error al acredirar usuario " );
        }


    }


}
