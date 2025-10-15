package edu.tudai.arq.integrador3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de reporte que muestra una Carrera con el total de estudiantes inscriptos.")
public record CarreraDTOCant(

        @Schema(description = "ID de la carrera")
        Long idCarrera,

        @Schema(description = "Nombre de la carrera")
        String nombre,

        @Schema(description = "Cantidad total de estudiantes inscriptos (COUNT)")
        Long cantInscriptos
) {}