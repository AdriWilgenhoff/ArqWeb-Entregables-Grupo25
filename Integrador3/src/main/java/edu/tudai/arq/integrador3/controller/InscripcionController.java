package edu.tudai.arq.integrador3.controller;

import edu.tudai.arq.integrador3.dto.InscripcionDTO;
import edu.tudai.arq.integrador3.exception.ApiError;
import edu.tudai.arq.integrador3.service.interfaces.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inscripciones")
@Tag(name = "Inscripcion", description = "API para gestión de inscripciones de estudiantes en carreras")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @PostMapping("/matricular")
    @Operation(summary = "Inscribir a un estudiante en una carrera")
    @ApiResponse(responseCode = "200", description = "Estudiante inscripto en la carrera", content = @Content(schema = @Schema(implementation = InscripcionDTO.Create.class)))
    @ApiResponse(responseCode = "409", description = "Inscripcion duplicada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<String> matricular(@RequestBody InscripcionDTO.Create in) {
        String msg = inscripcionService.matricularEstudianteEnCarrera(in);
        return ResponseEntity.ok(msg);
    }
}
