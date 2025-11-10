package edu.tudai.arq.monopatinservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MonopatinServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonopatinServiceApplication.class, args);
    }
}

