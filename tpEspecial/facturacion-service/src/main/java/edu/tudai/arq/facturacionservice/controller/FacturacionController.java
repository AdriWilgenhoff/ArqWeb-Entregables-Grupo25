package edu.tudai.arq.facturacionservice.controller;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;
import edu.tudai.arq.facturacionservice.exception.ApiError;
import edu.tudai.arq.facturacionservice.service.interfaces.FacturacionService;
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
@RequestMapping("/api/v1/facturaciones")
@Validated
@Tag(name = "Facturaciones", description = "API para gestión de facturaciones de viajes.")
public class FacturacionController {

    private final FacturacionService service;

    public FacturacionController(FacturacionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva facturación",
            description = "Accesible para usuarios autenticados. Crea una facturación para un viaje finalizado. " +
                    "Recibe: idViaje, idCuenta, tiempos (total, pausado, pausa extendida). " +
                    "El sistema obtiene automáticamente las tarifas vigentes HOY y calcula el monto total. " +
                    "Acceso: http://localhost:8080/api/v1/facturaciones"
    )
    @ApiResponse(responseCode = "201", description = "Facturación creada exitosamente. El monto se calculó automáticamente con las tarifas vigentes.",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Viaje ya facturado o datos inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Tarifa vigente no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<FacturacionDTO.Response> create(@Valid @RequestBody FacturacionDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una facturación por ID",
            description = "Requiere rol ADMINISTRADOR. Elimina una facturación del sistema. Acceso: http://localhost:8080/api/v1/facturaciones/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Facturación eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Facturación no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar facturación por ID",
            description = "Accesible para usuarios autenticados. Obtiene los detalles de una facturación específica. Acceso: http://localhost:8080/api/v1/facturaciones/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Facturación encontrada",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Facturación no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<FacturacionDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Obtener todas las facturaciones",
            description = "Requiere rol ADMINISTRADOR. Obtiene todas las facturaciones del sistema. Acceso: http://localhost:8080/api/v1/facturaciones"
    )
    @ApiResponse(responseCode = "200", description = "Lista de todas las facturaciones",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<List<FacturacionDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/viaje/{idViaje}")
    @Operation(
            summary = "Buscar facturación por viaje",
            description = "Accesible para usuarios autenticados. Obtiene la facturación asociada a un viaje específico. Acceso: http://localhost:8080/api/v1/facturaciones/viaje/{idViaje}"
    )
    @ApiResponse(responseCode = "200", description = "Facturación encontrada",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "No se encontró facturación para este viaje",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<FacturacionDTO.Response> findByViaje(@PathVariable Long idViaje) {
        return ResponseEntity.ok(service.findByViaje(idViaje));
    }

    @GetMapping("/cuenta/{idCuenta}")
    @Operation(
            summary = "Buscar facturaciones por cuenta",
            description = "Accesible para usuarios autenticados. Obtiene todas las facturaciones de una cuenta específica. Acceso: http://localhost:8080/api/v1/facturaciones/cuenta/{idCuenta}"
    )
    @ApiResponse(responseCode = "200", description = "Lista de facturaciones de la cuenta",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<List<FacturacionDTO.Response>> findByCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(service.findByCuenta(idCuenta));
    }

    // ==================== REPORTES ====================

    @GetMapping("/total-por-periodo")
    @Operation(
            summary = "Total facturado en rango de meses de un año (Requerimiento d)",
            description = "Requiere rol ADMINISTRADOR. Consulta el total facturado en un rango de meses de cierto año. Acceso: http://localhost:8080/api/v1/facturaciones/total-por-periodo"
    )
    @ApiResponse(responseCode = "200", description = "Total facturado en el período")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<Double> getTotalFacturadoPorPeriodo(
            @RequestParam Integer anio,
            @RequestParam Integer mesDesde,
            @RequestParam Integer mesHasta) {
        return ResponseEntity.ok(service.getTotalFacturadoPorPeriodo(anio, mesDesde, mesHasta));
    }
}
