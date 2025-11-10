package edu.tudai.arq.mantenimientoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta para el reporte de monopatines en operación vs en mantenimiento")
public record ReporteOperacionDTO(
        @Schema(description = "Cantidad de monopatines en operación (disponibles o en uso)", example = "7")
        Long enOperacion,

        @Schema(description = "Cantidad de monopatines en mantenimiento", example = "2")
        Long enMantenimiento,

        @Schema(description = "Total de monopatines", example = "9")
        Long total,

        @Schema(description = "Porcentaje de monopatines en operación", example = "77.8")
        Double porcentajeOperacion,

        @Schema(description = "Porcentaje de monopatines en mantenimiento", example = "22.2")
        Double porcentajeMantenimiento
) {
    public ReporteOperacionDTO(Long enOperacion, Long enMantenimiento) {
        this(
                enOperacion,
                enMantenimiento,
                enOperacion + enMantenimiento,
                calcularPorcentaje(enOperacion, enOperacion + enMantenimiento),
                calcularPorcentaje(enMantenimiento, enOperacion + enMantenimiento)
        );
    }

    private static Double calcularPorcentaje(Long valor, Long total) {
        if (total == 0) {
            return 0.0;
        }
        return Math.round((valor * 100.0 / total) * 10.0) / 10.0;
    }
}

