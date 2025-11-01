package edu.tudai.arq.viajeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PausaDTO {

    /** DTO de salida para Pausa */
    @Schema(description = "DTO de respuesta para Pausas", name = "PausaResponse")
    public record Response(
            @Schema(description = "ID de la pausa", example = "1")
            Long id,

            @Schema(description = "ID del viaje", example = "1")
            Long idViaje,

            @Schema(description = "Hora de inicio de la pausa", example = "2024-11-01T10:45:00")
            String horaInicio,

            @Schema(description = "Hora de fin de la pausa", example = "2024-11-01T10:50:00")
            String horaFin,

            @Schema(description = "Indica si la pausa fue extendida (más de 15 minutos)", example = "false")
            Boolean extendida
    ) {}
}

