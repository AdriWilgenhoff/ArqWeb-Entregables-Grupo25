package edu.tudai.arq.facturacionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class FacturacionDTO {

    /** DTO para crear Facturación - Recibe datos de viaje-service */
    @Schema(description = "DTO de entrada para crear una nueva Facturación", name = "FacturacionCreate")
    public record Create(
            @NotNull(message = "El ID de viaje es obligatorio")
            @Positive(message = "El ID de viaje debe ser positivo")
            @Schema(description = "ID del viaje facturado", example = "1")
            Long idViaje,

            @NotNull(message = "El ID de cuenta es obligatorio")
            @Positive(message = "El ID de cuenta debe ser positivo")
            @Schema(description = "ID de la cuenta que realizó el viaje", example = "1")
            Long idCuenta,

            @NotNull(message = "El tiempo total es obligatorio")
            @PositiveOrZero(message = "El tiempo total no puede ser negativo")
            @Schema(description = "Tiempo total del viaje en minutos", example = "50")
            Long tiempoTotal,

            @NotNull(message = "El tiempo sin pausas es obligatorio")
            @PositiveOrZero(message = "El tiempo sin pausas no puede ser negativo")
            @Schema(description = "Tiempo sin pausas (activo) en minutos", example = "30")
            Long tiempoSinPausas,

            @NotNull(message = "El tiempo en pausa normal es obligatorio")
            @PositiveOrZero(message = "El tiempo en pausa normal no puede ser negativo")
            @Schema(description = "Tiempo en pausa normal (≤15 min) en minutos", example = "10")
            Long tiempoPausaNormal,

            @NotNull(message = "El tiempo en pausa extendida es obligatorio")
            @PositiveOrZero(message = "El tiempo en pausa extendida no puede ser negativo")
            @Schema(description = "Tiempo en pausa extendida (>15 min) en minutos", example = "10")
            Long tiempoPausaExtendida
    ) {}

    /** DTO de salida para Facturación */
    @Schema(description = "DTO de respuesta para Facturaciones", name = "FacturacionResponse")
    public record Response(
            @Schema(description = "ID de la facturación", example = "1")
            Long id,

            @Schema(description = "ID del viaje", example = "1")
            Long idViaje,

            @Schema(description = "ID de la cuenta", example = "1")
            Long idCuenta,

            @Schema(description = "Fecha de facturación", example = "2024-11-01T10:30:00")
            String fecha,

            @Schema(description = "Monto total facturado", example = "350.50")
            Double montoTotal,

            @Schema(description = "Tiempo total del viaje en minutos", example = "30")
            Long tiempoTotal,

            @Schema(description = "Tiempo pausado en minutos", example = "5")
            Long tiempoPausado,

            @Schema(description = "Tiempo sin pausas en minutos", example = "25")
            Long tiempoSinPausas,

            @Schema(description = "ID de la tarifa normal aplicada", example = "1")
            Long idTarifaNormal,

            @Schema(description = "ID de la tarifa de pausa aplicada", example = "2")
            Long idTarifaPausa,

            @Schema(description = "ID de la tarifa extendida aplicada", example = "3")
            Long idTarifaExtendida
    ) {}

    /** DTO resumido para listados */
    @Schema(description = "DTO resumido de Facturaciones para listados", name = "FacturacionResumen")
    public record Resumen(
            @Schema(description = "ID de la facturación", example = "1")
            Long id,

            @Schema(description = "ID del viaje", example = "1")
            Long idViaje,

            @Schema(description = "ID de la cuenta", example = "1")
            Long idCuenta,

            @Schema(description = "Fecha de facturación", example = "2024-11-01T10:30:00")
            String fecha,

            @Schema(description = "Monto total", example = "350.50")
            Double montoTotal
    ) {}
}
