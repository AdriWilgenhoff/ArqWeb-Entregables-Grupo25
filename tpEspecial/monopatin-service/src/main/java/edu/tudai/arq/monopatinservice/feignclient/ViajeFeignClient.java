package edu.tudai.arq.monopatinservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "viaje-service", url = "http://localhost:8084")
public interface ViajeFeignClient {

    @GetMapping("/api/v1/viajes/monopatines-con-mas-viajes")
    List<Long> getMonopatinesConMasDeXViajes(
            @RequestParam Integer cantidadViajes,
            @RequestParam Integer anio
    );
}

