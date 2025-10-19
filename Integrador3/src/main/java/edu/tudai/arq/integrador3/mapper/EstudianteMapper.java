package edu.tudai.arq.integrador3.mapper;

import edu.tudai.arq.integrador3.dto.EstudianteDTO;
import edu.tudai.arq.integrador3.model.Estudiante;
import org.springframework.stereotype.Component;

@Component
public class EstudianteMapper {

    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public Estudiante toEntity(EstudianteDTO.Create in) {
        return new Estudiante(
                normalizeString(in.nombre()),
                normalizeString(in.apellido()),
                in.fechaNacimiento(),
                in.genero(),
                in.dni(),
                normalizeString(in.ciudadResidencia()),
                in.lu()
        );
    }

    public void update(Estudiante e, EstudianteDTO.Update in) {
        e.setNombre(normalizeString(in.nombre()));
        e.setApellido(normalizeString(in.apellido()));
        e.setFechaNacimiento(in.fechaNacimiento());
        e.setGenero(in.genero());
        e.setCiudadResidencia(normalizeString(in.ciudadResidencia()));
    }

    public EstudianteDTO.Response toResponse(Estudiante e) {
        return new EstudianteDTO.Response(
                e.getIdEstudiante(),
                e.getNombre(),
                e.getApellido(),
                e.getFechaNacimiento(),
                e.getEdad(),
                e.getGenero(),
                e.getDni(),
                e.getCiudadResidencia(),
                e.getLu()
        );
    }


}