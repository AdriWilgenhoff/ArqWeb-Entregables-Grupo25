package edu.tudai.arq.dto;

import edu.tudai.arq.entity.Estudiante;
import edu.tudai.arq.entity.Genero;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class EstudianteDTO implements Serializable {

    private String nombre;
    private String apellido;
    private Integer edad;
    private Genero genero;
    private String ciudadResidencia;
    private Long lu;

    public EstudianteDTO(Estudiante estudiante) {
        this.nombre = estudiante.getNombre();
        this.apellido = estudiante.getApellido();
        this.edad = estudiante.getEdad();
        this.genero = estudiante.getGenero();
        this.ciudadResidencia= estudiante.getCiudadResidencia();
        this.lu = estudiante.getLu();
    }

    public EstudianteDTO(String nombre, String apellido, Genero genero, LocalDate fechaNacimiento, String ciudadResidencia, Long lu) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.genero = genero;
        this.ciudadResidencia = ciudadResidencia;
        this.lu = lu;
        this.edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "EstudianteDTO{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", edad='" + edad + '\'' +
                ", libreta universitaria='" + lu + '\'' +
                ", ciudadResidencia='" + ciudadResidencia + '\'' +
                ", genero='" + genero + '\'' +
                '}';
    }
}
