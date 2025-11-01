package edu.tudai.arq.facturacionservice.service.interfaces;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface FacturacionService {

    // Operaciones CRUD básicas
    FacturacionDTO.Response create(FacturacionDTO.Create in);

    void delete(Long id);

    FacturacionDTO.Response findById(Long id);

    List<FacturacionDTO.Response> findAll();

    // Consultas específicas
    FacturacionDTO.Response findByViaje(Long idViaje);

    List<FacturacionDTO.Response> findByCuenta(Long idCuenta);

    List<FacturacionDTO.Response> findByFechaEntre(LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    List<FacturacionDTO.Response> findByCuentaAndFechaEntre(
            Long idCuenta, LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    // Reportes
    Double calcularTotalFacturado(LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    Double calcularTotalFacturadoPorCuenta(
            Long idCuenta, LocalDateTime fechaDesde, LocalDateTime fechaHasta);
}