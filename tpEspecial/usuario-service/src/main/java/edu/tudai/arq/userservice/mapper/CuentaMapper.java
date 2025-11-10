package edu.tudai.arq.userservice.mapper;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import edu.tudai.arq.userservice.entity.Cuenta;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {

    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public Cuenta toEntity(CuentaDTO.Create in) {
        Cuenta c = new Cuenta(
                normalizeString(in.numeroIdentificatorio()),
                normalizeString(in.idCuentaMercadoPago())
        );

        if (in.saldoInicial() != null && in.saldoInicial() > 0) {
            c.setSaldo(in.saldoInicial());
        }

        return c;
    }

    public void update(Cuenta c, CuentaDTO.Update in) {
        c.setIdCuentaMercadoPago(normalizeString(in.idCuentaMercadoPago()));

        if (in.habilitada() != null) {
            c.setHabilitada(in.habilitada());
        }
    }

    public CuentaDTO.Response toResponse(Cuenta c) {
        return new CuentaDTO.Response(
                c.getId(),
                c.getNumeroIdentificatorio(),
                c.getFechaAlta().toString(),
                c.getSaldo(),
                c.getHabilitada(),
                c.getIdCuentaMercadoPago()
        );
    }
}