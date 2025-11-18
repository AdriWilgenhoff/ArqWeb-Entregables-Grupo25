package edu.tudai.arq.viajeservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "facturacion-service", url = "http://localhost:8088")
public interface FacturacionFeignClient {

    record FacturacionCreateRequest(
            Long idViaje,
            Long idCuenta,
            Long tiempoTotal,
            Long tiempoSinPausas,
            Long tiempoPausaNormal,
            Long tiempoPausaExtendida,
            Double kilometrosRecorridos
    ) {}

    record FacturacionResponse(
            Long id,
            Long idViaje,
            Long idCuenta,
            String fecha,
            Double montoTotal,
            Long tiempoTotal,
            Long tiempoPausado,
            Long tiempoSinPausas,
            Long idTarifaNormal,
            Long idTarifaPausa,
            Long idTarifaExtendida
    ) {}

    @PostMapping("/api/v1/facturaciones")
    ResponseEntity<FacturacionResponse> crearFacturacion(@RequestBody FacturacionCreateRequest request);
}

