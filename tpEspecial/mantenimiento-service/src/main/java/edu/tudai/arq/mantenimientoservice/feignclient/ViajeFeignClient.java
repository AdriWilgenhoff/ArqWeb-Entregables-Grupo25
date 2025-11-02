package edu.tudai.arq.mantenimientoservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "viaje-service", url = "http://localhost:8082")
public interface ViajeFeignClient {

    @GetMapping("/api/v1/viajes/monopatin/{idMonopatin}")
    ResponseEntity<List<ViajeResponse>> getViajesPorMonopatin(@PathVariable("idMonopatin") Long idMonopatin);

    record ViajeResponse(
            Long id,
            Long idCuenta,
            Long idUsuario,
            Long idMonopatin,
            String fechaHoraInicio,
            String fechaHoraFin,
            Long idParadaInicio,
            Long idParadaFin,
            Double kilometrosRecorridos,
            String estado,
            Double costoTotal
    ) {}
}

