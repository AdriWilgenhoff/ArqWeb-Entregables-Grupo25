package edu.tudai.arq.mantenimientoservice.service.interfaces;

import edu.tudai.arq.mantenimientoservice.dto.MantenimientoDTO;
import edu.tudai.arq.mantenimientoservice.dto.ReporteUsoDTO;

import java.util.List;
import java.util.Map;

public interface MantenimientoService {

    MantenimientoDTO.Response create(MantenimientoDTO.Create in);

    MantenimientoDTO.Response update(Long id, MantenimientoDTO.Update in);

    MantenimientoDTO.Response finalizar(Long id, MantenimientoDTO.Update finishData);

    void delete(Long id);

    MantenimientoDTO.Response findById(Long id);

    List<MantenimientoDTO.Response> findAll();

    List<MantenimientoDTO.Response> findActivos();

    List<MantenimientoDTO.Response> findFinalizados();

    void marcarEnMantenimiento(Long idMonopatin);

    void desmarcarMantenimiento(Long idMonopatin, Long idParadaDestino);


    List<ReporteUsoDTO.Response> generarReporteUso(boolean incluirPausas);
    Map<String, Long> operativosVsMantenimiento();
}

