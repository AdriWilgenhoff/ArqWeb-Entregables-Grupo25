package edu.tudai.arq.mantenimientoservice.controller;

import edu.tudai.arq.mantenimientoservice.dto.MantenimientoDTO;
import edu.tudai.arq.mantenimientoservice.dto.ReporteUsoDTO;
import edu.tudai.arq.mantenimientoservice.exception.ApiError;
import edu.tudai.arq.mantenimientoservice.service.interfaces.MantenimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mantenimientos")
@Validated
@Tag(name = "Mantenimientos", description = "API para gestión de mantenimientos de monopatines")
public class MantenimientoController {

    private final MantenimientoService service;

    public MantenimientoController(MantenimientoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo registro de mantenimiento")
    @ApiResponse(responseCode = "201", description = "Mantenimiento creado exitosamente",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MantenimientoDTO.Response> create(@Valid @RequestBody MantenimientoDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un mantenimiento existente")
    @ApiResponse(responseCode = "200", description = "Mantenimiento actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MantenimientoDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody MantenimientoDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar un mantenimiento (registrar fecha de fin)")
    @ApiResponse(responseCode = "200", description = "Mantenimiento finalizado exitosamente",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MantenimientoDTO.Response> finalizar(
            @PathVariable Long id,
            @Valid @RequestBody MantenimientoDTO.Update finishData) {
        var out = service.finalizar(id, finishData);
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un registro de mantenimiento")
    @ApiResponse(responseCode = "204", description = "Mantenimiento eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un mantenimiento por ID")
    @ApiResponse(responseCode = "200", description = "Mantenimiento encontrado",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MantenimientoDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos los mantenimientos")
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos")
    public ResponseEntity<List<MantenimientoDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar mantenimientos activos (sin fecha de finalización)")
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos activos")
    public ResponseEntity<List<MantenimientoDTO.Response>> findActivos() {
        return ResponseEntity.ok(service.findActivos());
    }

    @GetMapping("/finalizados")
    @Operation(summary = "Listar mantenimientos finalizados (con fecha de finalización)")
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos finalizados")
    public ResponseEntity<List<MantenimientoDTO.Response>> findFinalizados() {
        return ResponseEntity.ok(service.findFinalizados());
    }

    @PutMapping("/monopatin/{idMonopatin}/marcar")
    @Operation(summary = "Marcar un monopatín como en mantenimiento (no disponible)")
    @ApiResponse(responseCode = "200", description = "Monopatín marcado como en mantenimiento")
    public ResponseEntity<Void> marcarEnMantenimiento(@PathVariable Long idMonopatin) {
        service.marcarEnMantenimiento(idMonopatin);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/monopatin/{idMonopatin}/desmarcar")
    @Operation(summary = "Desmarcar un monopatín (habilitarlo para uso)")
    @ApiResponse(responseCode = "200", description = "Monopatín desmarcado y habilitado")
    public ResponseEntity<Void> desmarcarMantenimiento(
            @PathVariable Long idMonopatin,
            @RequestParam(required = false) Long idParadaDestino) {
        service.desmarcarMantenimiento(idMonopatin, idParadaDestino);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/estadisticas/operativos-vs-mantenimiento")
    @Operation(summary = "Obtener cantidad de monopatines en operación vs en mantenimiento")
    @ApiResponse(responseCode = "200", description = "Estadísticas de monopatines")
    public ResponseEntity<Map<String, Long>> operativosVsMantenimiento() {
        return ResponseEntity.ok(service.operativosVsMantenimiento());
    }

    @GetMapping("/reporte-uso")
    @Operation(
            summary = "Generar reporte de uso de monopatines por kilómetros (Requerimiento a)",
            description = "Como encargado de mantenimiento quiero poder generar un reporte de uso de monopatines " +
                    "por kilómetros para establecer si un monopatín requiere de mantenimiento. " +
                    "Este reporte debe poder configurarse para incluir (o no) los tiempos de pausa. " +
                    "Los monopatines se ordenan por kilómetros descendente (los que más necesitan mantenimiento primero). " +
                    "Un monopatín requiere mantenimiento si: superó los 1000 km O superó las 100 horas de uso."
    )
    @ApiResponse(responseCode = "200", description = "Reporte de uso generado exitosamente",
            content = @Content(schema = @Schema(implementation = ReporteUsoDTO.Response.class)))
    public ResponseEntity<List<ReporteUsoDTO.Response>> generarReporteUso(
            @RequestParam(required = false, defaultValue = "true")
            @Parameter(description = "Incluir tiempos de pausa en el cálculo", example = "true")
            boolean incluirPausas) {
        return ResponseEntity.ok(service.generarReporteUso(incluirPausas));
    }
}

