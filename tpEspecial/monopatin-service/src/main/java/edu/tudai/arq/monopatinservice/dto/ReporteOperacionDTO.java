package edu.tudai.arq.monopatinservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta para el reporte de operación vs mantenimiento", name = "ReporteOperacionResponse")
public record ReporteOperacionDTO(
        @Schema(description = "Cantidad de monopatines en operación (DISPONIBLE + EN_USO)", example = "45")
        Long enOperacion,

        @Schema(description = "Cantidad de monopatines en mantenimiento", example = "5")
        Long enMantenimiento,

        @Schema(description = "Porcentaje de monopatines en operación", example = "90.0")
        Double porcentajeOperacion,

        @Schema(description = "Porcentaje de monopatines en mantenimiento", example = "10.0")
        Double porcentajeMantenimiento
) {
}

