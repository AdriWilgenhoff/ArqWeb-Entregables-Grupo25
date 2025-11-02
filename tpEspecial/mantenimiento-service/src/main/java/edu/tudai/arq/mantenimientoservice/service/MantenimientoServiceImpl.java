package edu.tudai.arq.mantenimientoservice.service;

import edu.tudai.arq.mantenimientoservice.dto.MantenimientoDTO;
import edu.tudai.arq.mantenimientoservice.dto.ReporteUsoDTO;
import edu.tudai.arq.mantenimientoservice.entity.Mantenimiento;
import edu.tudai.arq.mantenimientoservice.exception.MantenimientoNotFoundException;
import edu.tudai.arq.mantenimientoservice.feignclient.MonopatinFeignClient;
import edu.tudai.arq.mantenimientoservice.feignclient.ViajeFeignClient;
import edu.tudai.arq.mantenimientoservice.mapper.MantenimientoMapper;
import edu.tudai.arq.mantenimientoservice.repository.MantenimientoRepository;
import edu.tudai.arq.mantenimientoservice.service.interfaces.MantenimientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MantenimientoServiceImpl implements MantenimientoService {

    private final MantenimientoRepository repository;
    private final MantenimientoMapper mapper;
    private final MonopatinFeignClient monopatinClient;
    private final ViajeFeignClient viajeClient;

    public MantenimientoServiceImpl(MantenimientoRepository repository, MantenimientoMapper mapper,
                                   MonopatinFeignClient monopatinClient, ViajeFeignClient viajeClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.monopatinClient = monopatinClient;
        this.viajeClient = viajeClient;
    }

    @Override
    @Transactional
    public MantenimientoDTO.Response create(MantenimientoDTO.Create in) {
        Mantenimiento m = mapper.toEntity(in);
        m = repository.save(m);
        return mapper.toResponse(m);
    }

    @Override
    @Transactional
    public MantenimientoDTO.Response update(Long id, MantenimientoDTO.Update in) {
        Mantenimiento m = repository.findById(id)
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id));

        mapper.update(m, in);
        m = repository.save(m);
        return mapper.toResponse(m);
    }

    @Override
    @Transactional
    public MantenimientoDTO.Response finalizar(Long id, MantenimientoDTO.Update finishData) {
        return update(id, finishData);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MantenimientoDTO.Response findById(Long id) {
        Mantenimiento m = repository.findById(id)
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id));
        return mapper.toResponse(m);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO.Response> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO.Response> findActivos() {
        return repository.findByFechaHoraFinIsNull().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MantenimientoDTO.Response> findFinalizados() {
        return repository.findByFechaHoraFinIsNotNull().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marcarEnMantenimiento(Long idMonopatin) {
        // TODO: Implementar comunicación con monopatin-service para marcar como no disponible
        // Esta funcionalidad requiere Feign Client para comunicarse con monopatin-service
    }

    @Override
    @Transactional
    public void desmarcarMantenimiento(Long idMonopatin, Long idParadaDestino) {
        // TODO: Implementar comunicación con monopatin-service para marcar como disponible
        // y con parada-service para ubicar en parada si se especifica
        // Esta funcionalidad requiere Feign Client
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> operativosVsMantenimiento() {
        // TODO: Implementar comunicación con monopatin-service para obtener estadísticas
        // Esta funcionalidad requiere Feign Client
        long enMantenimiento = findActivos().size();
        return Map.of(
                "en_mantenimiento", enMantenimiento,
                "en_operacion", 0L // Placeholder, debe obtenerse del monopatin-service
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUsoDTO.Response> generarReporteUso(boolean incluirPausas) {
        List<ReporteUsoDTO.Response> reportes = new ArrayList<>();

        try {
            // 1. Obtener todos los monopatines
            var monopatinesResponse = monopatinClient.getAllMonopatines();
            if (monopatinesResponse.getBody() == null) {
                return reportes;
            }

            List<MonopatinFeignClient.MonopatinResponse> monopatines = monopatinesResponse.getBody();

            // 2. Para cada monopatín, obtener sus viajes y calcular estadísticas
            for (var monopatin : monopatines) {
                try {
                    // Obtener viajes del monopatín
                    var viajesResponse = viajeClient.getViajesPorMonopatin(monopatin.id());
                    List<ViajeFeignClient.ViajeResponse> viajes = viajesResponse.getBody() != null
                            ? viajesResponse.getBody()
                            : new ArrayList<>();

                    // 3. Calcular estadísticas
                    double kmTotales = viajes.stream()
                            .filter(v -> v.kilometrosRecorridos() != null)
                            .mapToDouble(ViajeFeignClient.ViajeResponse::kilometrosRecorridos)
                            .sum();

                    long tiempoTotal = calcularTiempoTotalViajes(viajes);
                    long tiempoEnPausas = incluirPausas ? calcularTiempoPausas(viajes) : 0L;
                    long tiempoSinPausas = tiempoTotal - tiempoEnPausas;

                    // 4. Crear el reporte
                    ReporteUsoDTO.Response reporte = new ReporteUsoDTO.Response(
                            monopatin.id(),
                            kmTotales,
                            incluirPausas ? tiempoTotal : tiempoSinPausas,
                            incluirPausas ? tiempoEnPausas : null,
                            tiempoSinPausas,
                            viajes.size(),
                            monopatin.estado()
                    );

                    reportes.add(reporte);

                } catch (Exception e) {
                    // Log error pero continuar con los demás monopatines
                    System.err.println("Error al procesar monopatín " + monopatin.id() + ": " + e.getMessage());
                }
            }

            // 5. Ordenar por kilómetros descendente (los que más necesitan mantenimiento primero)
            reportes.sort((r1, r2) -> Double.compare(r2.kilometrosTotales(), r1.kilometrosTotales()));

        } catch (Exception e) {
            System.err.println("Error al generar reporte de uso: " + e.getMessage());
        }

        return reportes;
    }

    /**
     * Calcula el tiempo total de todos los viajes en minutos
     */
    private long calcularTiempoTotalViajes(List<ViajeFeignClient.ViajeResponse> viajes) {
        return viajes.stream()
                .filter(v -> v.fechaHoraInicio() != null && v.fechaHoraFin() != null)
                .mapToLong(v -> {
                    try {
                        LocalDateTime inicio = LocalDateTime.parse(v.fechaHoraInicio());
                        LocalDateTime fin = LocalDateTime.parse(v.fechaHoraFin());
                        return Duration.between(inicio, fin).toMinutes();
                    } catch (Exception e) {
                        return 0L;
                    }
                })
                .sum();
    }

    /**
     * Calcula el tiempo en pausas (simplificado - asume 10% del tiempo total como pausas)
     * En una implementación real, se obtendría de una relación con entidad Pausa
     */
    private long calcularTiempoPausas(List<ViajeFeignClient.ViajeResponse> viajes) {
        // Simplificación: asumimos que el 10% del tiempo total son pausas
        // En producción, esto debería venir de la entidad Pausa relacionada con el viaje
        long tiempoTotal = calcularTiempoTotalViajes(viajes);
        return (long) (tiempoTotal * 0.10);
    }
}
