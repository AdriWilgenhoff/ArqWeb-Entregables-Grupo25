package edu.tudai.arq.monopatinservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReporteUsoDTO {

    @Schema(description = "DTO para reporte de uso de monopatines SIN detalles de pausas")
    public record Simple(
            @Schema(description = "ID del monopatín", example = "1")
            Long idMonopatin,

            @Schema(description = "Kilómetros totales recorridos", example = "150.5")
            Double kilometrosTotales,

            @Schema(description = "Tiempo de uso sin pausas en minutos", example = "2100")
            Long tiempoUsoSinPausas
    ) {}

    @Schema(description = "DTO para reporte de uso de monopatines CON detalles de pausas")
    public record ConPausas(
            @Schema(description = "ID del monopatín", example = "1")
            Long idMonopatin,

            @Schema(description = "Kilómetros totales recorridos", example = "150.5")
            Double kilometrosTotales,

            @Schema(description = "Tiempo de uso sin pausas en minutos", example = "2100")
            Long tiempoUsoSinPausas,

            @Schema(description = "Tiempo en pausas en minutos", example = "400")
            Long tiempoPausas
    ) {}
}
