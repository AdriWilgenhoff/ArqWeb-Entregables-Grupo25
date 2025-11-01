package edu.tudai.arq.adminservice.feignclient;

import edu.tudai.arq.adminservice.model.ParadaModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "parada-service", url = "http://localhost:8083")
public interface ParadaFeignClient {

    @GetMapping("/api/v1/paradas")
    ResponseEntity<List<ParadaModel>> getAllParadas();

    @GetMapping("/api/v1/paradas/{id}")
    ResponseEntity<ParadaModel> getParadaById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/paradas")
    ResponseEntity<ParadaModel> createParada(@RequestBody ParadaModel parada);

    @DeleteMapping("/api/v1/paradas/{id}")
    ResponseEntity<Void> deleteParada(@PathVariable("id") Long id);

    @PutMapping("/api/v1/paradas/{id}")
    ResponseEntity<ParadaModel> updateParada(@PathVariable("id") Long id, @RequestBody ParadaModel parada);
}

