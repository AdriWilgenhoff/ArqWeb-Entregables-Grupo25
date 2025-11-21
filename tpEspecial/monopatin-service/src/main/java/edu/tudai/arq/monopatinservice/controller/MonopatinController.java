package edu.tudai.arq.monopatinservice.controller;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.monopatinservice.dto.ReporteUsoDTO;
import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import edu.tudai.arq.monopatinservice.exception.ApiError;
import edu.tudai.arq.monopatinservice.service.interfaces.MonopatinService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/monopatines")
@Tag(name = "Monopatines", description = "API para gestión de monopatines eléctricos")
public class MonopatinController {

    private final MonopatinService service;

    public MonopatinController(MonopatinService service) {
        this.service = service;
    }

    @Operation(
            summary = "Registra un nuevo monopatín",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/monopatines"
    )
    @ApiResponse(responseCode = "201", description = "Monopatín creado exitosamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o error de validación", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @PostMapping
    public ResponseEntity<MonopatinDTO.Response> create(@RequestBody @Valid MonopatinDTO.Create request) {
        MonopatinDTO.Response newMonopatin = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMonopatin);
    }

    @Operation(
            summary = "Obtiene un monopatín por ID",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/monopatines/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Detalles del monopatín", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @GetMapping("/{id}")
    public ResponseEntity<MonopatinDTO.Response> getById(@PathVariable Long id) {
        MonopatinDTO.Response monopatin = service.findById(id);
        return ResponseEntity.ok(monopatin);
    }

    @Operation(
            summary = "Lista todos los monopatines",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/monopatines"
    )
    @ApiResponse(responseCode = "200", description = "Lista de monopatines", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @GetMapping
    public ResponseEntity<List<MonopatinDTO.Response>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }


    @Operation(
            summary = "Actualiza un monopatín",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/monopatines/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Monopatín actualizado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Error de validación o transición de estado inválida", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @PutMapping("/{id}")
    public ResponseEntity<MonopatinDTO.Response> update(@PathVariable Long id, @RequestBody @Valid MonopatinDTO.Update request) {
        MonopatinDTO.Response updatedMonopatin = service.update(id, request);
        return ResponseEntity.ok(updatedMonopatin);
    }


    @Operation(
            summary = "Da de baja un monopatín (baja lógica)",
            description = "Requiere rol ADMINISTRADOR. Marca el monopatín como DADO_DE_BAJA. Acceso: http://localhost:8080/api/v1/monopatines/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Monopatín dado de baja exitosamente")
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "No se puede dar de baja un monopatín en uso", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cambiar estado del monopatín",
            description = "Requiere rol ADMINISTRADOR. Estados disponibles: DISPONIBLE, EN_USO, EN_MANTENIMIENTO, DADO_DE_BAJA. Acceso: http://localhost:8080/api/v1/monopatines/{id}/estado/{nuevoEstado}"
    )
    @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Transición inválida de estado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<MonopatinDTO.Response> cambiarEstado(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado del monopatín (DISPONIBLE, EN_USO, EN_MANTENIMIENTO, DADO_DE_BAJA)", required = true, schema = @Schema(implementation = EstadoMonopatin.class))
            @PathVariable String nuevoEstado) {

        MonopatinDTO.Response updatedMonopatin = service.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(updatedMonopatin);
    }

    @Operation(
            summary = "Buscar monopatines cercanos (Requerimiento g)",
            description = "Accesible para usuarios autenticados. Retorna solo monopatines DISPONIBLES ordenados por cercanía. Acceso: http://localhost:8080/api/v1/monopatines/cercanos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de monopatines cercanos disponibles", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @GetMapping("/cercanos")
    public ResponseEntity<List<MonopatinDTO.Response>> findMonopatinesCercanos(
            @RequestParam Integer latitud,
            @RequestParam Integer longitud,
            @RequestParam(required = false, defaultValue = "1.0") Double radioKm) {

        List<MonopatinDTO.Response> monopatines = service.findMonopatinesCercanos(latitud, longitud, radioKm);
        return ResponseEntity.ok(monopatines);
    }

    // ==================== REPORTES ====================

    @Operation(
            summary = "Monopatines con más de X viajes en un año (Requerimiento c)",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/monopatines/con-mas-viajes"
    )
    @ApiResponse(responseCode = "200", description = "Lista de monopatines que cumplen el criterio", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @GetMapping("/con-mas-viajes")
    public ResponseEntity<List<MonopatinDTO.Response>> getMonopatinesConMasViajes(
            @RequestParam Integer cantidad,
            @RequestParam Integer anio) {

        List<MonopatinDTO.Response> monopatines = service.findMonopatinesConMasDeXViajes(cantidad, anio);
        return ResponseEntity.ok(monopatines);
    }

    @Operation(
            summary = "Reporte de operación vs mantenimiento (Requerimiento e)",
            description = "Requiere rol ADMINISTRADOR. Consulta la cantidad de monopatines en operación vs en mantenimiento. Acceso: http://localhost:8080/api/v1/monopatines/reporte-operacion"
    )
    @ApiResponse(responseCode = "200", description = "Reporte de operación generado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReporteOperacionDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @GetMapping("/reporte-operacion")
    public ResponseEntity<ReporteOperacionDTO> getReporteOperacion() {
        ReporteOperacionDTO reporte = service.getReporteOperacion();
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/reporte-uso-kilometros")
    @Operation(
            summary = "Reporte de uso por kilómetros (Requerimiento a)",
            description = "Requiere rol MANTENIMIENTO o ADMINISTRADOR. Genera reporte de uso de monopatines por kilómetros para establecer si requiere mantenimiento. incluirPausas=true muestra también tiempos de pausa. Acceso: http://localhost:8080/api/v1/monopatines/reporte-uso-kilometros"
    )
    @ApiResponse(responseCode = "200", description = "Reporte de uso generado. Ordenado por kilómetros descendente.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol MANTENIMIENTO o ADMINISTRADOR")
    public ResponseEntity<List<?>> getReporteUsoPorKilometros(
            @RequestParam(defaultValue = "false") @Parameter(description = "true = muestra kms + tiempo sin pausas + tiempo de pausas | false = muestra kms + tiempo sin pausas") boolean incluirPausas) {
        List<?> reporte = service.getReporteUsoPorKilometros(incluirPausas);
        return ResponseEntity.ok(reporte);
    }
}
