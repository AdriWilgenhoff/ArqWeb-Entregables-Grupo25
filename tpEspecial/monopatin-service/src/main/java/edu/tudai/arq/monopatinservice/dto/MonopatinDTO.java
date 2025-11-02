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
            @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
            @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
            @Schema(description = "Latitud de la ubicación del monopatín", example = "-37.3214")
            Double latitud,

            @NotNull(message = "La longitud es obligatoria")
            @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
            @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
            @Schema(description = "Longitud de la ubicación del monopatín", example = "-59.1352")
            Double longitud
    ) {}

    @Schema(description = "DTO de entrada para actualizar un Monopatín existente", name = "MonopatinUpdate")
    public record Update(
            @Schema(description = "Estado del monopatín", example = "EN_USO")
            EstadoMonopatin estado,

            @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
            @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
            @Schema(description = "Latitud de la ubicación del monopatín", example = "-37.3214")
            Double latitud,

            @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
            @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
            @Schema(description = "Longitud de la ubicación del monopatín", example = "-59.1352")
            Double longitud,

            @PositiveOrZero(message = "Los kilómetros deben ser mayores o iguales a 0")
            @Schema(description = "Kilómetros totales recorridos", example = "125.5")
            Double kilometrosTotales,

            @PositiveOrZero(message = "El tiempo de uso debe ser mayor o igual a 0")
            @Schema(description = "Tiempo total de uso en minutos", example = "320")
            Long tiempoUsoTotal
    ) {}

    @Schema(description = "DTO de respuesta que incluye todas las propiedades del Monopatín", name = "MonopatinResponse")
    public record Response(
            @Schema(description = "ID del monopatín", example = "1")
            Long id,

            @Schema(description = "Estado del monopatín", example = "DISPONIBLE")
            EstadoMonopatin estado,

            @Schema(description = "Latitud de la ubicación", example = "-37.3214")
            Double latitud,

            @Schema(description = "Longitud de la ubicación", example = "-59.1352")
            Double longitud,

            @Schema(description = "Kilómetros totales recorridos", example = "125.5")
            Double kilometrosTotales,

            @Schema(description = "Tiempo total de uso en minutos", example = "320")
            Long tiempoUsoTotal
    ) {}
}