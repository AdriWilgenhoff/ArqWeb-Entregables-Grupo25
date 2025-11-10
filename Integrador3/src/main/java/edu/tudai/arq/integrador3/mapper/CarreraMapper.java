package edu.tudai.arq.integrador3.mapper;

import edu.tudai.arq.integrador3.dto.CarreraDTO;
import edu.tudai.arq.integrador3.model.Carrera;
import org.springframework.stereotype.Component;

@Component
public class CarreraMapper {

    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public Carrera toEntity(CarreraDTO.Create in) {
        Carrera c = new Carrera(
                normalizeString(in.nombre()), in.duracionAnios()

         );
        return c;
    }

    public void update(Carrera c, CarreraDTO.Update in) {
        c.setNombre(normalizeString(in.nombre()));
        c.setDuracion(in.duracion());
    }

    public CarreraDTO.Response toResponse(Carrera c) {
        return new CarreraDTO.Response(
                c.getIdCarrera(),
                c.getNombre(),
                c.getDuracion()
        );
    }
