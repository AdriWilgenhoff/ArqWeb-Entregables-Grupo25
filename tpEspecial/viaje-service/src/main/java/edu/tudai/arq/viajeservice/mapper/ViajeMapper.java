package edu.tudai.arq.viajeservice.mapper;

import edu.tudai.arq.viajeservice.dto.ViajeDTO;
import edu.tudai.arq.viajeservice.entity.Viaje;
import org.springframework.stereotype.Component;

@Component
public class ViajeMapper {

    public Viaje toEntity(ViajeDTO.Create in) {
        return new Viaje(
                in.idCuenta(),
                in.idUsuario(),
                in.idMonopatin(),
                in.idParadaInicio()
        );
    }

    public ViajeDTO.Response toResponse(Viaje v) {
        return new ViajeDTO.Response(
                v.getId(),
                v.getIdCuenta(),
                v.getIdUsuario(),
                v.getIdMonopatin(),
                v.getFechaHoraInicio().toString(),
                v.getFechaHoraFin() != null ? v.getFechaHoraFin().toString() : null,
                v.getIdParadaInicio(),
                v.getIdParadaFin(),
                v.getKilometrosRecorridos(),
                v.getEstado().name(),
                v.getCostoTotal(),

                v.getPausas().size(),
                v.calcularTiempoTotal(),
                v.calcularTiempoPausado(),
                v.calcularTiempoPausaExtendida(),
                v.calcularTiempoPausaNormal(),
                v.calcularTiempoSinPausas()
        );
    }

    public ViajeDTO.Resumen toResumen(Viaje v) {
        return new ViajeDTO.Resumen(
                v.getId(),
                v.getIdUsuario(),
                v.getIdMonopatin(),
                v.getFechaHoraInicio().toString(),
                v.getKilometrosRecorridos(),
                v.getEstado().name(),
                v.getCostoTotal()
        );
    }
}


