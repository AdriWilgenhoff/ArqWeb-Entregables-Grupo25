package edu.tudai.arq.paradaservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class ParadaDTO {

    @Schema(description = "DTO de entrada para crear una nueva parada", name = "ParadaCreate")
    public record Create(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
            @Schema(description = "Nombre de la parada", example = "Parada Central Plaza")
            String nombre,

            @NotNull(message = "La latitud es obligatoria")
            @Schema(description = "Latitud de la ubicación de la parada", example = "100")
            Integer latitud,

            @NotNull(message = "La longitud es obligatoria")
            @Schema(description = "Longitud de la ubicación de la parada", example = "200")
            Integer longitud,

            @NotNull(message = "La capacidad es obligatoria")
            @Min(value = 1, message = "La capacidad debe ser mayor a 0")
            @Schema(description = "Capacidad máxima de monopatines en la parada", example = "20")
            Integer capacidad
    ) {}

    @Schema(description = "DTO de entrada para actualizar una parada existente", name = "ParadaUpdate")
    public record Update(
            @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
            @Schema(description = "Nombre de la parada", example = "Parada Central Plaza")
            String nombre,

            @Schema(description = "Latitud de la ubicación de la parada", example = "100")
            Integer latitud,

            @Schema(description = "Longitud de la ubicación de la parada", example = "200")
            Integer longitud,

            @Min(value = 1, message = "La capacidad debe ser mayor a 0")
            @Schema(description = "Capacidad máxima de monopatines en la parada", example = "20")
            Integer capacidad
    ) {}

    @Schema(description = "DTO de respuesta para paradas", name = "ParadaResponse")
    public record Response(
            @Schema(description = "ID de la parada (MongoDB ObjectId)", example = "507f1f77bcf86cd799439011")
            String id,

            @Schema(description = "Nombre de la parada", example = "Parada Central Plaza")
            String nombre,

            @Schema(description = "Latitud de la ubicación", example = "100")
            Integer latitud,

            @Schema(description = "Longitud de la ubicación", example = "200")
            Integer longitud,

            @Schema(description = "Capacidad máxima de monopatines", example = "20")
            Integer capacidad
    ) { }
}

