package edu.tudai.arq.viajeservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "usuario-service", url = "http://localhost:8081")
public interface CuentaFeignClient {

    record DescontarSaldoRequest(
            Double monto
    ) {}

    record CuentaResponse(
            Long id,
            String numeroIdentificatorio,
            String fechaAlta,
            Double saldo,
            Boolean habilitada,
            String idCuentaMercadoPago
    ) {}

    @GetMapping("/api/v1/cuentas/{id}")
    ResponseEntity<CuentaResponse> getCuentaById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/cuentas/{id}/descontar-saldo")
    ResponseEntity<CuentaResponse> descontarSaldo(@PathVariable("id") Long id,
                                                   @RequestBody DescontarSaldoRequest request);
}

