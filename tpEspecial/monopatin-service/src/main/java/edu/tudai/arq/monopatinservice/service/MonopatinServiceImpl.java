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

    @Override
    @Transactional(readOnly = true)
    public List<MonopatinDTO.Response> findMonopatinesCercanos(Double latitud, Double longitud, Double radioKm) {
        // Obtener todos los monopatines disponibles
        List<Monopatin> monopatinesDisponibles = repository.findAll().stream()
                .filter(m -> m.getEstado() == EstadoMonopatin.DISPONIBLE)
                .toList();

        // Filtrar por distancia usando fórmula de Haversine
        return monopatinesDisponibles.stream()
                .filter(m -> {
                    double distancia = calcularDistanciaHaversine(
                            latitud, longitud,
                            m.getLatitud(), m.getLongitud()
                    );
                    return distancia <= radioKm;
                })
                .sorted((m1, m2) -> {
                    // Ordenar por distancia (más cercano primero)
                    double dist1 = calcularDistanciaHaversine(latitud, longitud, m1.getLatitud(), m1.getLongitud());
                    double dist2 = calcularDistanciaHaversine(latitud, longitud, m2.getLatitud(), m2.getLongitud());
                    return Double.compare(dist1, dist2);
                })
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calcula la distancia entre dos puntos GPS usando la fórmula de Haversine.
     * @return distancia en kilómetros
     */
    private double calcularDistanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        final double RADIO_TIERRA_KM = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_KM * c;
    }
}