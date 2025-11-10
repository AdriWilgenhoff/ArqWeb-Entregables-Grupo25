package edu.tudai.arq.mantenimientoservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class MantenimientoDTO {

    @Schema(description = "DTO de entrada para crear un nuevo mantenimiento de monopatín", name = "MantenimientoCreate")
    public record Create(

            @NotNull(message = "El ID del monopatín es obligatorio")
            @Schema(description = "Identificador del monopatín en mantenimiento", example = "42")
            Long idMonopatin,

            @NotNull(message = "La fecha y hora de inicio son obligatorias")
            @Schema(description = "Fecha y hora de inicio del mantenimiento", example = "2025-11-01T09:30:00")
            LocalDateTime fechaHoraInicio,

            @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
            @Schema(description = "Descripción de la tarea o motivo del mantenimiento", example = "Revisión de frenos y calibración de sensores")
            String descripcion,

            @NotNull(message = "Los kilómetros al iniciar son obligatorios")
            @PositiveOrZero(message = "Los kilómetros deben ser mayores o iguales a 0")
            @Schema(description = "Cantidad de kilómetros recorridos al iniciar el mantenimiento", example = "1250.5")
            Double kilometrosAlIniciar,

            @NotNull(message = "El tiempo de uso al iniciar es obligatorio")
            @PositiveOrZero(message = "El tiempo de uso debe ser mayor o igual a 0")
            @Schema(description = "Tiempo total de uso acumulado al iniciar (en minutos)", example = "1830")
            Long tiempoUsoAlIniciar

    ) {}

    @Schema(description = "DTO de entrada para actualizar un mantenimiento de monopatín", name = "MantenimientoUpdate")
    public record Update(

            @Schema(description = "Fecha y hora de fin del mantenimiento", example = "2025-11-01T12:00:00")
            LocalDateTime fechaHoraFin
    ) {}

    @Schema(description = "DTO de respuesta para los registros de mantenimiento de monopatines", name = "MantenimientoResponse")
    public record Response(

            @Schema(description = "ID del mantenimiento", example = "10")
            Long idMantenimiento,

            @Schema(description = "ID del monopatín asociado", example = "42")
            Long idMonopatin,

            @Schema(description = "Fecha y hora de inicio del mantenimiento", example = "2025-11-01T09:30:00")
            LocalDateTime fechaHoraInicio,

            @Schema(description = "Fecha y hora de fin del mantenimiento", example = "2025-11-01T12:00:00")
            LocalDateTime fechaHoraFin,

            @Schema(description = "Descripción del mantenimiento realizado", example = "Revisión de frenos y calibración de sensores")
            String descripcion,

            @Schema(description = "Kilómetros recorridos al iniciar el mantenimiento", example = "1250.5")
            Double kilometrosAlIniciar,

            @Schema(description = "Tiempo de uso acumulado al iniciar (en minutos)", example = "1830")
            Long tiempoUsoAlIniciar,

            @Schema(description = "Identificador del encargado que realizó el mantenimiento", example = "E123")
            String idEncargado
    ) {}
}

