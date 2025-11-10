package edu.tudai.arq.facturacionservice.service.interfaces;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface FacturacionService {

    FacturacionDTO.Response create(FacturacionDTO.Create in);

    void delete(Long id);

    FacturacionDTO.Response findById(Long id);

    List<FacturacionDTO.Response> findAll();

    FacturacionDTO.Response findByViaje(Long idViaje);

    List<FacturacionDTO.Response> findByCuenta(Long idCuenta);

    // Reportes
    Double getTotalFacturadoPorPeriodo(Integer anio, Integer mesDesde, Integer mesHasta);
}