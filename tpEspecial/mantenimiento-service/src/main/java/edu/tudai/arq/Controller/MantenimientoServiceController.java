package edu.tudai.arq.Controller;
import edu.tudai.arq.entity.MantenimientoService;
import edu.tudai.arq.dto.MantenimientoServiceDTO;
import edu.tudai.arq.exception.ApiError;
import edu.tudai.arq.service.Interface.MantenimientoServiceInterface;
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
@RequestMapping("/api/v1/mantenimientos")
@Validated
@Tag(name = "Mantenimientos", description = "API para gestión de mantenimientos de monopatines")
public class MantenimientoController {

    private final MantenimientoService service;

    public MantenimientoController(MantenimientoService service) {
        this.service = service;
    }


    @PostMapping
    @Operation(summary = "Iniciar registro de mantenimiento")
    @ApiResponse(responseCode = "201", description = "Mantenimiento creado",
            content = @Content(schema = @Schema(implementation = MantenimientoServiceDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MantenimientoServiceDTO.Response> create(
            @Valid @RequestBody MantenimientoServiceDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }


    @PutMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar mantenimiento")
    @ApiResponse(responseCode = "200", description = "Mantenimiento finalizado",
            content = @Content(schema = @Schema(implementation = MantenimientoServiceDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "No encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MantenimientoServiceDTO.Response> finalizar(
            @PathVariable Long id,
            @Valid @RequestBody MantenimientoServiceDTO.Update finishData) {
        var out = service.finalizar(id, finishData);
        return ResponseEntity.ok(out);
    }

    @PutMapping("/{id}/marcar")
    @Operation(summary = "Marcar monopatín como en mantenimiento (no disponible para uso)")
    @ApiResponse(responseCode = "200", description = "Marcado como en mantenimiento")
    public ResponseEntity<Void> marcarEnMantenimiento(@PathVariable Long id) {
        service.marcarEnMantenimiento(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/desmarcar")
    @Operation(summary = "Desmarcar monopatín (habilitar para uso) al terminar mantenimiento")
    @ApiResponse(responseCode = "200", description = "Desmarcado y habilitado")
    public ResponseEntity<Void> desmarcarMantenimiento(@PathVariable Long id,
                                                       @RequestParam(required = false) Long idParadaDestino) {
        service.desmarcarMantenimiento(id, idParadaDestino);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener mantenimiento por ID")
    @ApiResponse(responseCode = "200", description = "Mantenimiento encontrado",
            content = @Content(schema = @Schema(implementation = MantenimientoServiceDTO.Response.class)))
    public ResponseEntity<MantenimientoServiceDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos los mantenimientos")
    public ResponseEntity<List<MantenimientoServiceDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar mantenimientos activos (fechaHoraFin == null)")
    public ResponseEntity<List<MantenimientoServiceDTO.Response>> findActivos() {
        return ResponseEntity.ok(service.findActivos());
    }

    @GetMapping("/finalizados")
    @Operation(summary = "Listar mantenimientos finalizados (fechaHoraFin != null)")
    public ResponseEntity<List<MantenimientoServiceDTO.Response>> findFinalizados() {
        return ResponseEntity.ok(service.findFinalizados());
    }


    // 3e) Cantidad en operación vs en mantenimiento
    @GetMapping("/estadisticas/operativos-vs-mantenimiento")
    @Operation(summary = "Cantidad de monopatines en operación vs en mantenimiento")
    public ResponseEntity<Map<String, Long>> operativosVsMantenimiento() {
        var mapa = service.operativosVsMantenimiento();
        return ResponseEntity.ok(mapa);
    }


}