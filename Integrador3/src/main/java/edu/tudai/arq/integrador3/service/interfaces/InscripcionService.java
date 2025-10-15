package edu.tudai.arq.integrador3.service.interfaces;

import edu.tudai.arq.integrador3.dto.InscripcionDTO;

public interface InscripcionService {

    String matricularEstudianteEnCarrera(InscripcionDTO.Create in);
}
