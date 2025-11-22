package edu.tudai.arq.mantenimientoservice.controller;

import edu.tudai.arq.mantenimientoservice.dto.MantenimientoDTO;
import edu.tudai.arq.mantenimientoservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.mantenimientoservice.exception.ApiError;
import edu.tudai.arq.mantenimientoservice.service.interfaces.MantenimientoService;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/api/v1/mantenimientos")
@Validated
@Tag(name = "Mantenimientos", description = "API para gestión de mantenimientos de monopatines.")
public class MantenimientoController {

    private final MantenimientoService service;

    public MantenimientoController(MantenimientoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo registro de mantenimiento",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos"
    )
    @ApiResponse(responseCode = "201", description = "Mantenimiento creado exitosamente",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<MantenimientoDTO.Response> create(@Valid @RequestBody MantenimientoDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un mantenimiento existente",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Mantenimiento actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<MantenimientoDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody MantenimientoDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @PutMapping("/{id}/finalizar")
    @Operation(
            summary = "Finalizar un mantenimiento (registrar fecha de fin)",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/{id}/finalizar"
    )
    @ApiResponse(responseCode = "200", description = "Mantenimiento finalizado exitosamente",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<MantenimientoDTO.Response> finalizar(
            @PathVariable Long id,
            @Valid @RequestBody MantenimientoDTO.Update finishData) {
        var out = service.finalizar(id, finishData);
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un registro de mantenimiento",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Mantenimiento eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener un mantenimiento por ID",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Mantenimiento encontrado",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<MantenimientoDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los mantenimientos",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<List<MantenimientoDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/activos")
    @Operation(
            summary = "Listar mantenimientos activos (sin fecha de finalización)",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/activos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos activos")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<List<MantenimientoDTO.Response>> findActivos() {
        return ResponseEntity.ok(service.findActivos());
    }

    @GetMapping("/finalizados")
    @Operation(
            summary = "Listar mantenimientos finalizados (con fecha de finalización)",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/finalizados"
    )
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos finalizados")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<List<MantenimientoDTO.Response>> findFinalizados() {
        return ResponseEntity.ok(service.findFinalizados());
    }

    // ==================== REPORTES ====================

    @GetMapping("/estadisticas/operativos-vs-mantenimiento")
    @Operation(
            summary = "Obtener cantidad de monopatines en operación vs en mantenimiento (Antes era el requerimiento e)",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/mantenimientos/estadisticas/operativos-vs-mantenimiento"
    )
    @ApiResponse(responseCode = "200", description = "Estadísticas de monopatines",
            content = @Content(schema = @Schema(implementation = ReporteOperacionDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<ReporteOperacionDTO> operativosVsMantenimiento() {
        return ResponseEntity.ok(service.operativosVsMantenimiento());
    }
}

