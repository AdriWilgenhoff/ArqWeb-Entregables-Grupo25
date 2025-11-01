package edu.tudai.arq.adminservice.feignclient;

import edu.tudai.arq.adminservice.model.MantenimientoModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mantenimiento-service", url = "http://localhost:8084")
public interface MantenimientoFeignClient {

    @GetMapping("/api/v1/mantenimientos")
    ResponseEntity<List<MantenimientoModel>> getAllMantenimientos();

    @GetMapping("/api/v1/mantenimientos/activos")
    ResponseEntity<List<MantenimientoModel>> getMantenimientosActivos();

    @PostMapping("/api/v1/mantenimientos")
    ResponseEntity<MantenimientoModel> registrarMantenimiento(@RequestBody MantenimientoModel mantenimiento);

    @PutMapping("/api/v1/mantenimientos/{id}/finalizar")
    ResponseEntity<MantenimientoModel> finalizarMantenimiento(@PathVariable("id") Long id);
}

