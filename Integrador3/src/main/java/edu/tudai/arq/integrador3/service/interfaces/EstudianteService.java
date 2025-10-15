package edu.tudai.arq.integrador3.service.interfaces;

import edu.tudai.arq.integrador3.dto.EstudianteDTO;
import edu.tudai.arq.integrador3.model.Genero;

import java.util.List;

public interface EstudianteService {

    List<EstudianteDTO.Response> findAll();

    EstudianteDTO.Response create(EstudianteDTO.Create in);

    EstudianteDTO.Response update(Long id, EstudianteDTO.Update in);

    void delete(Long id);

    EstudianteDTO.Response findById(Long id);

    EstudianteDTO.Response findByLu(Long lu);

    List<EstudianteDTO.Response> findAllSorted(String sortBy, String direction);

    List<EstudianteDTO.Response> findAllByGenero(Genero genero);

    List<EstudianteDTO.Response> findByCarreraAndCiudad(Long carreraId, String ciudad);
}
