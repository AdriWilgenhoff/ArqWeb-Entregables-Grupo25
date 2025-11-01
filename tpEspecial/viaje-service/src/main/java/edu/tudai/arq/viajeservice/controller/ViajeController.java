package edu.tudai.arq.viajeservice.controller;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.dto.ViajeDTO;
import edu.tudai.arq.viajeservice.exception.ApiError;
import edu.tudai.arq.viajeservice.service.interfaces.ViajeService;
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
@RequestMapping("/api/v1/viajes")
@Validated
@Tag(name = "Viajes", description = "API para gestión de viajes de monopatines")
public class ViajeController {

    private final ViajeService service;

    public ViajeController(ViajeService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Iniciar un nuevo viaje",
            description = "Crea un nuevo viaje y lo asocia a un usuario, cuenta y monopatín")
    @ApiResponse(responseCode = "201", description = "Viaje iniciado exitosamente",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o monopatín ya en uso",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ViajeDTO.Response> iniciarViaje(@Valid @RequestBody ViajeDTO.Create in) {
        var out = service.iniciarViaje(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar un viaje existente",
            description = "Finaliza un viaje en curso, registrando la parada final, kilómetros y costo")
    @ApiResponse(responseCode = "200", description = "Viaje finalizado exitosamente",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "El viaje ya está finalizado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ViajeDTO.Response> finalizarViaje(
            @PathVariable Long id,
            @Valid @RequestBody ViajeDTO.Finalizacion in) {
        return ResponseEntity.ok(service.finalizarViaje(id, in));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar viaje por ID")
    @ApiResponse(responseCode = "200", description = "Viaje encontrado",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ViajeDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los viajes")
    @ApiResponse(responseCode = "200", description = "Lista de todos los viajes",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    public ResponseEntity<List<ViajeDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un viaje por ID")
    @ApiResponse(responseCode = "204", description = "Viaje eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/pausar")
    @Operation(summary = "Pausar un viaje en curso",
            description = "Pausa un viaje que está en curso. El monopatín se puede apagar pero sigue asignado")
    @ApiResponse(responseCode = "200", description = "Viaje pausado exitosamente",
            content = @Content(schema = @Schema(implementation = PausaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "El viaje no puede ser pausado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<PausaDTO.Response> pausarViaje(@PathVariable Long id) {
        return ResponseEntity.ok(service.pausarViaje(id));
    }

    @PostMapping("/{id}/reanudar")
    @Operation(summary = "Reanudar un viaje pausado",
            description = "Reanuda un viaje que estaba pausado")
    @ApiResponse(responseCode = "200", description = "Viaje reanudado exitosamente",
            content = @Content(schema = @Schema(implementation = PausaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "El viaje no está pausado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<PausaDTO.Response> reanudarViaje(@PathVariable Long id) {
        return ResponseEntity.ok(service.reanudarViaje(id));
    }

    @GetMapping("/{id}/pausas")
    @Operation(summary = "Obtener todas las pausas de un viaje")
    @ApiResponse(responseCode = "200", description = "Lista de pausas del viaje",
            content = @Content(schema = @Schema(implementation = PausaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<List<PausaDTO.Response>> getPausasByViaje(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPausasByViaje(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Buscar viajes por usuario",
            description = "Obtiene todos los viajes realizados por un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de viajes del usuario",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    public ResponseEntity<List<ViajeDTO.Response>> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.findByUsuario(idUsuario));
    }

    @GetMapping("/cuenta/{idCuenta}")
    @Operation(summary = "Buscar viajes por cuenta",
            description = "Obtiene todos los viajes asociados a una cuenta específica")
    @ApiResponse(responseCode = "200", description = "Lista de viajes de la cuenta",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    public ResponseEntity<List<ViajeDTO.Response>> findByCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(service.findByCuenta(idCuenta));
    }

    @GetMapping("/monopatin/{idMonopatin}")
    @Operation(summary = "Buscar viajes por monopatín",
            description = "Obtiene todos los viajes realizados con un monopatín específico")
    @ApiResponse(responseCode = "200", description = "Lista de viajes del monopatín",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    public ResponseEntity<List<ViajeDTO.Response>> findByMonopatin(@PathVariable Long idMonopatin) {
        return ResponseEntity.ok(service.findByMonopatin(idMonopatin));
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener todos los viajes activos",
            description = "Obtiene todos los viajes que están EN_CURSO o PAUSADOS")
    @ApiResponse(responseCode = "200", description = "Lista de viajes activos",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Resumen.class)))
    public ResponseEntity<List<ViajeDTO.Resumen>> findViajesActivos() {
        return ResponseEntity.ok(service.findViajesActivos());
    }

    @GetMapping("/monopatin/{idMonopatin}/activo")
    @Operation(summary = "Obtener viaje activo de un monopatín",
            description = "Obtiene el viaje activo (EN_CURSO o PAUSADO) de un monopatín específico")
    @ApiResponse(responseCode = "200", description = "Viaje activo encontrado",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "No hay viaje activo para este monopatín",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ViajeDTO.Response> findViajeActivoByMonopatin(@PathVariable Long idMonopatin) {
        return ResponseEntity.ok(service.findViajeActivoByMonopatin(idMonopatin));
    }
}


