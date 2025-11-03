package edu.tudai.arq.monopatinservice.controller;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.monopatinservice.service.interfaces.MonopatinService;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Registra un nuevo monopatín",
            description = "Crea un monopatín con su estado, latitud y longitud iniciales.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Monopatín creado exitosamente",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud incorrecta o error de validación")
            })
    @PostMapping
    public ResponseEntity<MonopatinDTO.Response> create(@RequestBody @Valid MonopatinDTO.Create request) {
        MonopatinDTO.Response newMonopatin = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMonopatin);
    }

    @Operation(summary = "Obtiene un monopatín por ID",
            description = "Busca y devuelve los detalles de un monopatín específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Detalles del monopatín",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class))),
                    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado")
            })
    @GetMapping("/{id}")
    public ResponseEntity<MonopatinDTO.Response> getById(@PathVariable Long id) {
        MonopatinDTO.Response monopatin = service.findById(id);
        return ResponseEntity.ok(monopatin);
    }

    @Operation(summary = "Lista todos los monopatines",
            description = "Devuelve una lista de todos los monopatines registrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de monopatines",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class)))
            })
    @GetMapping
    public ResponseEntity<List<MonopatinDTO.Response>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }


    @Operation(summary = "Actualiza un monopatín",
            description = "Actualiza los datos de un monopatín existente. Permite modificar estado, ubicación y métricas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Monopatín actualizado",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class))),
                    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Error de validación o transición de estado inválida")
            })
    @PutMapping("/{id}")
    public ResponseEntity<MonopatinDTO.Response> update(@PathVariable Long id, @RequestBody @Valid MonopatinDTO.Update request) {
        MonopatinDTO.Response updatedMonopatin = service.update(id, request);
        return ResponseEntity.ok(updatedMonopatin);
    }


    @Operation(summary = "Elimina un monopatín",
            description = "Elimina el monopatín con el ID especificado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Monopatín eliminado (sin contenido de respuesta)"),
                    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cambiar estado del monopatín",
            description = "Realiza una transición de estado (DISPONIBLE, EN_USO, EN_MANTENIMIENTO).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado actualizado",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class))),
                    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Transición inválida de estado")
            })
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<MonopatinDTO.Response> cambiarEstado(
            @PathVariable Long id,
            @PathVariable String nuevoEstado) {

        MonopatinDTO.Response updatedMonopatin = service.cambiarEstado(id, nuevoEstado);
        return ResponseEntity.ok(updatedMonopatin);
    }

    @Operation(summary = "Buscar monopatines cercanos (Requerimiento g)",
            description = "Como usuario quiero un listado de los monopatines cercanos a mi zona, para poder encontrar un monopatín cerca de mi ubicación. " +
                    "Calcula la distancia usando la fórmula de Haversine y retorna solo monopatines DISPONIBLES.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de monopatines cercanos disponibles",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class)))
            })
    @GetMapping("/cercanos")
    public ResponseEntity<List<MonopatinDTO.Response>> findMonopatinesCercanos(
            @RequestParam Double latitud,
            @RequestParam Double longitud,
            @RequestParam(required = false, defaultValue = "1.0") Double radioKm) {

        List<MonopatinDTO.Response> monopatines = service.findMonopatinesCercanos(latitud, longitud, radioKm);
        return ResponseEntity.ok(monopatines);
    }

    // ==================== REPORTES ====================

    @Operation(summary = "Monopatines con más de X viajes en un año (Requerimiento c)",
            description = "Como administrador quiero consultar los monopatines con más de X viajes en un cierto año",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de monopatines que cumplen el criterio",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MonopatinDTO.Response.class)))
            })
    @GetMapping("/con-mas-viajes")
    public ResponseEntity<List<MonopatinDTO.Response>> getMonopatinesConMasViajes(
            @RequestParam Integer cantidad,
            @RequestParam Integer anio) {

        List<MonopatinDTO.Response> monopatines = service.findMonopatinesConMasDeXViajes(cantidad, anio);
        return ResponseEntity.ok(monopatines);
    }

    @Operation(summary = "Reporte de operación vs mantenimiento (Requerimiento e)",
            description = "Como administrador quiero consultar la cantidad de monopatines actualmente en operación, " +
                    "versus la cantidad de monopatines actualmente en mantenimiento",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Reporte de operación generado",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ReporteOperacionDTO.class)))
            })
    @GetMapping("/reporte-operacion")
    public ResponseEntity<ReporteOperacionDTO> getReporteOperacion() {
        ReporteOperacionDTO reporte = service.getReporteOperacion();
        return ResponseEntity.ok(reporte);
    }
}
