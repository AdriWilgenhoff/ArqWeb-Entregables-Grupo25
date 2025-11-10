package edu.tudai.arq.viajeservice.mapper;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.entity.Pausa;
import org.springframework.stereotype.Component;

@Component
public class PausaMapper {

    public PausaDTO.Response toResponse(Pausa p) {
        return new PausaDTO.Response(
                p.getId(),
                p.getIdViaje(),
                p.getHoraInicio().toString(),
                p.getHoraFin() != null ? p.getHoraFin().toString() : null,
                p.getExtendida()
        );
    }
}

