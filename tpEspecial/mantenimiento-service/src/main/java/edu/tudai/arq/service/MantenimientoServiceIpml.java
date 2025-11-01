package edu.tudai.arq.service;

import edu.tudai.arq.dto.MantenimientoServiceDTO;
import edu.tudai.arq.service.Interface.MantenimientoServiceInterface;

import java.util.List;
import java.util.Map;

public class MantenimientoServiceIpml implements MantenimientoServiceInterface {
    @Override
    public MantenimientoServiceDTO.Response create(MantenimientoServiceDTO.Create in) {
        return null;
    }

    @Override
    public MantenimientoServiceDTO.Response finalizar(Long id, MantenimientoServiceDTO.Update finishData) {
        return null;
    }

    @Override
    public void marcarEnMantenimiento(Long id) {

    }

    @Override
    public void desmarcarMantenimiento(Long id, Long idParadaDestino) {

    }

    @Override
    public List<MantenimientoServiceDTO.Response> findActivos() {
        return List.of();
    }

    @Override
    public List<MantenimientoServiceDTO.Response> findFinalizados() {
        return List.of();
    }

    @Override
    public Map<String, Long> operativosVsMantenimiento() {
        return Map.of();
    }
}
