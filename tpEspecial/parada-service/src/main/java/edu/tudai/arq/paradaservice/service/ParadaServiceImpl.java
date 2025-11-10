package edu.tudai.arq.paradaservice.service;

import edu.tudai.arq.paradaservice.dto.ParadaDTO;
import edu.tudai.arq.paradaservice.entity.Parada;
import edu.tudai.arq.paradaservice.exception.ParadaNotFoundException;
import edu.tudai.arq.paradaservice.mapper.ParadaMapper;
import edu.tudai.arq.paradaservice.repository.ParadaRepository;
import edu.tudai.arq.paradaservice.service.interfaces.ParadaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParadaServiceImpl implements ParadaService {

    private final ParadaRepository repository;
    private final ParadaMapper mapper;

    public ParadaServiceImpl(ParadaRepository repository, ParadaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ParadaDTO.Response create(ParadaDTO.Create in) {
        Parada p = mapper.toEntity(in);
        p = repository.save(p);
        return mapper.toResponse(p);
    }

    @Override
    @Transactional
    public ParadaDTO.Response update(Long id, ParadaDTO.Update in) {
        Parada p = repository.findById(id)
                .orElseThrow(() -> new ParadaNotFoundException(id));

        mapper.update(p, in);
        p = repository.save(p);
        return mapper.toResponse(p);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ParadaNotFoundException(id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ParadaDTO.Response findById(Long id) {
        Parada p = repository.findById(id)
                .orElseThrow(() -> new ParadaNotFoundException(id));
        return mapper.toResponse(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParadaDTO.Response> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParadaDTO.Response> findParadasCercanas(Integer latitud, Integer longitud, Double radioKm) {
        return repository.findAll().stream()
                .filter(p -> {
                    int diffLat = Math.abs(p.getLatitud() - latitud);
                    int diffLon = Math.abs(p.getLongitud() - longitud);
                    double distancia = diffLat + diffLon;
                    return distancia <= radioKm;
                })
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

}

