package edu.tudai.arq.facturacionservice.mapper;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;
import edu.tudai.arq.facturacionservice.entity.Facturacion;
import org.springframework.stereotype.Component;

@Component
public class FacturacionMapper {

    public Facturacion toEntity(FacturacionDTO.Create in) {
        return new Facturacion(
                in.idViaje(),
                in.idCuenta(),
                in.tiempoTotal(),
                in.tiempoPausado(),
                in.idTarifaNormal(),
                in.idTarifaExtendida(),
                in.montoTotal()
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
