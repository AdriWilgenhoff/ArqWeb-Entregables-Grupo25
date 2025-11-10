package edu.tudai.arq.monopatinservice.service;

import edu.tudai.arq.monopatinservice.dto.MonopatinDTO;
import edu.tudai.arq.monopatinservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.monopatinservice.dto.ReporteUsoDTO;
import edu.tudai.arq.monopatinservice.exception.InvalidStateTransitionException;
import edu.tudai.arq.monopatinservice.exception.MonopatinNotFoundException;
import edu.tudai.arq.monopatinservice.exception.ServiceCommunicationException;
import edu.tudai.arq.monopatinservice.feignclient.ViajeFeignClient;
import edu.tudai.arq.monopatinservice.mapper.MonopatinMapper;
import edu.tudai.arq.monopatinservice.entity.EstadoMonopatin;
import edu.tudai.arq.monopatinservice.entity.Monopatin;
import edu.tudai.arq.monopatinservice.repository.MonopatinRepository;
import edu.tudai.arq.monopatinservice.service.interfaces.MonopatinService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MonopatinServiceImpl implements MonopatinService {

    private final MonopatinRepository repository;
    private final MonopatinMapper mapper;
    private final ViajeFeignClient viajeClient;

    public MonopatinServiceImpl(MonopatinRepository repository, MonopatinMapper mapper, ViajeFeignClient viajeClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.viajeClient = viajeClient;
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
    public List<MonopatinDTO.Response> findMonopatinesCercanos(Integer latitud, Integer longitud, Double radioKm) {
        return repository.findAll().stream()
                .filter(m -> m.getEstado() == EstadoMonopatin.DISPONIBLE)
                .filter(m -> {
                    int diffLat = Math.abs(m.getLatitud() - latitud);
                    int diffLon = Math.abs(m.getLongitud() - longitud);
                    double distancia = diffLat + diffLon;
                    return distancia <= radioKm;
                })
                .sorted((m1, m2) -> {
                    int dist1 = Math.abs(m1.getLatitud() - latitud) + Math.abs(m1.getLongitud() - longitud);
                    int dist2 = Math.abs(m2.getLatitud() - latitud) + Math.abs(m2.getLongitud() - longitud);
                    return Integer.compare(dist1, dist2);
                })
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }


    // ==================== REPORTES ====================

    @Override
    @Transactional(readOnly = true)
    public List<MonopatinDTO.Response> findMonopatinesConMasDeXViajes(Integer cantidadViajes, Integer anio) {
        try {
            // Llamar a viaje-service usando FeignClient
            List<Long> idsMonopatines = viajeClient.getMonopatinesConMasDeXViajes(cantidadViajes, anio);

            if (idsMonopatines == null || idsMonopatines.isEmpty()) {
                return List.of();
            }

            // Buscar monopatines por IDs
            List<Monopatin> monopatines = repository.findByIdIn(idsMonopatines);

            return monopatines.stream()
                    .map(mapper::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceCommunicationException("Error al obtener monopatines con más viajes desde viaje-service: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteOperacionDTO getReporteOperacion() {
        long enDisponible = repository.countByEstado(EstadoMonopatin.DISPONIBLE);
        long enUso = repository.countByEstado(EstadoMonopatin.EN_USO);
        long totalEnOperacion = enDisponible + enUso;

        long enMantenimiento = repository.countByEstado(EstadoMonopatin.EN_MANTENIMIENTO);

        long totalMonopatines = totalEnOperacion + enMantenimiento;

        double porcentajeOperacion = totalMonopatines > 0 ?
                (totalEnOperacion * 100.0 / totalMonopatines) : 0.0;
        double porcentajeMantenimiento = totalMonopatines > 0 ?
                (enMantenimiento * 100.0 / totalMonopatines) : 0.0;

        return new ReporteOperacionDTO(
                totalEnOperacion,
                enMantenimiento,
                porcentajeOperacion,
                porcentajeMantenimiento
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> getReporteUsoPorKilometros(boolean incluirPausas) {
        List<Monopatin> monopatines = repository.findAll();

        if (incluirPausas) {
            // Retornar con detalles de pausas
            return monopatines.stream()
                    .filter(m -> m.getKilometrosTotales() > 0)
                    .map(m -> new ReporteUsoDTO.ConPausas(
                            m.getId(),
                            m.getKilometrosTotales(),
                            m.getTiempoUsoTotal() - m.getTiempoPausas(),  // Tiempo sin pausas
                            m.getTiempoPausas()                           // Tiempo de pausas
                    ))
                    .sorted((r1, r2) -> Double.compare(r2.kilometrosTotales(), r1.kilometrosTotales()))
                    .collect(Collectors.toList());
        } else {
            // Retornar simple, sin pausas
            return monopatines.stream()
                    .filter(m -> m.getKilometrosTotales() > 0)
                    .map(m -> new ReporteUsoDTO.Simple(
                            m.getId(),
                            m.getKilometrosTotales(),
                            m.getTiempoUsoTotal() - m.getTiempoPausas()   // Tiempo sin pausas
                    ))
                    .sorted((r1, r2) -> Double.compare(r2.kilometrosTotales(), r1.kilometrosTotales()))
                    .collect(Collectors.toList());
        }
    }
}