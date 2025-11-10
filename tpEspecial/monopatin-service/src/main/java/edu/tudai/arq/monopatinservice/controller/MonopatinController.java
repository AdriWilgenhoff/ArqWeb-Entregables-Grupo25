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

    // TODO: JWT - Requiere rol ADMIN - Agregar monopatín
    @Operation(summary = "Registra un nuevo monopatín",description = "Crea un monopatín con su estado, latitud y longitud iniciales.")
    @ApiResponse(responseCode = "201", description = "Monopatín creado exitosamente", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o error de validación", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<MonopatinDTO.Response> create(@RequestBody @Valid MonopatinDTO.Create request) {
        MonopatinDTO.Response newMonopatin = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMonopatin);
    }

    @Operation(summary = "Obtiene un monopatín por ID", description = "Busca y devuelve los detalles de un monopatín específico.")
    @ApiResponse(responseCode = "200", description = "Detalles del monopatín", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{id}")
    public ResponseEntity<MonopatinDTO.Response> getById(@PathVariable Long id) {
        MonopatinDTO.Response monopatin = service.findById(id);
        return ResponseEntity.ok(monopatin);
    }

    @Operation(summary = "Lista todos los monopatines", description = "Devuelve una lista de todos los monopatines registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de monopatines", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @GetMapping
    public ResponseEntity<List<MonopatinDTO.Response>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }


    @Operation(summary = "Actualiza un monopatín", description = "Actualiza los datos de un monopatín existente. Permite modificar estado, ubicación y métricas.")
    @ApiResponse(responseCode = "200", description = "Monopatín actualizado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Error de validación o transición de estado inválida", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PutMapping("/{id}")
    public ResponseEntity<MonopatinDTO.Response> update(@PathVariable Long id, @RequestBody @Valid MonopatinDTO.Update request) {
        MonopatinDTO.Response updatedMonopatin = service.update(id, request);
        return ResponseEntity.ok(updatedMonopatin);
    }


    // TODO: JWT - Requiere rol ADMIN - Quitar monopatín
    @Operation(summary = "Elimina un monopatín", description = "Elimina el monopatín con el ID especificado.")
    @ApiResponse(responseCode = "204", description = "Monopatín eliminado (sin contenido de respuesta)")
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar estado del monopatín", description = "Realiza una transición de estado (DISPONIBLE, EN_USO, EN_MANTENIMIENTO).")
    @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Transición inválida de estado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<MonopatinDTO.Response> cambiarEstado(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado del monopatín", required = true, schema = @Schema(implementation = EstadoMonopatin.class))
            @PathVariable String nuevoEstado) {

        MonopatinDTO.Response updatedMonopatin = service.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(updatedMonopatin);
    }

    @Operation(summary = "Buscar monopatines cercanos (Requerimiento g)", description = "Como usuario quiero un listado de los monopatines cercanos a mi zona, para poder encontrar un monopatín cerca de mi ubicación. Retorna solo monopatines DISPONIBLES ordenados por cercanía.")
    @ApiResponse(responseCode = "200", description = "Lista de monopatines cercanos disponibles", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @GetMapping("/cercanos")
    public ResponseEntity<List<MonopatinDTO.Response>> findMonopatinesCercanos(
            @RequestParam Integer latitud,
            @RequestParam Integer longitud,
            @RequestParam(required = false, defaultValue = "1.0") Double radioKm) {

        List<MonopatinDTO.Response> monopatines = service.findMonopatinesCercanos(latitud, longitud, radioKm);
        return ResponseEntity.ok(monopatines);
    }

    // ==================== REPORTES ====================

    // TODO: JWT - Requiere rol ADMIN - Requerimiento C
    @Operation(summary = "Monopatines con más de X viajes en un año (Requerimiento c)", description = "Como administrador quiero consultar los monopatines con más de X viajes en un cierto año")
    @ApiResponse(responseCode = "200", description = "Lista de monopatines que cumplen el criterio", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MonopatinDTO.Response.class)))
    @GetMapping("/con-mas-viajes")
    public ResponseEntity<List<MonopatinDTO.Response>> getMonopatinesConMasViajes(
            @RequestParam Integer cantidad,
            @RequestParam Integer anio) {

        List<MonopatinDTO.Response> monopatines = service.findMonopatinesConMasDeXViajes(cantidad, anio);
        return ResponseEntity.ok(monopatines);
    }

    // TODO: JWT - Requiere rol ADMIN - Requerimiento E
    @Operation(summary = "Reporte de operación vs mantenimiento (Requerimiento e)", description = "Como administrador quiero consultar la cantidad de monopatines actualmente en operación, versus la cantidad de monopatines actualmente en mantenimiento")
    @ApiResponse(responseCode = "200", description = "Reporte de operación generado", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReporteOperacionDTO.class)))
    @GetMapping("/reporte-operacion")
    public ResponseEntity<ReporteOperacionDTO> getReporteOperacion() {
        ReporteOperacionDTO reporte = service.getReporteOperacion();
        return ResponseEntity.ok(reporte);
    }

    // TODO: JWT - Requiere rol ENCARGADO_MANTENIMIENTO - Requerimiento a)
    @GetMapping("/reporte-uso-kilometros")
    @Operation(summary = "Reporte de uso por kilómetros (Requerimiento a)",
            description = "Como encargado de mantenimiento quiero poder generar un reporte de uso de monopatines por kilómetros para establecer si un monopatín requiere de mantenimiento. Este reporte debe poder configurarse para incluir (o no) los tiempos de pausa. Muestra kilómetros y tiempo de uso sin pausas. Si incluirPausas=true, también muestra el tiempo de pausas.")
    @ApiResponse(responseCode = "200", description = "Reporte de uso generado. Ordenado por kilómetros descendente. Si incluirPausas=false retorna ReporteUsoDTO.Simple (sin nulls), si incluirPausas=true retorna ReporteUsoDTO.ConPausas.",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<List<?>> getReporteUsoPorKilometros(
            @RequestParam(defaultValue = "false") @Parameter(description = "true = muestra kms + tiempo sin pausas + tiempo de pausas | false = muestra kms + tiempo sin pausas") boolean incluirPausas) {
        List<?> reporte = service.getReporteUsoPorKilometros(incluirPausas);
        return ResponseEntity.ok(reporte);
    }
}
