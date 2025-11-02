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
            @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
            @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
            @Schema(description = "Latitud de la ubicación de la parada", example = "-37.3214")
            Double latitud,

            @NotNull(message = "La longitud es obligatoria")
            @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
            @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
            @Schema(description = "Longitud de la ubicación de la parada", example = "-59.1352")
            Double longitud,

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

            @DecimalMin(value = "-90.0", message = "La latitud debe ser mayor o igual a -90")
            @DecimalMax(value = "90.0", message = "La latitud debe ser menor o igual a 90")
            @Schema(description = "Latitud de la ubicación de la parada", example = "-37.3214")
            Double latitud,

            @DecimalMin(value = "-180.0", message = "La longitud debe ser mayor o igual a -180")
            @DecimalMax(value = "180.0", message = "La longitud debe ser menor o igual a 180")
            @Schema(description = "Longitud de la ubicación de la parada", example = "-59.1352")
            Double longitud,

            @Min(value = 1, message = "La capacidad debe ser mayor a 0")
            @Schema(description = "Capacidad máxima de monopatines en la parada", example = "20")
            Integer capacidad,

            @Min(value = 0, message = "Los monopatines disponibles no pueden ser negativos")
            @Schema(description = "Cantidad de monopatines actualmente en la parada", example = "5")
            Integer monopatinesDisponibles
    ) {}

    @Schema(description = "DTO de respuesta para paradas", name = "ParadaResponse")
    public record Response(
            @Schema(description = "ID de la parada", example = "1")
            Long id,

            @Schema(description = "Nombre de la parada", example = "Parada Central Plaza")
            String nombre,

            @Schema(description = "Latitud de la ubicación", example = "-37.3214")
            Double latitud,

            @Schema(description = "Longitud de la ubicación", example = "-59.1352")
            Double longitud,

            @Schema(description = "Capacidad máxima de monopatines", example = "20")
            Integer capacidad,

            @Schema(description = "Cantidad de monopatines disponibles actualmente", example = "5")
            Integer monopatinesDisponibles,

            @Schema(description = "Porcentaje de ocupación", example = "25.0")
            Double porcentajeOcupacion,

            @Schema(description = "Indica si la parada tiene espacio disponible", example = "true")
            Boolean tieneEspacio
    ) {
        public Response(Long id, String nombre, Double latitud, Double longitud, Integer capacidad, Integer monopatinesDisponibles) {
            this(id, nombre, latitud, longitud, capacidad, monopatinesDisponibles,
                    capacidad > 0 ? (monopatinesDisponibles * 100.0 / capacidad) : 0.0,
                    monopatinesDisponibles < capacidad);
        }
    }
}

