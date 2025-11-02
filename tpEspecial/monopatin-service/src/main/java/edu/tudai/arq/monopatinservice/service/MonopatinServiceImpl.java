package edu.tudai.arq.monopatinservice.service;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.exception.InvalidStateTransitionException;
import edu.tudai.arq.monopatinservice.exception.MonopatinNotFoundException;
import edu.tudai.arq.monopatinservice.mapper.MonopatinMapper;
import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import edu.tudai.arq.monopatinservice.repository.MonopatinRepository;
import edu.tudai.arq.monopatinservice.service.interfaces.MonopatinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonopatinServiceImpl implements MonopatinService {

    private final MonopatinRepository repository;
    private final MonopatinMapper mapper;

    public MonopatinServiceImpl(MonopatinRepository repository, MonopatinMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public MonopatinDTO.Response create(MonopatinDTO.Create dto) {
        Monopatin entity = mapper.toEntity(dto);
        Monopatin savedEntity = repository.save(entity);
        return mapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonopatinDTO.Response> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MonopatinDTO.Response findById(Long id) {
        Monopatin entity = repository.findById(id)
                .orElseThrow(() -> new MonopatinNotFoundException(id));
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public MonopatinDTO.Response update(Long id, MonopatinDTO.Update dto) {
        Monopatin entity = repository.findById(id)
                .orElseThrow(() -> new MonopatinNotFoundException(id));

        mapper.update(entity, dto);

        Monopatin updatedEntity = repository.save(entity);
        return mapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Monopatin entity = repository.findById(id)
                .orElseThrow(() -> new MonopatinNotFoundException(id));

        repository.delete(entity);
    }

    @Override
    @Transactional
    public MonopatinDTO.Response cambiarEstado(Long id, String nuevoEstadoString) {
        Monopatin entity = repository.findById(id)
                .orElseThrow(() -> new MonopatinNotFoundException(id));
        EstadoMonopatin nuevoEstado;
        try {
            nuevoEstado = EstadoMonopatin.valueOf(nuevoEstadoString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidStateTransitionException(id, entity.getEstado().name(), nuevoEstadoString);
        }

        EstadoMonopatin estadoActual = entity.getEstado();

        if (estadoActual == EstadoMonopatin.EN_MANTENIMIENTO && nuevoEstado != EstadoMonopatin.DISPONIBLE) {
            throw new InvalidStateTransitionException(id, estadoActual.name(), nuevoEstado.name());
        }

        entity.setEstado(nuevoEstado);
        Monopatin updatedEntity = repository.save(entity);
        return mapper.toResponse(updatedEntity);
    }
}