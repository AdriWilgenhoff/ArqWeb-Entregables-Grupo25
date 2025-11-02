package edu.tudai.arq.mapper;


import edu.tudai.arq.dto.MantenimientoServiceDTO;
import edu.tudai.arq.entity.MantenimientoService;
import org.springframework.stereotype.Component;

@Component
public class MantenimientoServiceMapper {
    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public MantenimientoService toEntity(MantenimientoServiceDTO.Create in, Long idMonopatin) {
        MantenimientoService m = new MantenimientoService();

        m.setIdMonopatin(idMonopatin);
        m.setDescripcion(normalizeString(in.descripcion()));
        m.setFechaHoraInicio(in.fechaHoraInicio());
        m.setKilometrosAlIniniciar(in.kilometrosAlIniniciar());
        m.setTiempoUsoAlIniniciar(in.tiempoUsoAlIniniciar());

        return m;
    }

    public void update(MantenimientoService m, MantenimientoServiceDTO.Update in) {
        m.setFechaHoraInicio(in.fechaHoraFin());
    }

    public MantenimientoServiceDTO.Response toResponse(MantenimientoService m) {
        return new MantenimientoServiceDTO.Response(
                m.getId(),
                m.getIdMonopatin(),
                m.getFechaHoraInicio(),
                m.getFechaHoraFin(),
                m.getDescripcion(),
                m.getKilometrosAlIniniciar(),
                m.getTiempoUsoAlIniniciar(),
                m.getIdEncargado()
        );
    }
}