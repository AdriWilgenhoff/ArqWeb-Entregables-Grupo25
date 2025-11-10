package edu.tudai.arq.facturacionservice.service.interfaces;

import edu.tudai.arq.facturacionservice.dto.TarifaDTO;

import java.util.List;

public interface TarifaService {

    TarifaDTO.Response create(TarifaDTO.Create in);

    TarifaDTO.Response update(Long id, TarifaDTO.Update in);

    void delete(Long id);

    TarifaDTO.Response findById(Long id);

    List<TarifaDTO.Response> findAll();

    List<TarifaDTO.Response> findActivas();
}
