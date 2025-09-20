package edu.tudai.arq.repository.interfaces;

import edu.tudai.arq.dto.CarreraDTOCant;
import edu.tudai.arq.dto.ReporteCarreraDTO;
import edu.tudai.arq.entity.Carrera;

import java.util.List;

public interface CarreraRepository {

    List<CarreraDTOCant> getCarrerasConInscriptosOrdenadas();
    Carrera getCarreraByName(String name);
    List<ReporteCarreraDTO> generarReporteCarreras();

}
