package edu.tudai.arq.adminservice.feignclient;

import edu.tudai.arq.adminservice.model.MonopatinModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "monopatin-service", url = "http://localhost:8082")
public interface MonopatinFeignClient {

    @GetMapping("/api/v1/monopatines")
    ResponseEntity<List<MonopatinModel>> getAllMonopatines();

    @GetMapping("/api/v1/monopatines/{id}")
    ResponseEntity<MonopatinModel> getMonopatinById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/monopatines")
    ResponseEntity<MonopatinModel> createMonopatin(@RequestBody MonopatinModel monopatin);

    @DeleteMapping("/api/v1/monopatines/{id}")
    ResponseEntity<Void> deleteMonopatin(@PathVariable("id") Long id);

    @GetMapping("/api/v1/monopatines/estado/{estado}")
    ResponseEntity<List<MonopatinModel>> getMonopatinesByEstado(@PathVariable("estado") String estado);

    @GetMapping("/api/v1/monopatines/cercanos")
    ResponseEntity<List<MonopatinModel>> getMonopatinesCercanos(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(defaultValue = "5.0") Double radioKm);

    @GetMapping("/api/v1/monopatines/con-viajes")
    ResponseEntity<List<MonopatinModel>> getMonopatinesConMasDeXViajes(
            @RequestParam Integer cantidadViajes,
            @RequestParam Integer anio);
}

