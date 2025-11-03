package edu.tudai.arq.adminservice.feignclient;

import edu.tudai.arq.adminservice.model.CuentaModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "usuario-service", url = "http://localhost:8081")
public interface UsuarioFeignClient {

    @GetMapping("/api/v1/cuentas")
    ResponseEntity<List<CuentaModel>> getAllCuentas();

    @GetMapping("/api/v1/cuentas/{id}")
    ResponseEntity<CuentaModel> getCuentaById(@PathVariable("id") Long id);

    @PutMapping("/api/v1/cuentas/{id}/anular")
    ResponseEntity<Void> anularCuenta(@PathVariable("id") Long id);

    @PutMapping("/api/v1/cuentas/{id}/habilitar")
    ResponseEntity<Void> habilitarCuenta(@PathVariable("id") Long id);
}

