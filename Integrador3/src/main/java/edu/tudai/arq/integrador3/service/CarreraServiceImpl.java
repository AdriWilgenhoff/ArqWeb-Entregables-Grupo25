package edu.tudai.arq.integrador3.service;

import edu.tudai.arq.integrador3.dto.CarreraDTO;
import edu.tudai.arq.integrador3.dto.CarreraDTOCant;
import edu.tudai.arq.integrador3.dto.ReporteCarreraDTO;
import edu.tudai.arq.integrador3.mapper.CarreraMapper;
import edu.tudai.arq.integrador3.model.Carrera;
import edu.tudai.arq.integrador3.repository.CarreraRepository;
import edu.tudai.arq.integrador3.exception.CarreraNotFoundException; // Asumiendo que existe
import edu.tudai.arq.integrador3.service.interfaces.CarreraService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository repo;
    private final CarreraMapper mapper;

    public CarreraServiceImpl(CarreraRepository repo, CarreraMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CarreraDTO.Response create(CarreraDTO.Create in) {
        if (repo.findByNombreIgnoreCase(in.nombre()) != null) {
            throw new IllegalArgumentException("Ya existe una carrera con el nombre: " + in.nombre());
        }

        Carrera c = mapper.toEntity(in);
        c = repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public CarreraDTO.Response update(Long id, CarreraDTO.Update in) {
        Carrera c = repo.findById(id)
                .orElseThrow(() -> new CarreraNotFoundException("Carrera no encontrada con ID: " + id));

        Optional<Carrera> existing = Optional.ofNullable(repo.findByNombreIgnoreCase(in.nombre()));
        if (existing.isPresent() && !existing.get().getIdCarrera().equals(id)) {
            throw new IllegalArgumentException("Ya existe otra carrera con el nombre: " + in.nombre());
        }

        mapper.update(c, in);
        c = repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new CarreraNotFoundException("Carrera no encontrada con ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraDTO.Response findById(Long id) {
        Carrera c = repo.findById(id)
                .orElseThrow(() -> new CarreraNotFoundException("Carrera no encontrada con ID: " + id));
        return mapper.toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarreraDTO.Response> findAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraDTO.Response findByNombre(String nombre) {
        Carrera c = repo.findByNombreIgnoreCase(nombre);

        if (c == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Carrera no encontrada con nombre: " + nombre);
        }
        return mapper.toResponse(c);
    }

    @Override
    public List<CarreraDTOCant> getCarrerasOrdenadasPorInscriptos() {
        return repo.findCarrerasWithInscritosOrdered();
    }

    @Override
    public List<ReporteCarreraDTO> generarReporteCarreras() {

        List<Object[]> rows = repo.generarReporteCarreras();

        List<ReporteCarreraDTO> out = new java.util.ArrayList<>(rows.size());
        for (Object[] r : rows) {
            String nombreCarrera  = (String) r[0];
            int anio              = ((Number) r[1]).intValue();
            long cantGraduados    = ((Number) r[2]).longValue();
            long cantInscriptos   = ((Number) r[3]).longValue();

            out.add(new ReporteCarreraDTO(nombreCarrera, anio, cantGraduados, cantInscriptos));
        }
        return out;
    }
}