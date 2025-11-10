package edu.tudai.arq.viajeservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "monopatin-service", url = "http://localhost:8085")
public interface MonopatinFeignClient {

    @GetMapping("/api/v1/monopatines/{id}")
    ResponseEntity<MonopatinResponse> getMonopatinById(@PathVariable Long id);

    @PutMapping("/api/v1/monopatines/{id}")
    ResponseEntity<MonopatinResponse> updateMonopatin(@PathVariable Long id, @RequestBody MonopatinUpdate update);

    record MonopatinResponse(
            Long id,
            String estado,
            Integer latitud,
            Integer longitud,
            Double kilometrosTotales,
            Long tiempoUsoTotal,
            Long tiempoPausas
    ) {}

    record MonopatinUpdate(
            String estado,
            Integer latitud,
            Integer longitud,
            Double kilometrosTotales,
            Long tiempoUsoTotal,
            Long tiempoPausas
    ) {}
}

