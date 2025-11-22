package edu.tudai.arq.paradaservice.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.tudai.arq.paradaservice.entity.Parada;
import edu.tudai.arq.paradaservice.repository.ParadaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;

@Component

    private final ParadaRepository paradaRepository;
    private final ObjectMapper objectMapper;

    public CargaDeDatos(ParadaRepository paradaRepository, ObjectMapper objectMapper) {
        this.paradaRepository = paradaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (paradaRepository.count() == 0) {
            try (InputStream inputStream = TypeReference.class.getResourceAsStream("/paradas.json")) {

                List<Parada> paradas = objectMapper.readValue(inputStream, new TypeReference<List<Parada>>(){});

                paradaRepository.saveAll(paradas);

                System.out.println("DATOS CARGADOS EXITOSAMENTE EN NOSQL");
            } catch (Exception e) {
                System.out.println("No se pudo cargar el archivo paradas.json: " + e.getMessage());
            }
        } else {
            System.out.println("LA BASE DE DATOS YA CONTIENE DATOS");
        }
    }
}