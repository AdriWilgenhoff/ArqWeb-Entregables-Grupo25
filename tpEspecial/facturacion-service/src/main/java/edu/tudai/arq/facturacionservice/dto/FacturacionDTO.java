package edu.tudai.arq.facturacionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class FacturacionDTO {

    /** DTO para crear Facturación */
    @Schema(description = "DTO de entrada para crear una nueva Facturación", name = "FacturacionCreate")
    public record Create(
            @NotNull(message = "El ID de viaje es obligatorio")
            @Positive(message = "El ID de viaje debe ser positivo")
            Long idViaje,

            @NotNull(message = "El ID de cuenta es obligatorio")
            @Positive(message = "El ID de cuenta debe ser positivo")
            Long idCuenta,

            @NotNull(message = "El tiempo total es obligatorio")
            @PositiveOrZero(message = "El tiempo total no puede ser negativo")
            Long tiempoTotal,

            @NotNull(message = "El tiempo pausado es obligatorio")
            @PositiveOrZero(message = "El tiempo pausado no puede ser negativo")
            Long tiempoPausado,

            @NotNull(message = "El ID de tarifa normal es obligatorio")
            @Positive(message = "El ID de tarifa normal debe ser positivo")
            Long idTarifaNormal,

            @Positive(message = "El ID de tarifa extendida debe ser positivo si se proporciona")
            Long idTarifaExtendida,

            @NotNull(message = "El monto total es obligatorio")
            @PositiveOrZero(message = "El monto total no puede ser negativo")
            Double montoTotal
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

            @Schema(description = "ID de la tarifa extendida aplicada", example = "2")
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
