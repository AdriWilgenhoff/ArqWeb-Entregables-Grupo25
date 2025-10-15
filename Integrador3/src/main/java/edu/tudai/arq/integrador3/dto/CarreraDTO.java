package edu.tudai.arq.integrador3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class CarreraDTO {

    /** DTO para crear Carrera */
    @Schema(description = "DTO de entrada para crear una nueva Carrera" , name = "CarreraCreate")
    public record Create(
            @NotBlank(message = "El nombre de la carrera es obligatorio")
            @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
            String nombre,

            @NotNull(message = "La duración en años es obligatoria")
            @Min(value = 1, message = "La duración debe ser al menos 1 año")
            @Max(value = 15, message = "La duración máxima es de 15 años")
            Integer duracionAnios
    ) {}

    /** DTO para actualizar (solo campos editables) */
    @Schema(description = "DTO de entrada para actualizar una Carrera" , name = "CarreraUpdate")
    public record Update(
            @NotBlank(message = "El nombre de la carrera es obligatorio")
            String nombre,

            @NotNull(message = "La duración en años es obligatoria")
            @Min(value = 1)
            @Max(value = 15)
            Integer duracion
    ) {}

    /** DTO de salida (lo que retorna la API) */
    @Schema(description = "DTO de respuesta para Carreras" , name = "CarreraResponse")
    public record Response(
            @Schema(description = "ID de la carrera", example = "10")
            Long idCarrera,

            @Schema(description = "Nombre completo de la carrera", example = "Ingeniería de Sistemas")
            String nombre,

            @Schema(description = "Duracion en años de la carrera", example = "5")
            Integer duracion

    ) {}

    // ---

    // NOTA: Se incluye CarreraDTOCant para referencia, aunque no es un CRUD DTO.
    // Esta clase probablemente necesita el Constructor Expression para funcionar en @Query.
    // Ejemplo:
    // public record CarreraDTOCant(Carrera carrera, Long cantidadInscriptos) {}
}