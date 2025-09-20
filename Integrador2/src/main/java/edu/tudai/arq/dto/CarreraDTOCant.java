package edu.tudai.arq.dto;

import edu.tudai.arq.entity.Carrera;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarreraDTOCant extends CarreraDTO {

    private Long cantInscriptos;

    public CarreraDTOCant(Carrera carrera, Long cantInscriptos) {
        super(carrera);
        this.cantInscriptos = cantInscriptos;
    }

    @Override
    public String toString() {
        return  super.toString() +
                ", cantInscriptos=" + cantInscriptos +
                '}';
    }
}
