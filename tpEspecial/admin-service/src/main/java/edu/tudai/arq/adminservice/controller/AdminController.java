package edu.tudai.arq.adminservice.controller;

import edu.tudai.arq.adminservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.adminservice.dto.TarifaDTO;
import edu.tudai.arq.adminservice.dto.TotalFacturadoDTO;
import edu.tudai.arq.adminservice.model.*;
import edu.tudai.arq.adminservice.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Administración", description = "API de administración para gestión de monopatines, paradas, tarifas y reportes")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ==================== GESTIÓN DE CUENTAS ====================

    @PutMapping("/cuentas/{id}/anular")
    @Operation(summary = "Anular cuenta (Requerimiento b)",
            description = "Como administrador quiero poder anular cuentas para inhabilitar el uso momentáneo de la misma")
    @ApiResponse(responseCode = "204", description = "Cuenta anulada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anularCuenta(@PathVariable Long id) {
        adminService.anularCuenta(id);
    }

    @PutMapping("/cuentas/{id}/habilitar")
    @Operation(summary = "Habilitar cuenta",
            description = "Reactiva una cuenta previamente anulada")
    @ApiResponse(responseCode = "204", description = "Cuenta habilitada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void habilitarCuenta(@PathVariable Long id) {
        adminService.habilitarCuenta(id);
    }

    @GetMapping("/cuentas")
    @Operation(summary = "Listar todas las cuentas")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas",
            content = @Content(schema = @Schema(implementation = CuentaModel.class)))
    public ResponseEntity<List<CuentaModel>> getAllCuentas() {
        return ResponseEntity.ok(adminService.getAllCuentas());
    }

    @GetMapping("/cuentas/{id}")
    @Operation(summary = "Obtener cuenta por ID")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    public ResponseEntity<CuentaModel> getCuentaById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCuentaById(id));
    }

    // ==================== GESTIÓN DE MONOPATINES ====================

    @PostMapping("/monopatines")
    @Operation(summary = "Agregar monopatín",
            description = "Registra un nuevo monopatín en el sistema")
    @ApiResponse(responseCode = "201", description = "Monopatín creado exitosamente")
    public ResponseEntity<MonopatinModel> createMonopatin(@RequestBody MonopatinModel monopatin) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createMonopatin(monopatin));
    }

    @DeleteMapping("/monopatines/{id}")
    @Operation(summary = "Quitar monopatín",
            description = "Elimina un monopatín del sistema")
    @ApiResponse(responseCode = "204", description = "Monopatín eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMonopatin(@PathVariable Long id) {
        adminService.deleteMonopatin(id);
    }

    @GetMapping("/monopatines")
    @Operation(summary = "Listar todos los monopatines")
    @ApiResponse(responseCode = "200", description = "Lista de monopatines")
    public ResponseEntity<List<MonopatinModel>> getAllMonopatines() {
        return ResponseEntity.ok(adminService.getAllMonopatines());
    }

    @GetMapping("/monopatines/{id}")
    @Operation(summary = "Obtener monopatín por ID")
    @ApiResponse(responseCode = "200", description = "Monopatín encontrado")
    @ApiResponse(responseCode = "404", description = "Monopatín no encontrado")
    public ResponseEntity<MonopatinModel> getMonopatinById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getMonopatinById(id));
    }

    @GetMapping("/monopatines/con-viajes")
    @Operation(summary = "Monopatines con más de X viajes (Requerimiento c)",
            description = "Como administrador quiero consultar los monopatines con más de X viajes en un cierto año")
    @ApiResponse(responseCode = "200", description = "Lista de monopatines que cumplen el criterio")
    public ResponseEntity<List<MonopatinModel>> getMonopatinesConMasDeXViajes(
            @RequestParam Integer cantidadViajes,
            @RequestParam Integer anio) {
        return ResponseEntity.ok(adminService.getMonopatinesConMasDeXViajes(cantidadViajes, anio));
    }

    // ==================== GESTIÓN DE PARADAS ====================

    @PostMapping("/paradas")
    @Operation(summary = "Registrar parada",
            description = "Crea una nueva parada en el sistema")
    @ApiResponse(responseCode = "201", description = "Parada creada exitosamente")
    public ResponseEntity<ParadaModel> createParada(@RequestBody ParadaModel parada) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createParada(parada));
    }

    @DeleteMapping("/paradas/{id}")
    @Operation(summary = "Quitar parada",
            description = "Elimina una parada del sistema")
    @ApiResponse(responseCode = "204", description = "Parada eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Parada no encontrada")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteParada(@PathVariable Long id) {
        adminService.deleteParada(id);
    }

    @PutMapping("/paradas/{id}")
    @Operation(summary = "Actualizar parada",
            description = "Actualiza los datos de una parada existente")
    @ApiResponse(responseCode = "200", description = "Parada actualizada exitosamente")
    @ApiResponse(responseCode = "404", description = "Parada no encontrada")
    public ResponseEntity<ParadaModel> updateParada(@PathVariable Long id, @RequestBody ParadaModel parada) {
        return ResponseEntity.ok(adminService.updateParada(id, parada));
    }

    @GetMapping("/paradas")
    @Operation(summary = "Listar todas las paradas")
    @ApiResponse(responseCode = "200", description = "Lista de paradas")
    public ResponseEntity<List<ParadaModel>> getAllParadas() {
        return ResponseEntity.ok(adminService.getAllParadas());
    }

    @GetMapping("/paradas/{id}")
    @Operation(summary = "Obtener parada por ID")
    @ApiResponse(responseCode = "200", description = "Parada encontrada")
    @ApiResponse(responseCode = "404", description = "Parada no encontrada")
    public ResponseEntity<ParadaModel> getParadaById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getParadaById(id));
    }

    // ==================== GESTIÓN DE TARIFAS ====================

    @PostMapping("/tarifas")
    @Operation(summary = "Definir precio / Ajuste de precios (Requerimiento f)",
            description = "Como administrador quiero hacer un ajuste de precios, y que a partir de cierta fecha el sistema habilite los nuevos precios")
    @ApiResponse(responseCode = "201", description = "Tarifa creada exitosamente")
    public ResponseEntity<TarifaModel> ajustarPrecio(@Valid @RequestBody TarifaDTO.CreateUpdate tarifaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.ajustarPrecio(tarifaDTO));
    }

    @GetMapping("/tarifas")
    @Operation(summary = "Listar todas las tarifas")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas")
    public ResponseEntity<List<TarifaModel>> getAllTarifas() {
        return ResponseEntity.ok(adminService.getAllTarifas());
    }

    @GetMapping("/tarifas/activas")
    @Operation(summary = "Listar tarifas activas")
    @ApiResponse(responseCode = "200", description = "Lista de tarifas actualmente activas")
    public ResponseEntity<List<TarifaModel>> getTarifasActivas() {
        return ResponseEntity.ok(adminService.getTarifasActivas());
    }

    @PutMapping("/tarifas/{id}/desactivar")
    @Operation(summary = "Desactivar tarifa",
            description = "Desactiva una tarifa estableciendo su fecha de fin de vigencia")
    @ApiResponse(responseCode = "204", description = "Tarifa desactivada exitosamente")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivarTarifa(@PathVariable Long id, @RequestParam String fechaFin) {
        adminService.desactivarTarifa(id, fechaFin);
    }

    // ==================== GESTIÓN DE MANTENIMIENTOS ====================

    @PostMapping("/mantenimientos")
    @Operation(summary = "Registrar monopatín en mantenimiento",
            description = "Registra un nuevo mantenimiento, marcando el monopatín como no disponible")
    @ApiResponse(responseCode = "201", description = "Mantenimiento registrado exitosamente")
    public ResponseEntity<MantenimientoModel> registrarMantenimiento(@RequestBody MantenimientoModel mantenimiento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.registrarMantenimiento(mantenimiento));
    }

    @PutMapping("/mantenimientos/{id}/finalizar")
    @Operation(summary = "Registrar fin de mantenimiento",
            description = "Finaliza un mantenimiento, habilitando el monopatín para su uso")
    @ApiResponse(responseCode = "200", description = "Mantenimiento finalizado exitosamente")
    public ResponseEntity<MantenimientoModel> finalizarMantenimiento(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.finalizarMantenimiento(id));
    }

    @GetMapping("/mantenimientos")
    @Operation(summary = "Listar todos los mantenimientos")
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos")
    public ResponseEntity<List<MantenimientoModel>> getAllMantenimientos() {
        return ResponseEntity.ok(adminService.getAllMantenimientos());
    }

    @GetMapping("/mantenimientos/activos")
    @Operation(summary = "Listar mantenimientos activos")
    @ApiResponse(responseCode = "200", description = "Lista de mantenimientos en curso")
    public ResponseEntity<List<MantenimientoModel>> getMantenimientosActivos() {
        return ResponseEntity.ok(adminService.getMantenimientosActivos());
    }

    // ==================== REPORTES ====================

    @GetMapping("/reportes/total-facturado")
    @Operation(summary = "Total facturado en rango de meses (Requerimiento d)",
            description = "Como administrador quiero consultar el total facturado en un rango de meses de cierto año")
    @ApiResponse(responseCode = "200", description = "Total facturado calculado")
    public ResponseEntity<TotalFacturadoDTO> getTotalFacturado(
            @RequestParam Integer anio,
            @RequestParam Integer mesDesde,
            @RequestParam Integer mesHasta) {
        return ResponseEntity.ok(adminService.getTotalFacturadoEnRango(anio, mesDesde, mesHasta));
    }

    @GetMapping("/reportes/operacion")
    @Operation(summary = "Monopatines en operación vs mantenimiento (Requerimiento e)",
            description = "Como administrador quiero consultar la cantidad de monopatines actualmente en operación, versus la cantidad de monopatines actualmente en mantenimiento")
    @ApiResponse(responseCode = "200", description = "Reporte de operación generado")
    public ResponseEntity<ReporteOperacionDTO> getReporteOperacion() {
        return ResponseEntity.ok(adminService.getReporteOperacion());
    }
}

