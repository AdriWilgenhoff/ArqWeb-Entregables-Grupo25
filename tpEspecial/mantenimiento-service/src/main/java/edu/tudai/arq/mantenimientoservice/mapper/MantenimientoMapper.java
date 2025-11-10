package edu.tudai.arq.mantenimientoservice.mapper;

import edu.tudai.arq.mantenimientoservice.dto.MantenimientoDTO;
import edu.tudai.arq.mantenimientoservice.entity.Mantenimiento;
import org.springframework.stereotype.Component;

@Component
public class MantenimientoMapper {

    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public Mantenimiento toEntity(MantenimientoDTO.Create in) {
        return new Mantenimiento(
                in.idMonopatin(),
                in.fechaHoraInicio(),
                normalizeString(in.descripcion()),
                in.kilometrosAlIniciar(),
                in.tiempoUsoAlIniciar()
        );
    }

    public void update(Mantenimiento m, MantenimientoDTO.Update in) {
        if (in.fechaHoraFin() != null) {
            m.setFechaHoraFin(in.fechaHoraFin());
        }
    }

    public MantenimientoDTO.Response toResponse(Mantenimiento m) {
        return new MantenimientoDTO.Response(
                m.getId(),
                m.getIdMonopatin(),
                m.getFechaHoraInicio(),
                m.getFechaHoraFin(),
                m.getDescripcion(),
                m.getKilometrosAlIniciar(),
                m.getTiempoUsoAlIniciar(),
                m.getIdEncargado()
        );
    }
}

