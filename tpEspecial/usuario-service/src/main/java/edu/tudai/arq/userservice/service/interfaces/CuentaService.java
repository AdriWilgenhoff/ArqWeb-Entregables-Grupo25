package edu.tudai.arq.userservice.service.interfaces;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import java.util.List;

public interface CuentaService {

    CuentaDTO.Response create(CuentaDTO.Create in);

    CuentaDTO.Response update(Long id, CuentaDTO.Update in);

    void delete(Long id);

    CuentaDTO.Response findById(Long id);

    List<CuentaDTO.Response> findAll();

    CuentaDTO.Response findByNumeroIdentificatorio(String numeroIdentificatorio);

    CuentaDTO.Response cargarSaldo(Long id, CuentaDTO.CargarSaldo in);

    void anularCuenta(Long id);

    void habilitarCuenta(Long id);
}