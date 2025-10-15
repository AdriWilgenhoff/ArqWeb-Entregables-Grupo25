package edu.tudai.arq.integrador3.controller;

import edu.tudai.arq.integrador3.dto.CarreraDTO;
import edu.tudai.arq.integrador3.dto.CarreraDTOCant;
import edu.tudai.arq.integrador3.dto.ReporteCarreraDTO;
import edu.tudai.arq.integrador3.exception.ApiError;
import edu.tudai.arq.integrador3.service.interfaces.CarreraService;
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
@RequestMapping("/api/v1/carreras")
@Validated
@Tag(name = "Carreras", description = "API para gestión de carreras universitarias")
public class CarreraController {

    private final CarreraService service;

    public CarreraController(CarreraService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva carrera")
    @ApiResponse(responseCode = "201", description = "Carrera creada exitosamente", content = @Content(schema = @Schema(implementation = CarreraDTO.Create.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: nombre duplicado)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CarreraDTO.Response> create(@Valid @RequestBody CarreraDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una carrera existente por ID")
    @ApiResponse(responseCode = "200", description = "Carrera actualizada exitosamente", content = @Content(schema = @Schema(implementation = CarreraDTO.Update.class)))
    @ApiResponse(responseCode = "404", description = "Carrera no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CarreraDTO.Response> update(@PathVariable Long id,
                                                       @Valid @RequestBody CarreraDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una carrera por ID")
    @ApiResponse(responseCode = "204", description = "Carrera eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Carrera no encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar carrera por ID")
    @ApiResponse(responseCode = "200", description = "Carrera encontrada", content = @Content(schema = @Schema(implementation = CarreraDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Carrera no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CarreraDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Obtener todas las carreras")
    @ApiResponse(responseCode = "200", description = "Lista de todas las carreras", content = @Content(schema = @Schema(implementation = CarreraDTO.Response.class)))
    public ResponseEntity<List<CarreraDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Buscar carrera por nombre (ignorando mayúsculas/minúsculas)")
    @ApiResponse(responseCode = "200", description = "Carrera encontrada", content = @Content(schema = @Schema(implementation = CarreraDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Carrera no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<CarreraDTO.Response> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(service.findByNombre(nombre));
    }

    @GetMapping("/ranking-inscriptos")
    @Operation(summary = "Carreras con estudiantes inscriptos, ordenadas por cantidad (DESC)")
    @ApiResponse(responseCode = "200", description = "Ranking de carreras por inscriptos")
    public ResponseEntity<List<CarreraDTOCant>> rankingPorInscriptos() {
        return ResponseEntity.ok(service.getCarrerasOrdenadasPorInscriptos());
    }

    @GetMapping("/carreras/reporte")
    @Operation(summary = "Reporte de carreras con inscriptos y egresados por año", description = "Genera un reporte de las carreras, que para cada carrera incluya información de los\n" +
            "inscriptos y egresados por año. Se deben ordenar las carreras alfabéticamente, y presentar los años de manera cronológica.")
    public ResponseEntity<List<ReporteCarreraDTO>> reporteCarreras() {
        return ResponseEntity.ok(service.generarReporteCarreras());
    }

}