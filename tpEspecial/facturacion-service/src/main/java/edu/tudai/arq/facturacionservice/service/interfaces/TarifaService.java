package edu.tudai.arq.facturacionservice.service.interfaces;

import edu.tudai.arq.facturacionservice.dto.TarifaDTO;

import java.time.LocalDate;
import java.util.List;

public interface TarifaService {

    // Operaciones CRUD básicas
    TarifaDTO.Response create(TarifaDTO.Create in);

    TarifaDTO.Response update(Long id, TarifaDTO.Update in);

    void delete(Long id);

    TarifaDTO.Response findById(Long id);

    List<TarifaDTO.Response> findAll();

    // Consultas específicas
    List<TarifaDTO.Response> findByTipo(String tipoTarifa);

    List<TarifaDTO.Response> findActivas();

    TarifaDTO.Response findTarifaVigente(String tipoTarifa, LocalDate fecha);

    List<TarifaDTO.Response> findTarifasVigentesEn(LocalDate fecha);

    // Operaciones especiales
    void desactivarTarifa(Long id, LocalDate fechaFin);
}

