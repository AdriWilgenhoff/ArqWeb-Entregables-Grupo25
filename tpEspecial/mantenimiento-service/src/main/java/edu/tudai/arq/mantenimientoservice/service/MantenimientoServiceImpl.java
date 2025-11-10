package edu.tudai.arq.mantenimientoservice.service;

import edu.tudai.arq.mantenimientoservice.dto.MantenimientoDTO;
import edu.tudai.arq.mantenimientoservice.dto.ReporteOperacionDTO;
import edu.tudai.arq.mantenimientoservice.dto.ReporteUsoDTO;
import edu.tudai.arq.mantenimientoservice.entity.Mantenimiento;
import edu.tudai.arq.mantenimientoservice.exception.MantenimientoNotFoundException;
import edu.tudai.arq.mantenimientoservice.exception.ServiceCommunicationException;
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
        monopatinClient.cambiarEstado(m.getIdMonopatin(), "EN_MANTENIMIENTO");
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
        Mantenimiento m = repository.findById(id)
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id));

        Long idMonopatin = m.getIdMonopatin();

        MantenimientoDTO.Response response = update(id, finishData);

        monopatinClient.cambiarEstado(idMonopatin, "DISPONIBLE");

        return response;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Mantenimiento m = repository.findById(id)
                .orElseThrow(() -> new MantenimientoNotFoundException("Mantenimiento no encontrado con ID: " + id));

        if (m.getFechaHoraFin() == null) {
            monopatinClient.cambiarEstado(m.getIdMonopatin(), "DISPONIBLE");
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

    // ==================== REPORTES ====================

    @Override
    @Transactional(readOnly = true)
    public ReporteOperacionDTO operativosVsMantenimiento() {
        try {
            var response = monopatinClient.getAllMonopatines();
            if (response.getBody() == null) {
                throw new ServiceCommunicationException("No se pudo obtener la lista de monopatines desde monopatin-service");
            }

            List<MonopatinFeignClient.MonopatinResponse> monopatines = response.getBody();

            long enOperacion = monopatines.stream()
                    .filter(m -> "DISPONIBLE".equals(m.estado()) || "EN_USO".equals(m.estado()))
                    .count();

            long enMantenimiento = monopatines.stream()
                    .filter(m -> "EN_MANTENIMIENTO".equals(m.estado()))
                    .count();

            return new ReporteOperacionDTO(enOperacion, enMantenimiento);
        } catch (ServiceCommunicationException e) {
            throw e; // Re-lanzar la excepción personalizada
        } catch (Exception e) {
            throw new ServiceCommunicationException("Error al comunicarse con monopatin-service: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUsoDTO.Response> generarReporteUso(boolean incluirPausas) {
        List<ReporteUsoDTO.Response> reportes = new ArrayList<>();

        try {
            var monopatinesResponse = monopatinClient.getAllMonopatines();
            if (monopatinesResponse.getBody() == null) {
                throw new ServiceCommunicationException("No se pudo obtener la lista de monopatines desde monopatin-service");
            }

            List<MonopatinFeignClient.MonopatinResponse> monopatines = monopatinesResponse.getBody();

            for (var monopatin : monopatines) {
                try {
                    var viajesResponse = viajeClient.getViajesPorMonopatin(monopatin.id());
                    List<ViajeFeignClient.ViajeResponse> viajes = viajesResponse.getBody() != null
                            ? viajesResponse.getBody()
                            : new ArrayList<>();

                    double kmTotales = viajes.stream()
                            .filter(v -> v.kilometrosRecorridos() != null)
                            .mapToDouble(ViajeFeignClient.ViajeResponse::kilometrosRecorridos)
                            .sum();

                    long tiempoTotal = calcularTiempoTotalViajes(viajes);
                    long tiempoEnPausas = incluirPausas ? calcularTiempoPausas(viajes) : 0L;
                    long tiempoSinPausas = tiempoTotal - tiempoEnPausas;

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
                      throw new ServiceCommunicationException("Error al procesar monopatín " + monopatin.id() + ": " + e.getMessage(), e);
                }
            }

            reportes.sort((r1, r2) -> Double.compare(r2.kilometrosTotales(), r1.kilometrosTotales()));

            return reportes;

        } catch (ServiceCommunicationException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceCommunicationException("Error al generar reporte de uso: " + e.getMessage(), e);
        }
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
        long tiempoTotal = calcularTiempoTotalViajes(viajes);
        return (long) (tiempoTotal * 0.10);
    }
}
