package edu.tudai.arq.facturacionservice.controller;

import edu.tudai.arq.facturacionservice.dto.TarifaDTO;
import edu.tudai.arq.facturacionservice.exception.ApiError;
import edu.tudai.arq.facturacionservice.service.interfaces.TarifaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tarifas")
@Validated
@Tag(name = "Tarifas", description = "API para gestión de tarifas del sistema de monopatines")
public class TarifaController {

    private final TarifaService service;

    public TarifaController(TarifaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tarifa")
    @ApiResponse(responseCode = "201", description = "Tarifa creada exitosamente",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TarifaDTO.Response> create(@Valid @RequestBody TarifaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una tarifa existente por ID")
    @ApiResponse(responseCode = "200", description = "Tarifa actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TarifaDTO.Response> update(@PathVariable Long id,
                                                      @Valid @RequestBody TarifaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una tarifa por ID")
    @ApiResponse(responseCode = "204", description = "Tarifa eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarifa por ID")
    @ApiResponse(responseCode = "200", description = "Tarifa encontrada",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TarifaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las tarifas")
    @ApiResponse(responseCode = "200", description = "Lista de todas las tarifas",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    public ResponseEntity<List<TarifaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/tipo/{tipoTarifa}")
    @Operation(summary = "Buscar tarifas por tipo",
            description = "Obtiene todas las tarifas de un tipo específico (NORMAL o PAUSA_EXTENDIDA)")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas del tipo especificado",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    public ResponseEntity<List<TarifaDTO.Response>> findByTipo(@PathVariable String tipoTarifa) {
        return ResponseEntity.ok(service.findByTipo(tipoTarifa));
    }

    @GetMapping("/activas")
    @Operation(summary = "Obtener todas las tarifas activas")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas activas",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    public ResponseEntity<List<TarifaDTO.Response>> findActivas() {
        return ResponseEntity.ok(service.findActivas());
    }

    @GetMapping("/vigente")
    @Operation(summary = "Obtener tarifa vigente por tipo y fecha",
            description = "Obtiene la tarifa vigente de un tipo específico para una fecha dada")
    @ApiResponse(responseCode = "200", description = "Tarifa vigente encontrada",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "No hay tarifa vigente para los criterios especificados",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TarifaDTO.Response> findTarifaVigente(
            @RequestParam String tipo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.findTarifaVigente(tipo, fecha));
    }

    @GetMapping("/vigentes")
    @Operation(summary = "Obtener todas las tarifas vigentes en una fecha",
            description = "Obtiene todas las tarifas (normal y extendida) vigentes en una fecha específica")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas vigentes",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    public ResponseEntity<List<TarifaDTO.Response>> findTarifasVigentesEn(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.findTarifasVigentesEn(fecha));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar una tarifa",
            description = "Desactiva una tarifa estableciendo su fecha de fin de vigencia")
    @ApiResponse(responseCode = "204", description = "Tarifa desactivada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivarTarifa(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        service.desactivarTarifa(id, fechaFin);
    }
}

