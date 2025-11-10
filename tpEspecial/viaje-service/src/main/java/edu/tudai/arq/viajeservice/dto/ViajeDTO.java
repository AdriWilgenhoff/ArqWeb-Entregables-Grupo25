package edu.tudai.arq.viajeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;


public class ViajeDTO {

    /** DTO para crear Viaje (iniciar viaje) */
    @Schema(description = "DTO de entrada para iniciar un nuevo Viaje", name = "ViajeCreate")
    public record Create(
            @NotNull(message = "El ID de cuenta es obligatorio")
            @Positive(message = "El ID de cuenta debe ser positivo")
            Long idCuenta,

            @NotNull(message = "El ID de usuario es obligatorio")
            @Positive(message = "El ID de usuario debe ser positivo")
            Long idUsuario,

            @NotNull(message = "El ID de monopatín es obligatorio")
            @Positive(message = "El ID de monopatín debe ser positivo")
            Long idMonopatin,

            @NotNull(message = "El ID de parada de inicio es obligatorio")
            @Positive(message = "El ID de parada de inicio debe ser positivo")
            Long idParadaInicio
    ) {}

    /** DTO para finalizar Viaje */
    @Schema(description = "DTO de entrada para finalizar un Viaje", name = "ViajeFinalizacion")
    public record Finalizacion(
            @NotNull(message = "El ID de parada de fin es obligatorio")
            @Positive(message = "El ID de parada de fin debe ser positivo")
            @Schema(description = "ID de la parada donde finaliza el viaje", example = "7")
            Long idParadaFin,

            @NotNull(message = "Los kilómetros recorridos son obligatorios")
            @PositiveOrZero(message = "Los kilómetros recorridos no pueden ser negativos")
            @Schema(description = "Kilómetros recorridos durante el viaje", example = "5.2")
            Double kilometrosRecorridos
    ) {}

    /** DTO de salida para Viaje */
    @Schema(description = "DTO de respuesta para Viajes", name = "ViajeResponse")
    public record Response(
            @Schema(description = "ID del viaje", example = "1")
            Long id,

            @Schema(description = "ID de la cuenta", example = "1")
            Long idCuenta,

            @Schema(description = "ID del usuario", example = "1")
            Long idUsuario,

            @Schema(description = "ID del monopatín", example = "5")
            Long idMonopatin,

            @Schema(description = "Fecha y hora de inicio del viaje", example = "2024-11-01T10:30:00")
            String fechaHoraInicio,

            @Schema(description = "Fecha y hora de fin del viaje", example = "2024-11-01T11:00:00")
            String fechaHoraFin,

            @Schema(description = "ID de la parada de inicio", example = "3")
            Long idParadaInicio,

            @Schema(description = "ID de la parada de fin", example = "7")
            Long idParadaFin,

            @Schema(description = "Kilómetros recorridos", example = "5.2")
            Double kilometrosRecorridos,

            @Schema(description = "Estado del viaje", example = "EN_CURSO")
            String estado,

            @Schema(description = "Costo total del viaje", example = "350.50")
            Double costoTotal,

            @Schema(description = "Número de pausas realizadas", example = "2")
            Integer numeroPausas,

            @Schema(description = "Tiempo total del viaje en minutos", example = "50")
            Long tiempoTotal,

            @Schema(description = "Tiempo total pausado en minutos", example = "20")
            Long tiempoPausado,

            @Schema(description = "Tiempo en pausa extendida (>15 min) en minutos", example = "10")
            Long tiempoPausaExtendida,

            @Schema(description = "Tiempo en pausa normal (≤15 min) en minutos", example = "10")
            Long tiempoPausaNormal,

            @Schema(description = "Tiempo sin pausas (activo) en minutos", example = "30")
            Long tiempoSinPausas
    ) {}

    /** DTO de salida resumido para listados */
    @Schema(description = "DTO resumido de Viajes para listados", name = "ViajeResumen")
    public record Resumen(
            @Schema(description = "ID del viaje", example = "1")
            Long id,

            @Schema(description = "ID del usuario", example = "1")
            Long idUsuario,

            @Schema(description = "ID del monopatín", example = "5")
            Long idMonopatin,

            @Schema(description = "Fecha de inicio", example = "2024-11-01T10:30:00")
            String fechaHoraInicio,

            @Schema(description = "Kilómetros recorridos", example = "5.2")
            Double kilometrosRecorridos,

            @Schema(description = "Estado del viaje", example = "FINALIZADO")
            String estado,

            @Schema(description = "Costo total", example = "350.50")
            Double costoTotal
    ) {}
}



