package edu.tudai.arq.paradaservice.service.interfaces;

import edu.tudai.arq.paradaservice.dto.ParadaDTO;

import java.util.List;

public interface ParadaService {

    ParadaDTO.Response create(ParadaDTO.Create in);

    ParadaDTO.Response update(Long id, ParadaDTO.Update in);

    void delete(Long id);

    ParadaDTO.Response findById(Long id);

    List<ParadaDTO.Response> findAll();

    List<ParadaDTO.Response> findParadasConEspacio();

    List<ParadaDTO.Response> findParadasCercanas(Double latitud, Double longitud, Double radioKm);

    void incrementarMonopatines(Long id);

    void decrementarMonopatines(Long id);

    boolean verificarEspacioDisponible(Long id);
}

