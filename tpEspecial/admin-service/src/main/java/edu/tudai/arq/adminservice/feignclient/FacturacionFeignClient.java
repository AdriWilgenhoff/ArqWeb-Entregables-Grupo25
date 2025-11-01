package edu.tudai.arq.adminservice.feignclient;

import edu.tudai.arq.adminservice.model.TarifaModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "facturacion-service", url = "http://localhost:8086")
public interface FacturacionFeignClient {

    @GetMapping("/api/v1/tarifas")
    ResponseEntity<List<TarifaModel>> getAllTarifas();

    @GetMapping("/api/v1/tarifas/activas")
    ResponseEntity<List<TarifaModel>> getTarifasActivas();

    @PostMapping("/api/v1/tarifas")
    ResponseEntity<TarifaModel> createTarifa(@RequestBody TarifaModel tarifa);

    @PutMapping("/api/v1/tarifas/{id}")
    ResponseEntity<TarifaModel> updateTarifa(@PathVariable("id") Long id, @RequestBody TarifaModel tarifa);

    @PutMapping("/api/v1/tarifas/{id}/desactivar")
    ResponseEntity<Void> desactivarTarifa(@PathVariable("id") Long id, @RequestParam String fechaFin);

    @GetMapping("/api/v1/facturaciones/total-facturado")
    ResponseEntity<Double> getTotalFacturado(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta);
}

