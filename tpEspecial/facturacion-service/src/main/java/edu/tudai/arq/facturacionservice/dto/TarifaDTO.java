package edu.tudai.arq.facturacionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class TarifaDTO {

    /** DTO para crear Tarifa */
    @Schema(description = "DTO de entrada para crear una nueva Tarifa", name = "TarifaCreate")
    public record Create(
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

    /** DTO para actualizar Tarifa */
    @Schema(description = "DTO de entrada para actualizar una Tarifa", name = "TarifaUpdate")
    public record Update(
            @NotNull(message = "El precio por minuto es obligatorio")
            @Positive(message = "El precio por minuto debe ser positivo")
            Double precioPorMinuto,

            String fechaVigenciaHasta,

            Boolean activa
    ) {}

    /** DTO de salida para Tarifa */
    @Schema(description = "DTO de respuesta para Tarifas", name = "TarifaResponse")
    public record Response(
            @Schema(description = "ID de la tarifa", example = "1")
            Long id,

            @Schema(description = "Tipo de tarifa", example = "NORMAL")
            String tipoTarifa,

            @Schema(description = "Precio por minuto", example = "50.00")
            Double precioPorMinuto,

            @Schema(description = "Fecha de vigencia desde", example = "2024-01-01")
            String fechaVigenciaDesde,

            @Schema(description = "Fecha de vigencia hasta", example = "2024-12-31")
            String fechaVigenciaHasta,

            @Schema(description = "Indica si la tarifa está activa", example = "true")
            Boolean activa
    ) {}
}

