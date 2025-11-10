package edu.tudai.arq.monopatinservice.service.interfaces;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.dto.ReporteOperacionDTO;

import java.util.List;

public interface MonopatinService {

    MonopatinDTO.Response create(MonopatinDTO.Create in);

    MonopatinDTO.Response update(Long id, MonopatinDTO.Update in);

    MonopatinDTO.Response findById(Long id);

    List<MonopatinDTO.Response> findAll();

    void delete(Long id);

    MonopatinDTO.Response cambiarEstado(Long id, String nuevoEstado);

    List<MonopatinDTO.Response> findMonopatinesCercanos(Integer latitud, Integer longitud, Double radioKm);

    List<MonopatinDTO.Response> findMonopatinesConMasDeXViajes(Integer cantidadViajes, Integer anio);

    ReporteOperacionDTO getReporteOperacion();

    List<?> getReporteUsoPorKilometros(boolean incluirPausas);
}