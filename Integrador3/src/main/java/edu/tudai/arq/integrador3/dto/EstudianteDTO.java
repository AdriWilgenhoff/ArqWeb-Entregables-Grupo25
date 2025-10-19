package edu.tudai.arq.integrador3.dto;

import edu.tudai.arq.integrador3.model.Genero;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class EstudianteDTO {

    /** DTO para crear Estudiante **/
    @Schema(description = "DTO de entrada para crear un nuevo Estudiante" , name = "EstudianteCreate")
    public record Create(
            @NotBlank(message = "El nombre es obligatorio")
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            String apellido,

            @NotNull(message = "La fecha de nacimiento es obligatoria")
            @Schema(description = "Fecha de nacimiento", example = "1988-07-31")
            @Past(message = "La fecha de nacimiento debe ser en el pasado")
            LocalDate fechaNacimiento,

            @NotNull(message = "El género es obligatorio")
            Genero genero,

            @Schema(description = "Documento Nacional de Identidad", example = "38123456")
            @NotNull(message = "El DNI es obligatorio")
            @Positive(message = "El DNI debe ser un número positivo")
            @Digits(integer = 20, fraction = 0, message = "El DNI debe ser un número entero")
            Long dni,

            @NotBlank(message = "La ciudad de residencia es obligatoria")
            String ciudadResidencia,

            @Schema(description = "Libreta Universitaria", example = "12345")
            @NotNull(message = "La Libreta Universitaria (LU) es obligatoria")
            @Positive(message = "El LU debe ser un número positivo")
            Long lu
    ) {}

    /** DTO para actualizar **/
    @Schema(description = "DTO de entrada para actualizar un Estudiante" , name = "EstudianteUpdate")
    public record Update(
            @NotBlank(message = "El nombre es obligatorio")
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            String apellido,

            @NotNull(message = "La fecha de nacimiento es obligatoria")
            @Past(message = "La fecha de nacimiento debe ser en el pasado")
            LocalDate fechaNacimiento,

            @NotNull(message = "El género es obligatorio")
            Genero genero,

            @NotBlank(message = "La ciudad de residencia es obligatoria")
            String ciudadResidencia
    ) {}


    /** DTO de salida **/
    @Schema(description = "DTO de respuesta completa para Estudiantes" , name = "EstudianteResponse")
    public record Response(
            @Schema(description = "ID del estudiante", example = "1")
            Long idEstudiante,

            @Schema(description = "Nombre del estudiante", example = "Juan")
            String nombre,

            @Schema(description = "Apellido del estudiante", example = "Pérez")
            String apellido,

            @Schema(description = "Fecha de nacimiento", example = "1995-10-20")
            LocalDate fechaNacimiento,

            @Schema(description = "Edad del estudiante", example = "29")
            Integer edad,

            @Schema(description = "Género", example = "MASCULINO")
            Genero genero,

            @Schema(description = "Documento Nacional de Identidad", example = "38123456")
            Long dni,

            @Schema(description = "Ciudad de residencia", example = "La Plata")
            String ciudadResidencia,

            @Schema(description = "Libreta Universitaria", example = "12345")
            Long lu
    ) {}
}