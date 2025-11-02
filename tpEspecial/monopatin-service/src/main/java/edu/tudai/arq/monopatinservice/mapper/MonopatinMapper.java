package edu.tudai.arq.monopatinservice.mapper;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import org.springframework.stereotype.Component;

@Component
public class MonopatinMapper {
    public Monopatin toEntity(MonopatinDTO.Create dto) {
        return new Monopatin(
                dto.getEstado(),
                dto.getLatitud(),
                dto.getLongitud()
        );
    }

    public MonopatinDTO.Response toResponse(Monopatin entity) {
        return new MonopatinDTO.Response(entity);
    }

    public void update(Monopatin entity, MonopatinDTO.Update dto) {
        if (dto.getEstado() != null) { entity.setEstado(dto.getEstado()); }
        if (dto.getLatitud() != null) { entity.setLatitud(dto.getLatitud()); }
        if (dto.getLongitud() != null) { entity.setLongitud(dto.getLongitud()); }
        if (dto.getKilometrosTotales() != null) { entity.setKilometrosTotales(dto.getKilometrosTotales()); }
        if (dto.getTiempoUsoTotal() != null) { entity.setTiempoUsoTotal(dto.getTiempoUsoTotal()); }
    }
}