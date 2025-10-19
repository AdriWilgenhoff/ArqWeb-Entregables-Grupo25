package edu.tudai.arq.integrador3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class InscripcionDTO {

    /** DTO para crear Inscripción **/
    @Schema(description = "DTO de entrada para crear una nueva Inscripción", name = "InscripcionCreate")
    public record Create(
            @NotNull
            @Schema(description = "ID del estudiante", example = "1")
            Long estudianteId,

            @Schema(description = "ID de la carrera", example = "7")
            @NotNull Long carreraId
    ) {}

    /** DTO de salida **/
    @Schema(description = "DTO de respuesta para Inscripciones", name = "InscripcionResponse")
    public record Response(
            @Schema(description = "ID del estudiante", example = "1")
            Long estudianteId,

            @Schema(description = "ID de la carrera", example = "7")
            Long carreraId,

            @Schema(description = "Fecha de inscripción", example = "2025-03-01")
            LocalDate fechaInscripcion,

            @Schema(description = "Fecha de egreso (si corresponde)", example = "2027-12-15")
            LocalDate fechaEgreso
    ) {}
}