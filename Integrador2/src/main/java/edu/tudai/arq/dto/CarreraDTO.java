package edu.tudai.arq.dto;

import edu.tudai.arq.entity.Carrera;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CarreraDTO implements Serializable {

    private Long idCarrera;
    private String nombre;

    public CarreraDTO(Carrera c) {
        this.idCarrera = c.getIdCarrera();
        this.nombre = c.getNombre();

    }

    @Override
    public String toString() {
        return "Carrera {" +
                "idCarrera=" + idCarrera +
                ", nombre='" + nombre + '\'';
    }
}
