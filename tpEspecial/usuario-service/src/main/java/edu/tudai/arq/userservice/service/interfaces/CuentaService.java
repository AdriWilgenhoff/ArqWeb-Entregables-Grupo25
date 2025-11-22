package edu.tudai.arq.userservice.service.interfaces;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import edu.tudai.arq.userservice.dto.UsuarioDTO;
import java.util.List;

public interface CuentaService {

    CuentaDTO.Response create(CuentaDTO.Create in);

    CuentaDTO.Response update(Long id, CuentaDTO.Update in);

    void delete(Long id);

    CuentaDTO.Response findById(Long id);

    List<CuentaDTO.Response> findAll();

    CuentaDTO.Response findByNumeroIdentificatorio(String numeroIdentificatorio);

    CuentaDTO.Response cargarSaldo(Long id, CuentaDTO.CargarSaldo in);

    CuentaDTO.Response descontarSaldo(Long id, CuentaDTO.DescontarSaldo in);

    void anularCuenta(Long id);

    void habilitarCuenta(Long id);

    List<UsuarioDTO.Response> getUsuariosByCuenta(Long idCuenta);

    // ==================== MÉTODOS PREMIUM ====================

    CuentaDTO.Response upgradeToPremium(Long id);

    void renovarCupo(Long id);

    Double usarKilometrosGratis(Long id, Double kilometros);

    /**
     * Descuenta kilómetros gratis si la cuenta es PREMIUM.
     * Retorna cuántos km se descontaron y cuántos quedan por cobrar.
     */
    CuentaDTO.ResultadoDescuentoKm descontarKilometrosGratis(Long id, CuentaDTO.DescontarKilometros in);
}