package edu.tudai.arq.mantenimientoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MantenimientoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MantenimientoServiceApplication.class, args);
    }
}



