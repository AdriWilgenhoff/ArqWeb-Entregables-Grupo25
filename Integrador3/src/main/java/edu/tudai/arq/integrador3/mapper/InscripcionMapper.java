package edu.tudai.arq.integrador3.mapper;

import edu.tudai.arq.integrador3.dto.InscripcionDTO;
import edu.tudai.arq.integrador3.model.Carrera;
import edu.tudai.arq.integrador3.model.Estudiante;
import edu.tudai.arq.integrador3.model.Inscripcion;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InscripcionMapper {

    public Inscripcion toEntity(InscripcionDTO.Create in, Estudiante estudiante, Carrera carrera) {
        Inscripcion ins = new Inscripcion(estudiante, carrera);
        if (in.fechaInscripcion() != null) {
            ins.setFechaInscripcion(in.fechaInscripcion());
        } else {
            ins.setFechaInscripcion(LocalDate.now());
        }
        return ins;
    }

    public InscripcionDTO.Response toResponse(Inscripcion i) {
        return new InscripcionDTO.Response(
                i.getEstudiante().getIdEstudiante(),
                i.getCarrera().getIdCarrera(),
                i.getFecha_inscripcion(),
                i.getFecha_egreso(),
                i.isGraduo()
        );
    }
}
