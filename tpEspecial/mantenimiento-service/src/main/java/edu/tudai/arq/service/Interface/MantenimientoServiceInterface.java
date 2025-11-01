package edu.tudai.arq.service.Interface;

import edu.tudai.arq.dto.MantenimientoServiceDTO;

import java.util.List;
import java.util.Map;

public interface MantenimientoServiceInterface {

    MantenimientoServiceDTO.Response create(MantenimientoServiceDTO.Create in);
    MantenimientoServiceDTO.Response finalizar(Long id, MantenimientoServiceDTO.Update finishData);
    void marcarEnMantenimiento(Long id);
    void desmarcarMantenimiento(Long id, Long idParadaDestino);
    List<MantenimientoServiceDTO.Response> findActivos();
    List<MantenimientoServiceDTO.Response> findFinalizados();
    Map<String, Long> operativosVsMantenimiento();

}
