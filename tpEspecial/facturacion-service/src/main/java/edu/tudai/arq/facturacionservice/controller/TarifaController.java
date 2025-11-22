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
@Tag(name = "Tarifas", description = "API para gestión de tarifas del sistema de monopatines.")
public class TarifaController {

    private final TarifaService service;

    public TarifaController(TarifaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva tarifa (Requerimiento f)",
            description = "Requiere rol ADMINISTRADOR. Permite definir tarifas de tres tipos:\n" +
                    "- NORMAL: precio por KILÓMETRO recorrido (ej: $50/km)\n" +
                    "- PAUSA: precio por MINUTO de pausa normal (≤15 min, ej: $5/min)\n" +
                    "- PAUSA_EXTENDIDA: precio por MINUTO de pausa extendida (>15 min, solo excedente, ej: $10/min)\n" +
                    "El campo 'precioPorMinuto' se interpreta según el tipo de tarifa. Acceso: http://localhost:8080/api/v1/tarifas"
    )
    @ApiResponse(responseCode = "201", description = "Tarifa creada exitosamente",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ApiResponse(responseCode = "409", description = "Ya existe una tarifa activa del mismo tipo sin fecha de fin",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TarifaDTO.Response> create(@Valid @RequestBody TarifaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una tarifa existente (Requerimiento f - Ajuste de precios)",
            description = "Requiere rol ADMINISTRADOR. Permite realizar ajuste de precios estableciendo nuevas tarifas con fecha de vigencia futura. Acceso: http://localhost:8080/api/v1/tarifas/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Tarifa actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<TarifaDTO.Response> update(@PathVariable Long id, @Valid @RequestBody TarifaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una tarifa",
            description = "Requiere rol ADMINISTRADOR. Elimina una tarifa del sistema. Acceso: http://localhost:8080/api/v1/tarifas/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Tarifa eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar tarifa por ID",
            description = "Requiere rol ADMINISTRADOR. Obtiene los detalles de una tarifa específica. Acceso: http://localhost:8080/api/v1/tarifas/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Tarifa encontrada",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<TarifaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las tarifas",
            description = "Requiere rol ADMINISTRADOR. Obtiene todas las tarifas registradas en el sistema (activas e inactivas). Acceso: http://localhost:8080/api/v1/tarifas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de todas las tarifas",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<List<TarifaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/activas")
    @Operation(
            summary = "Listar tarifas activas",
            description = "Accesible para usuarios autenticados. Obtiene solo las tarifas actualmente vigentes. Útil para calcular costos de viajes. Acceso: http://localhost:8080/api/v1/tarifas/activas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de tarifas activas",
            content = @Content(schema = @Schema(implementation = TarifaDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<List<TarifaDTO.Response>> findActivas() {
        return ResponseEntity.ok(service.findActivas());
    }
}
