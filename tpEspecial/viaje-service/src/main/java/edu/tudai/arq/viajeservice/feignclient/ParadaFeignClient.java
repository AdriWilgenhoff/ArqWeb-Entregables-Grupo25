package edu.tudai.arq.viajeservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "parada-service", url = "http://localhost:8083")
public interface ParadaFeignClient {

    @GetMapping("/api/v1/paradas/cercanas")
    ResponseEntity<List<ParadaResponse>> findParadasCercanas(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(required = false, defaultValue = "0.05") Double radioKm
    );

    record ParadaResponse(
            Long id,
            String nombre,
            Double latitud,
            Double longitud,
            Integer capacidad,
            Integer monopatinesDisponibles,
            Double porcentajeOcupacion,
            Boolean tieneEspacio
    ) {}
}

