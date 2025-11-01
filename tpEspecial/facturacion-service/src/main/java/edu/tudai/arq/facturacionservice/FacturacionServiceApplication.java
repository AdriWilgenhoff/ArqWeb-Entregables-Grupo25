package edu.tudai.arq.facturacionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FacturacionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FacturacionServiceApplication.class, args);
    }
}
