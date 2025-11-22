package edu.tudai.arq.viajeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReporteUsuarioDTO {

    @Schema(description = "DTO para reporte de usuarios más activos", name = "ReporteUsuarioActivoResponse")
    public record UsuarioActivo(
            @Schema(description = "ID del usuario", example = "1")
            Long idUsuario,

            @Schema(description = "Cantidad de viajes realizados", example = "45")
            Integer cantidadViajes,

            @Schema(description = "Kilómetros totales recorridos", example = "230.5")
            Double kilometrosTotales,

            @Schema(description = "Tiempo total de uso en minutos", example = "1850")
            Long tiempoTotalMinutos
    ) {}
}

