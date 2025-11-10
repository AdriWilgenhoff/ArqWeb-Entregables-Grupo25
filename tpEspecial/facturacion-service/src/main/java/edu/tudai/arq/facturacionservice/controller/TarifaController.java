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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    // TODO: JWT - Requiere rol ADMIN - Definir precio / Ajuste de precios - Requerimiento f)
    @Operation(summary = "Crear una nueva tarifa", description = "Permite definir tarifas de tres tipos: NORMAL (uso regular), PAUSA (durante pausas de hasta 15 min) y PAUSA_EXTENDIDA (pausas mayores a 15 min).")
    @ApiResponse(responseCode = "201", description = "Tarifa creada exitosamente", content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Ya existe una tarifa activa del mismo tipo sin fecha de fin. Debe editar la existente antes de crear una nueva.", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping
    public ResponseEntity<TarifaDTO.Response> create(@Valid @RequestBody TarifaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @Operation(summary = "Actualizar una tarifa existente", description = "Permite realizar ajuste de precios estableciendo nuevas tarifas con fecha de vigencia futura.")
    @ApiResponse(responseCode = "200", description = "Tarifa actualizada exitosamente", content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PutMapping("/{id}")
    public ResponseEntity<TarifaDTO.Response> update(@PathVariable Long id, @Valid @RequestBody TarifaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @Operation(summary = "Eliminar una tarifa", description = "Elimina una tarifa del sistema.")
    @ApiResponse(responseCode = "204", description = "Tarifa eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar tarifa por ID", description = "Obtiene los detalles de una tarifa específica.")
    @ApiResponse(responseCode = "200", description = "Tarifa encontrada", content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Listar todas las tarifas", description = "Obtiene todas las tarifas registradas en el sistema (activas e inactivas).")
    @ApiResponse(responseCode = "200", description = "Lista de todas las tarifas", content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @GetMapping
    public ResponseEntity<List<TarifaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Listar tarifas activas", description = "Obtiene solo las tarifas actualmente vigentes.")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas activas", content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @GetMapping("/activas")
    public ResponseEntity<List<TarifaDTO.Response>> findActivas() {
        return ResponseEntity.ok(service.findActivas());
    }
}
