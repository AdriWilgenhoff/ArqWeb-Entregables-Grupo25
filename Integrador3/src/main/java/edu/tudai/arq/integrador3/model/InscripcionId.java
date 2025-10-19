package edu.tudai.arq.integrador3.model;

import java.io.Serializable;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode
public class InscripcionId implements Serializable {

    private Long idEstudiante;
    private Long idCarrera;

}