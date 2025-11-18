package edu.tudai.arq.viajeservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "DTO de respuesta para el servicio de chat IA")
public class ChatResponseDTO {

    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean ok;

    @Schema(description = "Mensaje descriptivo del resultado", example = "Consulta ejecutada con éxito")
    private String mensaje;

    @Schema(description = "Datos retornados por la consulta SQL")
    private Object datos;
}

