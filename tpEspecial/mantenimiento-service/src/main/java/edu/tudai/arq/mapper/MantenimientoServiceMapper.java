package edu.tudai.arq.mapper;


import edu.tudai.arq.dto.MantenimientoServiceDTO;
import edu.tudai.arq.entity.MantenimientoService;
import org.springframework.stereotype.Component;

@Component
public class MantenimientoServiceMapper {
    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public MantenimientoService toEntity(MantenimientoServiceDTO.Create in) {
        MantenimientoService m = new MantenimientoService(
                normalizeString(in.descripcion()), in.fechaHoraInicio()

        );
        return m;
    }

    public void update(MantenimientoService m, MantenimientoServiceDTO.Update in) {
        m.setDescripcion(normalizeString(in.descripcion()));
        m.setFechaHoraInicio(in.fechaHoraFin());
    }

    public  MantenimientoServiceDTO.Response toResponse(MantenimientoService m) {
        return new MantenimientoServiceDTO.Response(
                m.getId(),
                m.getFechaHoraInicio(),
                m.getFechaHoraFin()
        );
}
