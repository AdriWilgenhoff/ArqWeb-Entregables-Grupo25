package edu.tudai.arq.viajeservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-service", url = "http://localhost:8081")
public interface UsuarioFeignClient {

    @GetMapping("/api/v1/cuentas/{id}")
    ResponseEntity<CuentaResponse> getCuentaById(@PathVariable Long id);

    record CuentaResponse(
            Long id,
            String numeroCuenta,
            String fechaAlta,
            Double saldo,
            Boolean habilitada
    ) {}
}

