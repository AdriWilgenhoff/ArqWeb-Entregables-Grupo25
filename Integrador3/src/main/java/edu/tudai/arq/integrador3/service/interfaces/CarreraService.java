package edu.tudai.arq.integrador3.service.interfaces;

import edu.tudai.arq.integrador3.dto.CarreraDTO;
import edu.tudai.arq.integrador3.dto.CarreraDTOCant;
import edu.tudai.arq.integrador3.dto.ReporteCarreraDTO;

import java.util.List;

public interface CarreraService {

    CarreraDTO.Response create(CarreraDTO.Create in);

    CarreraDTO.Response update(Long id, CarreraDTO.Update in);

    void delete(Long id);

    CarreraDTO.Response findById(Long id);

    List<CarreraDTO.Response> findAll();

    CarreraDTO.Response findByNombre(String nombre);

    List<CarreraDTOCant> getCarrerasOrdenadasPorInscriptos();

    List<ReporteCarreraDTO> generarReporteCarreras();

}