package edu.tudai.arq.monopatinservice.service.interfaces;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;

import java.util.List;

public interface MonopatinService {

    // Operación de Creación
    MonopatinDTO.Response create(MonopatinDTO.Create in);

    // Operación de Actualización
    MonopatinDTO.Response update(Long id, MonopatinDTO.Update in);

    // Operación de Lectura por ID
    MonopatinDTO.Response findById(Long id);

    // Operación de Lectura de todos
    List<MonopatinDTO.Response> findAll();

    // Operación de Eliminación
    void delete(Long id);

    // Métodos de Negocio Específicos para Monopatín

    MonopatinDTO.Response cambiarEstado(Long id, String nuevoEstado);

    List<MonopatinDTO.Response> findMonopatinesCercanos(Double latitud, Double longitud, Double radioKm);

}