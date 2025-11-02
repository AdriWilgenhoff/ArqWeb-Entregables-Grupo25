package edu.tudai.arq.mantenimientoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReporteUsoDTO {

    @Schema(description = "DTO de respuesta para el reporte de uso de monopatines", name = "ReporteUsoResponse")
    public record Response(
            @Schema(description = "ID del monopatín", example = "1")
            Long idMonopatin,

            @Schema(description = "Kilómetros totales recorridos", example = "1250.5")
            Double kilometrosTotales,

            @Schema(description = "Tiempo total de uso en minutos", example = "3600")
            Long tiempoUsoTotal,

            @Schema(description = "Tiempo en pausas en minutos (solo si incluirPausas=true)", example = "120")
            Long tiempoEnPausas,

            @Schema(description = "Tiempo sin pausas en minutos", example = "3480")
            Long tiempoSinPausas,

            @Schema(description = "Cantidad de viajes realizados", example = "45")
            Integer cantidadViajes,

            @Schema(description = "Estado actual del monopatín", example = "DISPONIBLE")
            String estadoActual,

            @Schema(description = "Indica si requiere mantenimiento", example = "true")
            Boolean requiereMantenimiento,

            @Schema(description = "Motivo por el cual requiere mantenimiento", example = "Superó los 1000 km")
            String motivoMantenimiento
    ) {
        public Response(Long idMonopatin, Double kilometrosTotales, Long tiempoUsoTotal,
                       Long tiempoEnPausas, Long tiempoSinPausas, Integer cantidadViajes,
                       String estadoActual) {
            this(idMonopatin, kilometrosTotales, tiempoUsoTotal, tiempoEnPausas, tiempoSinPausas,
                    cantidadViajes, estadoActual,
                    determinarSiRequiereMantenimiento(kilometrosTotales, tiempoUsoTotal),
                    obtenerMotivoMantenimiento(kilometrosTotales, tiempoUsoTotal));
        }

        private static Boolean determinarSiRequiereMantenimiento(Double km, Long tiempo) {
            final double KM_LIMITE = 1000.0;
            final long HORAS_LIMITE = 100;
            return km >= KM_LIMITE || (tiempo / 60) >= HORAS_LIMITE;
        }

        private static String obtenerMotivoMantenimiento(Double km, Long tiempo) {
            final double KM_LIMITE = 1000.0;
            final long HORAS_LIMITE = 100;

            if (km >= KM_LIMITE && (tiempo / 60) >= HORAS_LIMITE) {
                return String.format("Superó los %.0f km y las %d horas de uso", KM_LIMITE, HORAS_LIMITE);
            } else if (km >= KM_LIMITE) {
                return String.format("Superó los %.0f km", KM_LIMITE);
            } else if ((tiempo / 60) >= HORAS_LIMITE) {
                return String.format("Superó las %d horas de uso", HORAS_LIMITE);
            }
            return "No requiere mantenimiento";
        }
    }
}

