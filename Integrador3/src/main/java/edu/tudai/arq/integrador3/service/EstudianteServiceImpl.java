package edu.tudai.arq.integrador3.service;

import edu.tudai.arq.integrador3.dto.EstudianteDTO;
import edu.tudai.arq.integrador3.exception.CarreraConInscripcionesException;
import edu.tudai.arq.integrador3.exception.EstudianteNotFoundException;
import edu.tudai.arq.integrador3.model.Estudiante;
import edu.tudai.arq.integrador3.model.Genero;
import edu.tudai.arq.integrador3.repository.EstudianteRepository;
import edu.tudai.arq.integrador3.mapper.EstudianteMapper;

import edu.tudai.arq.integrador3.service.interfaces.EstudianteService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository estudianteRepo;
    private final EstudianteMapper estudianteMapper;

    public EstudianteServiceImpl(EstudianteRepository estudianteRepo,  EstudianteMapper estudianteMapper) {
        this.estudianteRepo = estudianteRepo;
        this.estudianteMapper = estudianteMapper;
    }

    /** Creación de Estudiante con validación de DNI y LU. */
    @Override
    @Transactional
    public EstudianteDTO.Response create(EstudianteDTO.Create in) {
        if (estudianteRepo.existsByDni(in.dni())) {
            throw new IllegalArgumentException("El DNI " + in.dni() + " ya está registrado para otro estudiante.");
        }
        if (estudianteRepo.existsByLu(in.lu())) {
            throw new IllegalArgumentException("La Libreta Universitaria (LU) " + in.lu() + " ya está asignada.");
        }

        Estudiante e = estudianteMapper.toEntity(in);
        e = estudianteRepo.save(e);
        return estudianteMapper.toResponse(e);
    }

    /** Actualización de Estudiante. */
    @Override
    @Transactional
    public EstudianteDTO.Response update(Long id, EstudianteDTO.Update in) {
        Estudiante e = estudianteRepo.findById(id).orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrada con ID: " + id));

        estudianteMapper.update(e, in);
        e = estudianteRepo.save(e);
        return estudianteMapper.toResponse(e);
    }

    /** Eliminación de Estudiante. */
    @Override
    @Transactional
    public void delete(Long id) {
        if (!estudianteRepo.existsById(id)) {
            throw new EstudianteNotFoundException("Estudiante no encontrada con ID: " + id);
        }
        try {
            estudianteRepo.deleteById(id);
            estudianteRepo.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new CarreraConInscripcionesException("No se puede borrar el estudiante ID " + id + " porque tiene inscripciones.");
        }
    }

    /** Búsqueda por ID. */
    @Override
    public EstudianteDTO.Response findById(Long id) {
        Estudiante e = estudianteRepo.findById(id).orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrada con ID: " + id));
        return estudianteMapper.toResponse(e);
    }

    /** Búsqueda por Libreta Universitaria (LU). */
    @Override
    public EstudianteDTO.Response findByLu(Long lu) {
        Estudiante e = estudianteRepo.findByLu(lu).orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado con LU: " + lu));
        return estudianteMapper.toResponse(e);
    }

    /** Obtener todos los estudiantes. */
    @Override
    public List<EstudianteDTO.Response> findAll() {
        return estudianteRepo.findAll().stream().map(estudianteMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<EstudianteDTO.Response> findAllSorted(String sortBy, String direction) {
        var allowed = java.util.Set.of("idEstudiante", "apellido", "nombre", "dni", "lu", "ciudadResidencia", "fechaNacimiento", "genero");
        if (!allowed.contains(sortBy)) {
            sortBy = "idEstudiante";
        }

        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return estudianteRepo.findAll(Sort.by(dir, sortBy)).stream().map(estudianteMapper::toResponse).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<EstudianteDTO.Response> findAllByGenero(Genero genero) {
        List<Estudiante> estudiantes = estudianteRepo.findAllByGenero(genero);
        List<EstudianteDTO.Response> responses = new java.util.ArrayList<>();
        for (Estudiante estudiante : estudiantes) {
            responses.add(estudianteMapper.toResponse(estudiante));
        }
        return responses;
    }

    @Override
    public List<EstudianteDTO.Response> findByCarreraAndCiudad(Long carreraId, String ciudad) {
        List<Estudiante> estudiantes = estudianteRepo.getEstudiantesByCarreraAndCiudad(carreraId, ciudad);

        if (estudiantes == null || estudiantes.isEmpty()) {
            return new ArrayList<>();
        }
        List<EstudianteDTO.Response> responses = new ArrayList<>(estudiantes.size());
        for (Estudiante e : estudiantes) {
            EstudianteDTO.Response dto = estudianteMapper.toResponse(e);
            responses.add(dto);
        }
        return responses;
    }

}