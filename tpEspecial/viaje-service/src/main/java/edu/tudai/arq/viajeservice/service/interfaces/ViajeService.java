package edu.tudai.arq.viajeservice.service.interfaces;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.dto.ViajeDTO;

import java.util.List;

public interface ViajeService {

    // Operaciones CRUD básicas
    ViajeDTO.Response iniciarViaje(ViajeDTO.Create in);

    ViajeDTO.Response finalizarViaje(Long id, ViajeDTO.Finalizacion in);

    ViajeDTO.Response findById(Long id);

    List<ViajeDTO.Response> findAll();

    void delete(Long id);

    // Operaciones de pausas
    PausaDTO.Response pausarViaje(Long idViaje);

    PausaDTO.Response reanudarViaje(Long idViaje);

    List<PausaDTO.Response> getPausasByViaje(Long idViaje);

    // Consultas específicas
    List<ViajeDTO.Response> findByUsuario(Long idUsuario);

    List<ViajeDTO.Response> findByCuenta(Long idCuenta);

    List<ViajeDTO.Response> findByMonopatin(Long idMonopatin);

    List<ViajeDTO.Resumen> findViajesActivos();

    ViajeDTO.Response findViajeActivoByMonopatin(Long idMonopatin);

    // Reportes
    List<Long> getMonopatinesConMasDeXViajes(Integer cantidadViajes, Integer anio);
}
