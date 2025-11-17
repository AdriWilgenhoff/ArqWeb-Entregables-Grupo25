package edu.tudai.arq.paradaservice.controller;

import edu.tudai.arq.paradaservice.dto.ParadaDTO;
import edu.tudai.arq.paradaservice.exception.ApiError;
import edu.tudai.arq.paradaservice.service.interfaces.ParadaService;
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
@RequestMapping("/api/v1/paradas")
@Validated
@Tag(name = "Paradas", description = "API para gestión de paradas/estaciones de monopatines. ⚠️ IMPORTANTE: Este servicio debe consumirse a través del API Gateway (puerto 8080) para que la autenticación y autorización funcionen correctamente.")
public class ParadaController {

    private final ParadaService service;

    public ParadaController(ParadaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Crear una nueva parada",
            description = "🔒 Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/paradas"
    )
    @ApiResponse(responseCode = "201", description = "Parada creada exitosamente",
            content = @Content(schema = @Schema(implementation = ParadaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<ParadaDTO.Response> create(@Valid @RequestBody ParadaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar una parada existente",
            description = "🔒 Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/paradas/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Parada actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = ParadaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Parada no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<ParadaDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody ParadaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar una parada",
            description = "🔒 Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/paradas/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Parada eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Parada no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener una parada por ID",
            description = "🔓 Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/paradas/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Parada encontrada",
            content = @Content(schema = @Schema(implementation = ParadaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Parada no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<ParadaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Listar todas las paradas",
            description = "🔓 Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/paradas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de paradas")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<List<ParadaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/cercanas")
    @Operation(
            summary = "Buscar paradas cercanas a una ubicación",
            description = "🔓 Accesible para usuarios autenticados. Busca paradas dentro del radio especificado. Acceso: http://localhost:8080/api/v1/paradas/cercanas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de paradas cercanas")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<List<ParadaDTO.Response>> findParadasCercanas(
            @RequestParam Integer latitud,
            @RequestParam Integer longitud,
            @RequestParam(required = false, defaultValue = "1.0") Double radioKm) {
        return ResponseEntity.ok(service.findParadasCercanas(latitud, longitud, radioKm));
    }

}

