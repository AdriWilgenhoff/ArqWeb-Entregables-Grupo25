package edu.tudai.arq.facturacionservice.mapper;

import edu.tudai.arq.facturacionservice.dto.TarifaDTO;
import edu.tudai.arq.facturacionservice.entity.Tarifa;
import edu.tudai.arq.facturacionservice.entity.TipoTarifa;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TarifaMapper {

    public Tarifa toEntity(TarifaDTO.Create in) {
        return new Tarifa(
                TipoTarifa.valueOf(in.tipoTarifa()),
                in.precioPorMinuto(),
                LocalDate.parse(in.fechaVigenciaDesde())
        );
    }

    public void update(Tarifa t, TarifaDTO.Update in) {
        t.setPrecioPorMinuto(in.precioPorMinuto());

        if (in.fechaVigenciaHasta() != null && !in.fechaVigenciaHasta().isBlank()) {
            t.setFechaVigenciaHasta(LocalDate.parse(in.fechaVigenciaHasta()));
        }

        if (in.activa() != null) {
            t.setActiva(in.activa());
        }
    }

    public TarifaDTO.Response toResponse(Tarifa t) {
        return new TarifaDTO.Response(
                t.getId(),
                t.getTipoTarifa().name(),
                t.getPrecioPorMinuto(),
                t.getFechaVigenciaDesde().toString(),
                t.getFechaVigenciaHasta() != null ? t.getFechaVigenciaHasta().toString() : null,
                t.getActiva()
        );
    }
}

