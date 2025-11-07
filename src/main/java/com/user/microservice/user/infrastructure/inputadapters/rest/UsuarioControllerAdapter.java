package com.user.microservice.user.infrastructure.inputadapters.rest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.microservice.common.errors.NotFoundException;
import com.user.microservice.user.application.inputports.AcreditarBancaInputPort;
import com.user.microservice.user.application.inputports.ActualizarUsuarioInputPort;
import com.user.microservice.user.application.inputports.CambiarEstadoUsuarioInputPort;
import com.user.microservice.user.application.inputports.DebitarBancaInputPort;
import com.user.microservice.user.application.inputports.ListarUsuariosInputPort;
import com.user.microservice.user.application.inputports.ObtenerUsuarioInputPort;
import com.user.microservice.user.domain.Rol;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.ActualizarRequest;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.MontoRequest;
import com.user.microservice.user.infrastructure.inputadapters.rest.dto.UsuarioResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController @RequestMapping("/v1/users") @RequiredArgsConstructor
public class UsuarioControllerAdapter {
  private final ListarUsuariosInputPort listar;
  private final ObtenerUsuarioInputPort obtener;
  private final ActualizarUsuarioInputPort actualizar;
  private final CambiarEstadoUsuarioInputPort cambiarEstado;
  private final AcreditarBancaInputPort acreditarBanca; 
    private final DebitarBancaInputPort debitarBanca; 

  @Operation(summary="Obtener usuario por id")
  @GetMapping("/{id}")
  public UsuarioResponse porId(@PathVariable UUID id){
    var u = obtener.porId(id).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    return UsuarioResponse.from(u);
  }

  @Operation(summary="Listar usuarios (solo arreglo)")
  @GetMapping
  public List<UsuarioResponse> list(@RequestParam(required=false) String q,
                                    @RequestParam(required=false) Rol rol,
                                    @RequestParam(required=false) Boolean enabled,
                                    @ParameterObject Pageable pageable){
    return listar.listar(q, rol, enabled, pageable).map(UsuarioResponse::from).getContent();
  }

  @Operation(summary="Actualizar nombre/rol/enabled")
  @PutMapping("/{id}")
  public UsuarioResponse update(@PathVariable UUID id, @RequestBody ActualizarRequest req){
    var u = actualizar.actualizar(id, req.nombre(), req.email(), req.rol(), req.dpi(), req.enabled());
    return UsuarioResponse.from(u);
  }

  @Operation(summary="Habilitar/Deshabilitar")
  @PostMapping("/{id}/enabled")
  public UsuarioResponse setEnabled(@PathVariable UUID id, @RequestParam boolean value){
    return UsuarioResponse.from(cambiarEstado.habilitar(id, value));
  }

  @Operation(summary = "Acreditar saldo en bancaVirtual")
    @PatchMapping("/{id}/banca/acreditar")
    public UsuarioResponse acreditar(@PathVariable UUID id, @RequestBody MontoRequest req) {
        var u = acreditarBanca.acreditar(id, req.getMonto(), req.getMotivo());
        return UsuarioResponse.from(u);
    }

    @Operation(summary = "Debitar saldo en bancaVirtual")
    @PatchMapping("/{id}/banca/debitar")
    public UsuarioResponse debitar(@PathVariable UUID id, @RequestBody MontoRequest req) {
        var u = debitarBanca.debitar(id, req.getMonto(), req.getMotivo());
        return UsuarioResponse.from(u);
    }

    @Operation(summary = "Consultar saldo de bancaVirtual")
    @GetMapping("/{id}/banca")
    public Map<String, Object> saldo(@PathVariable UUID id) {
        var u = obtener.porId(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        return Map.of(
                "userId", id,
                "bancaVirtual", u.getBancaVirtual()
        );
    }
    
}