package edu.tudai.arq.monopatinservice.mapper;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import org.springframework.stereotype.Component;

@Component
public class MonopatinMapper {

    public Monopatin toEntity(MonopatinDTO.Create dto) {
        return new Monopatin(
                dto.estado(),
                dto.latitud(),
                dto.longitud()
        );
    }

    public MonopatinDTO.Response toResponse(Monopatin entity) {
        return new MonopatinDTO.Response(
                entity.getId(),
                entity.getEstado(),
                entity.getLatitud(),
                entity.getLongitud(),
                entity.getKilometrosTotales(),
                entity.getTiempoUsoTotal(),
                entity.getTiempoPausas()
        );
    }

    public void update(Monopatin entity, MonopatinDTO.Update dto) {
        if (dto.estado() != null) {
            entity.setEstado(dto.estado());
        }
        if (dto.latitud() != null) {
            entity.setLatitud(dto.latitud());
        }
        if (dto.longitud() != null) {
            entity.setLongitud(dto.longitud());
        }
        if (dto.kilometrosTotales() != null) {
            entity.setKilometrosTotales(dto.kilometrosTotales());
        }
        if (dto.tiempoUsoTotal() != null) {
            entity.setTiempoUsoTotal(dto.tiempoUsoTotal());
        }
        if (dto.tiempoPausas() != null) {
            entity.setTiempoPausas(dto.tiempoPausas());
        }
    }
}