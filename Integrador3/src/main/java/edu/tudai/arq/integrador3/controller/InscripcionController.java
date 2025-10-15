package edu.tudai.arq.integrador3.controller;

import edu.tudai.arq.integrador3.dto.InscripcionDTO;
import edu.tudai.arq.integrador3.service.interfaces.InscripcionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    // POST /api/v1/inscripciones/matricular
    @PostMapping("/matricular")
    public ResponseEntity<String> matricular(@RequestBody InscripcionDTO.Create in) {
        String msg = inscripcionService.matricularEstudianteEnCarrera(in);
        return ResponseEntity.ok(msg);
    }
}
