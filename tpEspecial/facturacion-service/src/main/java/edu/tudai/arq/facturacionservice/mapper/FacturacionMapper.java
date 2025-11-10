package edu.tudai.arq.facturacionservice.mapper;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;
import edu.tudai.arq.facturacionservice.entity.Facturacion;
import org.springframework.stereotype.Component;

@Component
public class FacturacionMapper {

    public Facturacion toEntity(Long idViaje, Long idCuenta, Long tiempoTotal, Long tiempoPausado,
                                Long idTarifaNormal, Long idTarifaPausa, Long idTarifaExtendida, Double montoTotal) {
        return new Facturacion(
                idViaje,
                idCuenta,
                tiempoTotal,
                tiempoPausado,
                idTarifaNormal,
                idTarifaPausa,
                idTarifaExtendida,
                montoTotal
        );
    }

    public FacturacionDTO.Response toResponse(Facturacion f) {
        return new FacturacionDTO.Response(
                f.getId(),
                f.getIdViaje(),
                f.getIdCuenta(),
                f.getFecha().toString(),
                f.getMontoTotal(),
                f.getTiempoTotal(),
                f.getTiempoPausado(),
                f.getTiempoSinPausas(),
                f.getIdTarifaNormal(),
                f.getIdTarifaPausa(),
                f.getIdTarifaExtendida()
        );
    }

    public FacturacionDTO.Resumen toResumen(Facturacion f) {
        return new FacturacionDTO.Resumen(
                f.getId(),
                f.getIdViaje(),
                f.getIdCuenta(),
                f.getFecha().toString(),
                f.getMontoTotal()
        );
    }
}
