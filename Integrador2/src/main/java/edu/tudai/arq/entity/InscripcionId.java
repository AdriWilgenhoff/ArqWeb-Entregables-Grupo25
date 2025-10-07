package edu.tudai.arq.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionId implements Serializable {

    private Long idEstudiante;
    private Long idCarrera;

}
