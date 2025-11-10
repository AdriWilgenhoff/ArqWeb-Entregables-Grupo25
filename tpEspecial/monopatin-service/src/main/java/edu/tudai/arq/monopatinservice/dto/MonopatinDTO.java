package edu.tudai.arq.monopatinservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import jakarta.validation.constraints.*;

public class MonopatinDTO {

    @Schema(description = "DTO de entrada para crear un nuevo Monopatín", name = "MonopatinCreate")
    public record Create(
            @NotNull(message = "El estado es obligatorio")
            @Schema(description = "Estado inicial del monopatín", example = "DISPONIBLE")
            EstadoMonopatin estado,

            @NotNull(message = "La latitud es obligatoria")
            @Schema(description = "Latitud de la ubicación del monopatín", example = "100")
            Integer latitud,

            @NotNull(message = "La longitud es obligatoria")
            @Schema(description = "Longitud de la ubicación del monopatín", example = "200")
            Integer longitud
    ) {}

    @Schema(description = "DTO de entrada para actualizar un Monopatín existente", name = "MonopatinUpdate")
    public record Update(
            @Schema(description = "Estado del monopatín", example = "EN_USO")
            EstadoMonopatin estado,

            @Schema(description = "Latitud de la ubicación del monopatín", example = "100")
            Integer latitud,

            @Schema(description = "Longitud de la ubicación del monopatín", example = "200")
            Integer longitud,

            @PositiveOrZero(message = "Los kilómetros deben ser mayores o iguales a 0")
            @Schema(description = "Kilómetros totales recorridos", example = "125.5")
            Double kilometrosTotales,

            @PositiveOrZero(message = "El tiempo de uso debe ser mayor o igual a 0")
            @Schema(description = "Tiempo total de uso en minutos", example = "320")
            Long tiempoUsoTotal,

            @PositiveOrZero(message = "El tiempo de pausas debe ser mayor o igual a 0")
            @Schema(description = "Tiempo total en pausas en minutos", example = "45")
            Long tiempoPausas
    ) {}

    @Schema(description = "DTO de respuesta que incluye todas las propiedades del Monopatín", name = "MonopatinResponse")
    public record Response(
            @Schema(description = "ID del monopatín", example = "1")
            Long id,

            @Schema(description = "Estado del monopatín", example = "DISPONIBLE")
            EstadoMonopatin estado,

            @Schema(description = "Latitud de la ubicación", example = "100")
            Integer latitud,

            @Schema(description = "Longitud de la ubicación", example = "200")
            Integer longitud,

            @Schema(description = "Kilómetros totales recorridos", example = "125.5")
            Double kilometrosTotales,

            @Schema(description = "Tiempo total de uso en minutos", example = "320")
            Long tiempoUsoTotal,

            @Schema(description = "Tiempo total en pausas en minutos", example = "45")
            Long tiempoPausas
    ) {}
}