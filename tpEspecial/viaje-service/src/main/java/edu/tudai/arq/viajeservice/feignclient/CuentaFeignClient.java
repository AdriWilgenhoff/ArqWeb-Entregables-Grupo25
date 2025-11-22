package edu.tudai.arq.viajeservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "usuario-service", url = "http://localhost:8086")
public interface CuentaFeignClient {

    record DescontarSaldoRequest(
            Double monto
    ) {}

    record DescontarKilometrosRequest(
            Double kilometros
    ) {}

    record ResultadoDescuentoKm(
            Double kilometrosDescontados,
            Double kilometrosACobrar
    ) {}

    record CuentaResponse(
            Long id,
            String numeroIdentificatorio,
            String fechaAlta,
            Double saldo,
            Boolean habilitada,
            String idCuentaMercadoPago,
            String tipoCuenta,
            Double kilometrosDisponibles
    ) {}

    @GetMapping("/api/v1/cuentas/{id}")
    ResponseEntity<CuentaResponse> getCuentaById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/cuentas/{id}/descontar-saldo")
    ResponseEntity<CuentaResponse> descontarSaldo(@PathVariable("id") Long id,
                                                   @RequestBody DescontarSaldoRequest request);

    @PostMapping("/api/v1/cuentas/{id}/descontar-kilometros")
    ResponseEntity<ResultadoDescuentoKm> descontarKilometros(@PathVariable("id") Long id,
                                                             @RequestBody DescontarKilometrosRequest request);

    @GetMapping("/api/v1/usuarios/{idUsuario}/cuentas")
    ResponseEntity<List<CuentaResponse>> getCuentasByUsuario(@PathVariable("idUsuario") Long idUsuario);
}

