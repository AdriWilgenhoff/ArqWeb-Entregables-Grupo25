package edu.tudai.arq.facturacionservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cuenta-service", url = "http://localhost:8086")
public interface CuentaFeignClient {

    record CuentaResponse(
            Long id,
            String numeroIdentificatorio,
            String fechaAlta,
            Double saldo,
            Boolean habilitada,
            String idCuentaMercadoPago,
            String tipoCuenta,
            Double kilometrosDisponibles,
            String fechaUltimoPagoPremium,
            Boolean necesitaRenovacion
    ) {}

    @GetMapping("/api/v1/cuentas/{id}")
    ResponseEntity<CuentaResponse> getCuentaById(@PathVariable Long id);

    @PostMapping("/api/v1/cuentas/{id}/usar-kilometros-gratis")
    ResponseEntity<Double> usarKilometrosGratis(
            @PathVariable Long id,
            @RequestParam Double kilometros
    );
}

