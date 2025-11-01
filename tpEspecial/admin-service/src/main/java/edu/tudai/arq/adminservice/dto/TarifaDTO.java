package edu.tudai.arq.adminservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class TarifaDTO {

    @Schema(description = "DTO para crear/actualizar tarifa", name = "TarifaCreateUpdate")
    public record CreateUpdate(
            @NotNull(message = "El tipo de tarifa es obligatorio")
            @Pattern(regexp = "NORMAL|PAUSA_EXTENDIDA",
                    message = "El tipo de tarifa debe ser NORMAL o PAUSA_EXTENDIDA")
            String tipoTarifa,

            @NotNull(message = "El precio por minuto es obligatorio")
            @Positive(message = "El precio por minuto debe ser positivo")
            Double precioPorMinuto,

            @NotNull(message = "La fecha de vigencia desde es obligatoria")
            String fechaVigenciaDesde
    ) {}
}

