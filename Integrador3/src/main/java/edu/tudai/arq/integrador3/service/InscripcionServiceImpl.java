package edu.tudai.arq.integrador3.service;

import edu.tudai.arq.integrador3.dto.InscripcionDTO;
import edu.tudai.arq.integrador3.exception.CarreraNotFoundException;
import edu.tudai.arq.integrador3.exception.EstudianteNotFoundException;
import edu.tudai.arq.integrador3.exception.MatriculaFoundException;
import edu.tudai.arq.integrador3.model.Inscripcion;
import edu.tudai.arq.integrador3.model.InscripcionId;
import edu.tudai.arq.integrador3.repository.CarreraRepository;
import edu.tudai.arq.integrador3.repository.EstudianteRepository;
import edu.tudai.arq.integrador3.repository.InscripcionRepository;
import edu.tudai.arq.integrador3.service.interfaces.InscripcionService;
import org.springframework.stereotype.Service;

@Service
public class InscripcionServiceImpl implements InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;

    public InscripcionServiceImpl(InscripcionRepository inscripcionRepository,
                                  EstudianteRepository estudianteRepository,
                                  CarreraRepository carreraRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
    }

    @Override
    public String matricularEstudianteEnCarrera(InscripcionDTO.Create in) {
        var est = estudianteRepository.findById(in.estudianteId())
                .orElseThrow(() ->
                        new EstudianteNotFoundException("Estudiante no encontrado con ID: " + in.estudianteId()));

        var car = carreraRepository.findById(in.carreraId())
                .orElseThrow(() ->
                        new CarreraNotFoundException("Carrera no encontrada con ID: " + in.carreraId()));

        // Verificar duplicado por PK compuesta
        var id = new InscripcionId(est.getIdEstudiante(), car.getIdCarrera());
        if (inscripcionRepository.existsById(id)) {
            throw new MatriculaFoundException(
                    "El estudiante " + est.getIdEstudiante() +
                            " ya está inscripto en la carrera " + car.getIdCarrera()
            );
        }

        inscripcionRepository.save(new Inscripcion(est, car));

        // Respuesta simple
        return "Inscripción creada correctamente";
    }
}
