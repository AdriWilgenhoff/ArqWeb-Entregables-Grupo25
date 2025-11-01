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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/facturaciones")
@Validated
@Tag(name = "Facturaciones", description = "API para gestión de facturaciones de viajes")
public class FacturacionController {

    private final FacturacionService service;

    public FacturacionController(FacturacionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva facturación",
            description = "Crea una facturación para un viaje finalizado")
    @ApiResponse(responseCode = "201", description = "Facturación creada exitosamente",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o viaje ya facturado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<FacturacionDTO.Response> create(@Valid @RequestBody FacturacionDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una facturación por ID")
    @ApiResponse(responseCode = "204", description = "Facturación eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Facturación no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar facturación por ID")
    @ApiResponse(responseCode = "200", description = "Facturación encontrada",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Facturación no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<FacturacionDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las facturaciones")
    @ApiResponse(responseCode = "200", description = "Lista de todas las facturaciones",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    public ResponseEntity<List<FacturacionDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/viaje/{idViaje}")
    @Operation(summary = "Buscar facturación por viaje",
            description = "Obtiene la facturación asociada a un viaje específico")
    @ApiResponse(responseCode = "200", description = "Facturación encontrada",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "No se encontró facturación para este viaje",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<FacturacionDTO.Response> findByViaje(@PathVariable Long idViaje) {
        return ResponseEntity.ok(service.findByViaje(idViaje));
    }

    @GetMapping("/cuenta/{idCuenta}")
    @Operation(summary = "Buscar facturaciones por cuenta",
            description = "Obtiene todas las facturaciones de una cuenta específica")
    @ApiResponse(responseCode = "200", description = "Lista de facturaciones de la cuenta",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    public ResponseEntity<List<FacturacionDTO.Response>> findByCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(service.findByCuenta(idCuenta));
    }

    @GetMapping("/rango-fechas")
    @Operation(summary = "Buscar facturaciones en un rango de fechas",
            description = "Obtiene todas las facturaciones entre dos fechas")
    @ApiResponse(responseCode = "200", description = "Lista de facturaciones en el rango",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    public ResponseEntity<List<FacturacionDTO.Response>> findByFechaEntre(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        return ResponseEntity.ok(service.findByFechaEntre(fechaDesde, fechaHasta));
    }

    @GetMapping("/cuenta/{idCuenta}/rango-fechas")
    @Operation(summary = "Buscar facturaciones de una cuenta en un rango de fechas")
    @ApiResponse(responseCode = "200", description = "Lista de facturaciones de la cuenta en el rango",
            content = @Content(schema = @Schema(implementation = FacturacionDTO.Response.class)))
    public ResponseEntity<List<FacturacionDTO.Response>> findByCuentaAndFechaEntre(
            @PathVariable Long idCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        return ResponseEntity.ok(service.findByCuentaAndFechaEntre(idCuenta, fechaDesde, fechaHasta));
    }

    @GetMapping("/total-facturado")
    @Operation(summary = "Calcular total facturado en un rango de fechas",
            description = "Calcula el monto total facturado en un período específico")
    @ApiResponse(responseCode = "200", description = "Total facturado calculado")
    public ResponseEntity<Double> calcularTotalFacturado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        return ResponseEntity.ok(service.calcularTotalFacturado(fechaDesde, fechaHasta));
    }

    @GetMapping("/cuenta/{idCuenta}/total-facturado")
    @Operation(summary = "Calcular total facturado de una cuenta en un rango de fechas",
            description = "Calcula el monto total facturado a una cuenta en un período específico")
    @ApiResponse(responseCode = "200", description = "Total facturado a la cuenta calculado")
    public ResponseEntity<Double> calcularTotalFacturadoPorCuenta(
            @PathVariable Long idCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        return ResponseEntity.ok(service.calcularTotalFacturadoPorCuenta(idCuenta, fechaDesde, fechaHasta));
    }
}

