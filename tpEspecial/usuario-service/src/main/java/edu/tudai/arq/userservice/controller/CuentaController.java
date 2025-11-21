package edu.tudai.arq.userservice.controller;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import edu.tudai.arq.userservice.dto.UsuarioDTO;
import edu.tudai.arq.userservice.exception.ApiError;
import edu.tudai.arq.userservice.service.interfaces.CuentaService;
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
@RequestMapping("/api/v1/cuentas")
@Validated
@Tag(name = "Cuentas", description = "API para gestión de cuentas del sistema de monopatines.")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva cuenta",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/cuentas"
    )
    @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: número identificatorio duplicado)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<CuentaDTO.Response> create(@Valid @RequestBody CuentaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una cuenta existente por ID",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/cuentas/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR o ser usuario de la cuenta")
    public ResponseEntity<CuentaDTO.Response> update(@PathVariable Long id,
                                                     @Valid @RequestBody CuentaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una cuenta por ID",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/cuentas/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "La cuenta tiene usuarios asociados o viajes registrados",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar cuenta por ID",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/cuentas/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada", content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<CuentaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Obtener todas las cuentas",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/cuentas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de todas las cuentas",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<List<CuentaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/numero/{numeroIdentificatorio}")
    @Operation(
            summary = "Buscar cuenta por número identificatorio",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/cuentas/numero/{numeroIdentificatorio}"
    )
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<CuentaDTO.Response> findByNumeroIdentificatorio(@PathVariable String numeroIdentificatorio) {
        return ResponseEntity.ok(service.findByNumeroIdentificatorio(numeroIdentificatorio));
    }

    @GetMapping("/{idCuenta}/usuarios")
    @Operation(
            summary = "Obtener todos los usuarios de una cuenta",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/cuentas/{idCuenta}/usuarios"
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios de la cuenta",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<List<UsuarioDTO.Response>> getUsuariosByCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(service.getUsuariosByCuenta(idCuenta));
    }

    @PostMapping("/{id}/cargar-saldo")
    @Operation(
            summary = "Cargar saldo en una cuenta",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/cuentas/{id}/cargar-saldo"
    )
    @ApiResponse(responseCode = "200", description = "Saldo cargado exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Monto inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<CuentaDTO.Response> cargarSaldo(@PathVariable Long id,
                                                          @Valid @RequestBody CuentaDTO.CargarSaldo in) {
        return ResponseEntity.ok(service.cargarSaldo(id, in));
    }

    @PostMapping("/{id}/descontar-saldo")
    @Operation(
            summary = "Descontar saldo de una cuenta",
            description = "Descuenta saldo al finalizar viajes. Acceso: http://localhost:8080/api/v1/cuentas/{id}/descontar-saldo"
    )
    @ApiResponse(responseCode = "200", description = "Saldo descontado exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Saldo insuficiente o monto inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<CuentaDTO.Response> descontarSaldo(@PathVariable Long id,
                                                             @Valid @RequestBody CuentaDTO.DescontarSaldo in) {
        return ResponseEntity.ok(service.descontarSaldo(id, in));
    }

    @PutMapping("/{id}/anular")
    @Operation(
            summary = "Deshabilitar una cuenta (Requerimiento b)",
            description = "Requiere rol ADMINISTRADOR. Deshabilita la cuenta para uso momentáneo. Acceso: http://localhost:8080/api/v1/cuentas/{id}/anular"
    )
    @ApiResponse(responseCode = "204", description = "Cuenta anulada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anularCuenta(@PathVariable Long id) {
        service.anularCuenta(id);
    }

    @PutMapping("/{id}/habilitar")
    @Operation(
            summary = "Habilitar una cuenta previamente anulada",
            description = "Requiere rol ADMINISTRADOR. Rehabilita una cuenta que fue anulada. Acceso: http://localhost:8080/api/v1/cuentas/{id}/habilitar"
    )
    @ApiResponse(responseCode = "204", description = "Cuenta habilitada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void habilitarCuenta(@PathVariable Long id) {
        service.habilitarCuenta(id);
    }

    // ==================== ENDPOINTS PREMIUM ====================

    @PostMapping("/{id}/upgrade-premium")
    @Operation(
            summary = "Convertir cuenta a PREMIUM",
            description = "Accesible para usuarios autenticados. Actualiza una cuenta BASICA a PREMIUM. Descuenta $500 del saldo. Las cuentas premium obtienen 100km gratis. Acceso: http://localhost:8080/api/v1/cuentas/{id}/upgrade-premium"
    )
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada a premium exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Saldo insuficiente o cuenta ya es premium",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<CuentaDTO.Response> upgradeToPremium(@PathVariable Long id) {
        return ResponseEntity.ok(service.upgradeToPremium(id));
    }

    @PostMapping("/{id}/renovar-cupo")
    @Operation(
            summary = "Renovar cupo mensual de cuenta PREMIUM",
            description = "Accesible para usuarios autenticados. Resetea el contador de kilómetros y cobra $500. Acceso: http://localhost:8080/api/v1/cuentas/{id}/renovar-cupo"
    )
    @ApiResponse(responseCode = "204", description = "Cupo renovado exitosamente")
    @ApiResponse(responseCode = "400", description = "Saldo insuficiente o cuenta no es premium",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void renovarCupo(@PathVariable Long id) {
        service.renovarCupo(id);
    }

    @PostMapping("/{id}/usar-kilometros-gratis")
    @Operation(
            summary = "Usar kilómetros gratis de cuenta PREMIUM",
            description = "Descuenta kilómetros del cupo mensual gratis al finalizar viajes. Acceso: http://localhost:8080/api/v1/cuentas/{id}/usar-kilometros-gratis"
    )
    @ApiResponse(responseCode = "200", description = "Kilómetros usados exitosamente",
            content = @Content(schema = @Schema(implementation = Double.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<Double> usarKilometrosGratis(
            @PathVariable Long id,
            @RequestParam Double kilometros) {
        return ResponseEntity.ok(service.usarKilometrosGratis(id, kilometros));
    }
}