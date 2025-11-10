package edu.tudai.arq.paradaservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ParadaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParadaServiceApplication.class, args);
    }
}
