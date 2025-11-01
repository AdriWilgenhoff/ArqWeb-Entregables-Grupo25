package edu.tudai.arq.adminservice.feignclient;

import edu.tudai.arq.adminservice.model.ViajeModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "viaje-service", url = "http://localhost:8087")
public interface ViajeFeignClient {

    @GetMapping("/api/v1/viajes")
    ResponseEntity<List<ViajeModel>> getAllViajes();

    @GetMapping("/api/v1/viajes/monopatin/{idMonopatin}")
    ResponseEntity<List<ViajeModel>> getViajesByMonopatin(@PathVariable("idMonopatin") Long idMonopatin);
}

