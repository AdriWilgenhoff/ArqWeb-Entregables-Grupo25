package edu.tudai.arq.mantenimientoservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "monopatin-service", url = "http://localhost:8085")
public interface MonopatinFeignClient {

    @GetMapping("/api/v1/monopatines")
    ResponseEntity<List<MonopatinResponse>> getAllMonopatines();

    @GetMapping("/api/v1/monopatines/{id}")
    ResponseEntity<MonopatinResponse> getMonopatinById(@PathVariable("id") Long id);

    @PutMapping("/api/v1/monopatines/{id}/estado/{nuevoEstado}")
    ResponseEntity<MonopatinResponse> cambiarEstado(
            @PathVariable("id") Long id,
            @PathVariable("nuevoEstado") String nuevoEstado
    );

    record MonopatinResponse(
            Long id,
            String estado,
            Double latitud,
            Double longitud,
            Double kilometrosTotales,
            Long tiempoUsoTotal
    ) {}
}

