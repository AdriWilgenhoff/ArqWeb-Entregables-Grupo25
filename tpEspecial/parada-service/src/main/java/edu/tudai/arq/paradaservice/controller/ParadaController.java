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
@Tag(name = "Paradas", description = "API para gestión de paradas/estaciones de monopatines")
public class ParadaController {

    private final ParadaService service;

    public ParadaController(ParadaService service) {
        this.service = service;
    }

    // TODO: Requiere rol ADMIN - Registrar parada
    @PostMapping
    @Operation(summary = "Crear una nueva parada")
    @ApiResponse(responseCode = "201", description = "Parada creada exitosamente",
            content = @Content(schema = @Schema(implementation = ParadaDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ParadaDTO.Response> create(@Valid @RequestBody ParadaDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    // TODO: Requiere rol ADMIN
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una parada existente")
    @ApiResponse(responseCode = "200", description = "Parada actualizada exitosamente",
            content = @Content(schema = @Schema(implementation = ParadaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Parada no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ParadaDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody ParadaDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    // TODO: Requiere rol ADMIN - Quitar parada
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una parada")
    @ApiResponse(responseCode = "204", description = "Parada eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Parada no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una parada por ID")
    @ApiResponse(responseCode = "200", description = "Parada encontrada",
            content = @Content(schema = @Schema(implementation = ParadaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Parada no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<ParadaDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todas las paradas")
    @ApiResponse(responseCode = "200", description = "Lista de paradas")
    public ResponseEntity<List<ParadaDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/cercanas")
    @Operation(summary = "Buscar paradas cercanas a una ubicación",
            description = "Busca paradas por igualdad exacta de coordenadas")
    @ApiResponse(responseCode = "200", description = "Lista de paradas cercanas")
    public ResponseEntity<List<ParadaDTO.Response>> findParadasCercanas(
            @RequestParam Integer latitud,
            @RequestParam Integer longitud,
            @RequestParam(required = false, defaultValue = "1.0") Double radioKm) {
        return ResponseEntity.ok(service.findParadasCercanas(latitud, longitud, radioKm));
    }

}

