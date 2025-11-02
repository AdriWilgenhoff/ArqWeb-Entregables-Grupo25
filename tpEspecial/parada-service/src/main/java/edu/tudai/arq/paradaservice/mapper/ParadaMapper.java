package edu.tudai.arq.paradaservice.mapper;

import edu.tudai.arq.paradaservice.dto.ParadaDTO;
import edu.tudai.arq.paradaservice.entity.Parada;
import org.springframework.stereotype.Component;

@Component
public class ParadaMapper {

    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public Parada toEntity(ParadaDTO.Create in) {
        return new Parada(
                normalizeString(in.nombre()),
                in.latitud(),
                in.longitud(),
                in.capacidad()
        );
    }

    public void update(Parada p, ParadaDTO.Update in) {
        if (in.nombre() != null) {
            p.setNombre(normalizeString(in.nombre()));
        }
        if (in.latitud() != null) {
            p.setLatitud(in.latitud());
        }
        if (in.longitud() != null) {
            p.setLongitud(in.longitud());
        }
        if (in.capacidad() != null) {
            p.setCapacidad(in.capacidad());
        }
        if (in.monopatinesDisponibles() != null) {
            p.setMonopatinesDisponibles(in.monopatinesDisponibles());
        }
    }

    public ParadaDTO.Response toResponse(Parada p) {
        return new ParadaDTO.Response(
                p.getId(),
                p.getNombre(),
                p.getLatitud(),
                p.getLongitud(),
                p.getCapacidad(),
                p.getMonopatinesDisponibles()
        );
    }
}

