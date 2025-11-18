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
@Tag(name = "Cuentas", description = "API para gestión de cuentas del sistema de monopatines")
public class CuentaController {

    private final CuentaService service;

    public CuentaController(CuentaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta")
    @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: número identificatorio duplicado)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> create(@Valid @RequestBody CuentaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una cuenta existente por ID")
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> update(@PathVariable Long id,
                                                     @Valid @RequestBody CuentaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cuenta por ID")
    @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "La cuenta tiene usuarios asociados o viajes registrados",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cuenta por ID")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada", content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las cuentas")
    @ApiResponse(responseCode = "200", description = "Lista de todas las cuentas",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    public ResponseEntity<List<CuentaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/numero/{numeroIdentificatorio}")
    @Operation(summary = "Buscar cuenta por número identificatorio")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> findByNumeroIdentificatorio(@PathVariable String numeroIdentificatorio) {
        return ResponseEntity.ok(service.findByNumeroIdentificatorio(numeroIdentificatorio));
    }

    @GetMapping("/{idCuenta}/usuarios")
    @Operation(summary = "Obtener todos los usuarios de una cuenta",
            description = "Retorna la lista de usuarios asociados a una cuenta específica")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios de la cuenta",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<List<UsuarioDTO.Response>> getUsuariosByCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(service.getUsuariosByCuenta(idCuenta));
    }

    @PostMapping("/{id}/cargar-saldo")
    @Operation(summary = "Cargar saldo en una cuenta",
            description = "Agrega el monto especificado al saldo actual de la cuenta")
    @ApiResponse(responseCode = "200", description = "Saldo cargado exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Monto inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> cargarSaldo(@PathVariable Long id,
                                                          @Valid @RequestBody CuentaDTO.CargarSaldo in) {
        return ResponseEntity.ok(service.cargarSaldo(id, in));
    }

    // TODO: JWT - Requiere autenticación del sistema (Feign interno)
    @PostMapping("/{id}/descontar-saldo")
    @Operation(summary = "Descontar saldo de una cuenta",
            description = "Descuenta el monto especificado del saldo actual. Usado internamente por viaje-service al finalizar viajes.")
    @ApiResponse(responseCode = "200", description = "Saldo descontado exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "Saldo insuficiente o monto inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> descontarSaldo(@PathVariable Long id,
                                                             @Valid @RequestBody CuentaDTO.DescontarSaldo in) {
        return ResponseEntity.ok(service.descontarSaldo(id, in));
    }

    // TODO: JWT - Requiere rol ADMIN - Requerimiento b)
    @PutMapping("/{id}/anular")
    @Operation(summary = "Deshabilitar una cuenta",
            description = "Deshabilita la cuenta para su uso momentáneo (requerimiento del administrador)")
    @ApiResponse(responseCode = "204", description = "Cuenta anulada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anularCuenta(@PathVariable Long id) {
        service.anularCuenta(id);
    }

    // TODO: JWT - Requiere rol ADMIN
    @PutMapping("/{id}/habilitar")
    @Operation(summary = "Habilitar una cuenta previamente anulada",
            description = "Rehabilita una cuenta que fue anulada anteriormente")
    @ApiResponse(responseCode = "204", description = "Cuenta habilitada exitosamente")
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void habilitarCuenta(@PathVariable Long id) {
        service.habilitarCuenta(id);
    }

    // ==================== ENDPOINTS PREMIUM ====================

    @PostMapping("/{id}/upgrade-premium")
    @Operation(
            summary = "Convertir cuenta a PREMIUM",
            description = "Actualiza una cuenta BASICA a PREMIUM. Descuenta $500 del saldo. " +
                    "Las cuentas premium obtienen 100km gratis por mes, y luego 50% de descuento en viajes."
    )
    @ApiResponse(responseCode = "200", description = "Cuenta actualizada a premium exitosamente",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Saldo insuficiente o cuenta ya es premium",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CuentaDTO.Response> upgradeToPremium(@PathVariable Long id) {
        return ResponseEntity.ok(service.upgradeToPremium(id));
    }

    @PostMapping("/{id}/renovar-cupo")
    @Operation(
            summary = "Renovar cupo mensual de cuenta PREMIUM",
            description = "Resetea el contador de kilómetros del mes y cobra el pago mensual de $500. " +
                    "Esto se hace automáticamente cada mes, pero puede hacerse manualmente."
    )
    @ApiResponse(responseCode = "204", description = "Cupo renovado exitosamente")
    @ApiResponse(responseCode = "400", description = "Saldo insuficiente o cuenta no es premium",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void renovarCupo(@PathVariable Long id) {
        service.renovarCupo(id);
    }

    @PostMapping("/{id}/usar-kilometros-gratis")
    @Operation(
            summary = "Usar kilómetros gratis de cuenta PREMIUM",
            description = "Descuenta kilómetros del cupo mensual gratis. Devuelve cuántos km se usaron efectivamente. " +
                    "Si la cuenta no es premium o no tiene km disponibles, devuelve 0."
    )
    @ApiResponse(responseCode = "200", description = "Kilómetros usados exitosamente",
            content = @Content(schema = @Schema(implementation = Double.class)))
    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Double> usarKilometrosGratis(
            @PathVariable Long id,
            @RequestParam Double kilometros) {
        return ResponseEntity.ok(service.usarKilometrosGratis(id, kilometros));
    }
}