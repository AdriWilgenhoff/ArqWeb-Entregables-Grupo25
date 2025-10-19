package edu.tudai.arq.integrador3.controller;

import edu.tudai.arq.integrador3.dto.EstudianteDTO;
import edu.tudai.arq.integrador3.exception.ApiError;
import edu.tudai.arq.integrador3.model.Genero;
import edu.tudai.arq.integrador3.service.interfaces.EstudianteService;
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
@RequestMapping("/api/v1/estudiantes")
@Validated
@Tag(name = "Estudiantes", description = "API para gestión de estudiantes universitarios")
public class EstudianteController {

    private final EstudianteService service;

    public EstudianteController(EstudianteService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Da de alta un estudiante")
    @ApiResponse(responseCode = "201", description = "Estudiante registrado exitosamente", content = @Content(schema = @Schema(implementation = EstudianteDTO.Create.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: DNI o LU ya existentes)", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<EstudianteDTO.Response> create(@Valid @RequestBody EstudianteDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un estudiante por ID")
    @ApiResponse(responseCode = "200", description = "Estudiante actualizado exitosamente", content = @Content(schema = @Schema(implementation = EstudianteDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<EstudianteDTO.Response> update(@PathVariable Long id,
                                                          @Valid @RequestBody EstudianteDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un estudiante por ID")
    @ApiResponse(responseCode = "204", description = "Estudiante eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Estudiante con inscripciones",content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping
    @Operation(summary = "Obtener la lista de todos los estudiantes")
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes", content = @Content(schema = @Schema(implementation = EstudianteDTO.Response.class)))
    public ResponseEntity<List<EstudianteDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar estudiante por ID")
    @ApiResponse(responseCode = "200", description = "Estudiante encontrado", content = @Content(schema = @Schema(implementation = EstudianteDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<EstudianteDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/lu/{lu}")
    @Operation(summary = "Recupera un estudiante, en base a su número de libreta universitaria (LU)")
    @ApiResponse(responseCode = "200", description = "Estudiante encontrado", content = @Content(schema = @Schema(implementation = EstudianteDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<EstudianteDTO.Response> findByLu(@PathVariable Long lu) {
        return ResponseEntity.ok(service.findByLu(lu));
    }

    //@GetMapping(params = {"sortBy", "direction"})
    @GetMapping("/sorted")
    @Operation(summary = "Recupera todos los estudiantes, y especifica algún criterio de ordenamiento simple", description = "Permite ordenar por un campo simple: \"idEstudiante\", \"apellido\", \"nombre\", \"dni\", \"lu\", \"ciudadResidencia\", \"fechaNacimiento\", \"genero\". Ej: ?sortBy=apellido&direction=asc")
    @ApiResponse(responseCode = "200", description = "Lista de estudiantes",
            content = @Content(schema = @Schema(implementation = EstudianteDTO.Response.class)))
    public ResponseEntity<List<EstudianteDTO.Response>> findAllSorted(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(service.findAllSorted(sortBy, direction));
    }

    @GetMapping("/genero/{genero}")
    @Operation(summary = "Recuperar todos los estudiantes, en base a su género")
    @ApiResponse(responseCode = "200", description = "Lista filtrada de estudiantes",
            content = @Content(schema = @Schema(implementation = EstudianteDTO.Response.class)))
    public ResponseEntity<List<EstudianteDTO.Response>> findAllByGenero(@PathVariable Genero genero) {
        return ResponseEntity.ok(service.findAllByGenero(genero));
    }


    @GetMapping("/carreras/{carreraId}/ciudad/{ciudad}")
    @Operation(summary = "Recupera los estudiantes de una determinada carrera, filtrado por ciudad de residencia")
    public ResponseEntity<List<EstudianteDTO.Response>> byCarreraAndCiudad(
            @PathVariable Long carreraId,
            @PathVariable String ciudad) {
        return ResponseEntity.ok(service.findByCarreraAndCiudad(carreraId, ciudad.trim()));
    }

}