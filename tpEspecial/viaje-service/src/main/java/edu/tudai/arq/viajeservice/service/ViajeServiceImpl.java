package edu.tudai.arq.viajeservice.service;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.dto.ReporteUsuarioDTO;
import edu.tudai.arq.viajeservice.dto.ViajeDTO;
import edu.tudai.arq.viajeservice.entity.EstadoViaje;
import edu.tudai.arq.viajeservice.entity.Pausa;
import edu.tudai.arq.viajeservice.entity.Viaje;
import edu.tudai.arq.viajeservice.exception.PausaNotFoundException;
import edu.tudai.arq.viajeservice.exception.ViajeInvalidoException;
import edu.tudai.arq.viajeservice.exception.ViajeNotFoundException;
import edu.tudai.arq.viajeservice.feignclient.CuentaFeignClient;
import edu.tudai.arq.viajeservice.feignclient.FacturacionFeignClient;
import edu.tudai.arq.viajeservice.feignclient.MonopatinFeignClient;
import edu.tudai.arq.viajeservice.feignclient.ParadaFeignClient;
import edu.tudai.arq.viajeservice.mapper.PausaMapper;
import edu.tudai.arq.viajeservice.mapper.ViajeMapper;
import edu.tudai.arq.viajeservice.repository.PausaRepository;
import edu.tudai.arq.viajeservice.repository.ViajeRepository;
import edu.tudai.arq.viajeservice.service.interfaces.ViajeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ViajeServiceImpl implements ViajeService {

    private static final Logger logger = LoggerFactory.getLogger(ViajeServiceImpl.class);

    private final ViajeRepository viajeRepo;
    private final PausaRepository pausaRepo;
    private final ViajeMapper viajeMapper;
    private final PausaMapper pausaMapper;
    private final ParadaFeignClient paradaClient;
    private final MonopatinFeignClient monopatinClient;
    private final FacturacionFeignClient facturacionClient;
    private final CuentaFeignClient cuentaClient;

    private static final int MINUTOS_PAUSA_MAXIMA = 15;
    private static final double RADIO_VALIDACION_PARADA_KM = 0.05;

    public ViajeServiceImpl(ViajeRepository viajeRepo, PausaRepository pausaRepo,
                            ViajeMapper viajeMapper, PausaMapper pausaMapper,
                            ParadaFeignClient paradaClient, MonopatinFeignClient monopatinClient,
                            FacturacionFeignClient facturacionClient,
                            CuentaFeignClient cuentaClient) {
        this.viajeRepo = viajeRepo;
        this.pausaRepo = pausaRepo;
        this.viajeMapper = viajeMapper;
        this.pausaMapper = pausaMapper;
        this.paradaClient = paradaClient;
        this.monopatinClient = monopatinClient;
        this.facturacionClient = facturacionClient;
        this.cuentaClient = cuentaClient;
    }

    @Override
    @Transactional
    public ViajeDTO.Response iniciarViaje(ViajeDTO.Create in) {
        try {
            var cuentaResponse = cuentaClient.getCuentaById(in.idCuenta());
            if (cuentaResponse.getBody() == null) {
                throw new ViajeInvalidoException("Cuenta no encontrada con ID: " + in.idCuenta());
            }
            var cuenta = cuentaResponse.getBody();
            if (!cuenta.habilitada()) {
                throw new ViajeInvalidoException("La cuenta con ID " + in.idCuenta() + " está anulada/deshabilitada");
            }
            if (cuenta.saldo() == null || cuenta.saldo() < 0) {
                throw new ViajeInvalidoException("La cuenta no tiene saldo suficiente para iniciar un viaje");
            }
        } catch (ViajeInvalidoException e) {
            throw e;
        } catch (Exception e) {
            throw new ViajeInvalidoException("Error al validar cuenta: " + e.getMessage());
        }

        try {
            var monopatinResponse = monopatinClient.getMonopatinById(in.idMonopatin());
            if (monopatinResponse.getBody() == null) {
                throw new ViajeInvalidoException("Monopatín no encontrado con ID: " + in.idMonopatin());
            }
            var monopatin = monopatinResponse.getBody();
            if (!"DISPONIBLE".equals(monopatin.estado())) {
                throw new ViajeInvalidoException("El monopatín no está disponible (estado: " + monopatin.estado() + ")");
            }
        } catch (Exception e) {
            throw new ViajeInvalidoException("Error al validar monopatín: " + e.getMessage());
        }

        viajeRepo.findViajeActivoByMonopatin(in.idMonopatin()).ifPresent(v -> {
            throw new ViajeInvalidoException("El monopatín con ID " + in.idMonopatin() + " ya tiene un viaje activo");
        });

        Viaje viaje = viajeMapper.toEntity(in);
        viaje = viajeRepo.save(viaje);

        try {
            var monopatinUpdate = new MonopatinFeignClient.MonopatinUpdate(
                    "EN_USO", null, null, null, null, null
            );
            monopatinClient.updateMonopatin(in.idMonopatin(), monopatinUpdate);
        } catch (Exception e) {
            logger.warn("No se pudo actualizar estado del monopatín al iniciar viaje: {}", e.getMessage());
        }

        return viajeMapper.toResponse(viaje);
    }

    @Override
    @Transactional
    public ViajeDTO.Response finalizarViaje(Long id, ViajeDTO.Finalizacion in) {
        Viaje viaje = viajeRepo.findById(id)
                .orElseThrow(() -> new ViajeNotFoundException("Viaje no encontrado con ID: " + id));

        if (viaje.getEstado() == EstadoViaje.FINALIZADO) {
            throw new ViajeInvalidoException("El viaje con ID " + id + " ya está finalizado");
        }

        MonopatinFeignClient.MonopatinResponse monopatin;
        try {
            var response = monopatinClient.getMonopatinById(viaje.getIdMonopatin());
            if (response.getBody() == null) {
                throw new ViajeInvalidoException("No se pudo obtener información del monopatín");
            }
            monopatin = response.getBody();
        } catch (Exception e) {
            throw new ViajeInvalidoException("Error al obtener ubicación del monopatín: " + e.getMessage());
        }

        String idParadaFin;
        try {
            var paradasResponse = paradaClient.findParadasCercanas(
                    monopatin.latitud(),
                    monopatin.longitud(),
                    RADIO_VALIDACION_PARADA_KM
            );

            if (paradasResponse.getBody() == null || paradasResponse.getBody().isEmpty()) {
                throw new ViajeInvalidoException(
                        "No se puede finalizar el viaje. El monopatín debe estar en una parada. " +
                        "No hay paradas cercanas a la ubicación actual (lat: " + monopatin.latitud() +
                        ", lng: " + monopatin.longitud() + ")"
                );
            }

            var paradaMasCercana = paradasResponse.getBody().get(0);
            idParadaFin = paradaMasCercana.id();
            logger.info("Viaje {} finalizando. Parada autodetectada: ID={}, Nombre={}",
                    id, idParadaFin, paradaMasCercana.nombre());

        } catch (ViajeInvalidoException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al validar parada al finalizar viaje: {}", e.getMessage(), e);
            throw new ViajeInvalidoException("Error al validar la ubicación del monopatín: " + e.getMessage());
        }

        // Finalizar cualquier pausa activa
        pausaRepo.findByIdViajeAndHoraFinIsNull(id).ifPresent(pausa -> {
            pausa.finalizarPausa();
            verificarYMarcarPausaExtendida(pausa);
            pausaRepo.save(pausa);
        });

        // Finalizar el viaje con la parada autodetectada
        viaje.finalizarViaje(idParadaFin, in.kilometrosRecorridos());
        viaje = viajeRepo.save(viaje);

        Long tiempoTotal = viaje.calcularTiempoTotal();
        Long tiempoSinPausas = viaje.calcularTiempoSinPausas();
        Long tiempoPausaNormal = viaje.calcularTiempoPausaNormal();
        Long tiempoPausaExtendida = viaje.calcularTiempoPausaExtendida();

        // Descontar kilómetros gratis si la cuenta es PREMIUM
        Double kilometrosACobrar = in.kilometrosRecorridos();
        try {
            var descontarKmRequest = new CuentaFeignClient.DescontarKilometrosRequest(in.kilometrosRecorridos());
            var resultadoKm = cuentaClient.descontarKilometros(viaje.getIdCuenta(), descontarKmRequest);

            if (resultadoKm.getBody() != null) {
                kilometrosACobrar = resultadoKm.getBody().kilometrosACobrar();
                logger.info("Kilómetros PREMIUM descontados: {}. Quedan por cobrar: {}",
                        resultadoKm.getBody().kilometrosDescontados(), kilometrosACobrar);
            }
        } catch (Exception e) {
            logger.warn("No se pudieron descontar kilómetros gratis: {}", e.getMessage());
        }

        // Crear facturación solo con los kilómetros que quedan por cobrar
        Double costoViaje = null;
        try {
            var facturacionRequest = new FacturacionFeignClient.FacturacionCreateRequest(
                    viaje.getId(),
                    viaje.getIdCuenta(),
                    tiempoTotal,
                    tiempoSinPausas,
                    tiempoPausaNormal,
                    tiempoPausaExtendida,
                    kilometrosACobrar  // Solo factura los km que no fueron cubiertos por los gratuitos
            );

            var facturacionResponse = facturacionClient.crearFacturacion(facturacionRequest);

            if (facturacionResponse.getBody() != null) {
                costoViaje = facturacionResponse.getBody().montoTotal();
                viaje.setCostoTotal(costoViaje);
                viaje = viajeRepo.save(viaje);
                logger.info("Facturación creada. Costo del viaje: ${}", costoViaje);
            }
        } catch (Exception e) {
            logger.warn("No se pudo crear facturación para el viaje {}: {}", id, e.getMessage());
        }

        // Facturación ya descuenta el saldo automáticamente

        try {
            Long tiempoPausado = tiempoPausaNormal + tiempoPausaExtendida;

            var monopatinUpdate = new MonopatinFeignClient.MonopatinUpdate(
                    "DISPONIBLE",
                    monopatin.latitud(),
                    monopatin.longitud(),
                    monopatin.kilometrosTotales() + (in.kilometrosRecorridos() != null ? in.kilometrosRecorridos() : 0),
                    monopatin.tiempoUsoTotal() + tiempoTotal,
                    monopatin.tiempoPausas() + tiempoPausado
            );
            monopatinClient.updateMonopatin(viaje.getIdMonopatin(), monopatinUpdate);
        } catch (Exception e) {
            logger.warn("No se pudo actualizar monopatín al finalizar viaje: {}", e.getMessage());
        }

        return viajeMapper.toResponse(viaje);
    }

    @Override
    @Transactional(readOnly = true)
    public ViajeDTO.Response findById(Long id) {
        Viaje viaje = viajeRepo.findById(id)
                .orElseThrow(() -> new ViajeNotFoundException("Viaje no encontrado con ID: " + id));
        return viajeMapper.toResponse(viaje);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeDTO.Response> findAll() {
        return viajeRepo.findAll().stream()
                .map(viajeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!viajeRepo.existsById(id)) {
            throw new ViajeNotFoundException("Viaje no encontrado con ID: " + id);
        }
        viajeRepo.deleteById(id);
    }

    @Override
    @Transactional
    public PausaDTO.Response pausarViaje(Long idViaje) {
        Viaje viaje = viajeRepo.findById(idViaje)
                .orElseThrow(() -> new ViajeNotFoundException("Viaje no encontrado con ID: " + idViaje));

        if (viaje.getEstado() == EstadoViaje.FINALIZADO) {
            throw new ViajeInvalidoException("No se puede pausar un viaje finalizado");
        }

        if (viaje.getEstado() == EstadoViaje.PAUSADO) {
            throw new ViajeInvalidoException("El viaje ya está pausado");
        }

        if (pausaRepo.findByIdViajeAndHoraFinIsNull(idViaje).isPresent()) {
            throw new ViajeInvalidoException("Ya existe una pausa activa para este viaje");
        }

        viaje.pausar();
        viajeRepo.save(viaje);

        Pausa pausa = new Pausa(idViaje);
        pausa = pausaRepo.save(pausa);

        return pausaMapper.toResponse(pausa);
    }

    @Override
    @Transactional
    public PausaDTO.Response reanudarViaje(Long idViaje) {
        Viaje viaje = viajeRepo.findById(idViaje)
                .orElseThrow(() -> new ViajeNotFoundException("Viaje no encontrado con ID: " + idViaje));

        if (viaje.getEstado() != EstadoViaje.PAUSADO) {
            throw new ViajeInvalidoException("El viaje no está pausado");
        }

        Pausa pausa = pausaRepo.findByIdViajeAndHoraFinIsNull(idViaje)
                .orElseThrow(() -> new PausaNotFoundException("No hay una pausa activa para el viaje con ID: " + idViaje));

        pausa.finalizarPausa();
        verificarYMarcarPausaExtendida(pausa);
        pausa = pausaRepo.save(pausa);

        viaje.reanudar();
        viajeRepo.save(viaje);

        return pausaMapper.toResponse(pausa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PausaDTO.Response> getPausasByViaje(Long idViaje) {
        if (!viajeRepo.existsById(idViaje)) {
            throw new ViajeNotFoundException("Viaje no encontrado con ID: " + idViaje);
        }

        return pausaRepo.findByIdViaje(idViaje).stream()
                .map(pausaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeDTO.Response> findByUsuario(Long idUsuario) {
        return viajeRepo.findByIdUsuario(idUsuario).stream()
                .map(viajeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeDTO.Response> findByCuenta(Long idCuenta) {
        return viajeRepo.findByIdCuenta(idCuenta).stream()
                .map(viajeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeDTO.Response> findByMonopatin(Long idMonopatin) {
        return viajeRepo.findByIdMonopatin(idMonopatin).stream()
                .map(viajeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViajeDTO.Resumen> findViajesActivos() {
        List<Viaje> viajes = viajeRepo.findByEstado(EstadoViaje.EN_CURSO);
        viajes.addAll(viajeRepo.findByEstado(EstadoViaje.PAUSADO));

        return viajes.stream()
                .map(viajeMapper::toResumen)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ViajeDTO.Response findViajeActivoByMonopatin(Long idMonopatin) {
        Viaje viaje = viajeRepo.findViajeActivoByMonopatin(idMonopatin)
                .orElseThrow(() -> new ViajeNotFoundException(
                        "No hay viaje activo para el monopatín con ID: " + idMonopatin));
        return viajeMapper.toResponse(viaje);
    }

    // ==================== REPORTES ====================

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMonopatinesConMasDeXViajes(Integer cantidadViajes, Integer anio) {
        return viajeRepo.findMonopatinesConMasDeXViajes(cantidadViajes, anio);
    }

    // ==================== REPORTES DE USUARIOS (Requerimientos e y h) ====================

    @Override
    @Transactional(readOnly = true)
    public List<ReporteUsuarioDTO.UsuarioActivo> getUsuariosMasActivos(
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta,
            String tipoCuenta) {

        List<Viaje> viajes;
        if (fechaDesde != null && fechaHasta != null) {
            viajes = viajeRepo.findByFechaHoraInicioBetween(fechaDesde, fechaHasta);
        } else {
            viajes = viajeRepo.findAll();
        }

        // Agrupar por usuario
        Map<Long, List<Viaje>> viajesPorUsuario = viajes.stream()
                .filter(v -> v.getIdUsuario() != null)
                .collect(Collectors.groupingBy(Viaje::getIdUsuario));

        List<ReporteUsuarioDTO.UsuarioActivo> reportes = new ArrayList<>();

        for (Map.Entry<Long, List<Viaje>> entry : viajesPorUsuario.entrySet()) {
            Long idUsuario = entry.getKey();
            List<Viaje> viajesUsuario = entry.getValue();

            // Si se especificó tipo de cuenta, filtrar
            if (tipoCuenta != null && !tipoCuenta.isBlank()) {
                try {
                    var cuentaResponse = cuentaClient.getCuentaById(
                            viajesUsuario.get(0).getIdCuenta()
                    );
                    if (cuentaResponse.getBody() != null) {
                        String tipoCuentaUsuario = cuentaResponse.getBody().tipoCuenta();
                        if (!tipoCuenta.equalsIgnoreCase(tipoCuentaUsuario)) {
                            continue; // Saltar este usuario
                        }
                    }
                } catch (Exception e) {
                    logger.warn("No se pudo verificar tipo de cuenta para usuario {}", idUsuario);
                    continue;
                }
            }

            int cantidadViajes = viajesUsuario.size();
            Double kilometrosTotales = viajesUsuario.stream()
                    .filter(v -> v.getKilometrosRecorridos() != null)
                    .mapToDouble(Viaje::getKilometrosRecorridos)
                    .sum();

            Long tiempoTotal = viajesUsuario.stream()
                    .filter(v -> v.getEstado() == EstadoViaje.FINALIZADO)
                    .mapToLong(Viaje::calcularTiempoTotal)
                    .sum();

            reportes.add(new ReporteUsuarioDTO.UsuarioActivo(
                    idUsuario,
                    cantidadViajes,
                    kilometrosTotales,
                    tiempoTotal
            ));
        }

        reportes.sort((r1, r2) -> Long.compare(r2.tiempoTotalMinutos(), r1.tiempoTotalMinutos()));

        return reportes;
    }

    @Override
    @Transactional(readOnly = true)
    public ReporteUsuarioDTO.UsuarioActivo getUsoDeUsuario(
            Long idUsuario,
            LocalDateTime fechaDesde,
            LocalDateTime fechaHasta) {

        List<Viaje> viajes;

        if (fechaDesde != null && fechaHasta != null) {
            viajes = viajeRepo.findByIdUsuarioAndFechaHoraInicioBetween(idUsuario, fechaDesde, fechaHasta);
        } else {
            viajes = viajeRepo.findByIdUsuario(idUsuario);
        }

        if (viajes.isEmpty()) {
            return new ReporteUsuarioDTO.UsuarioActivo(idUsuario, 0, 0.0, 0L);
        }

        int cantidadViajes = viajes.size();

        Double kilometrosTotales = viajes.stream()
                .filter(v -> v.getKilometrosRecorridos() != null)
                .mapToDouble(Viaje::getKilometrosRecorridos)
                .sum();

        Long tiempoTotal = viajes.stream()
                .filter(v -> v.getEstado() == EstadoViaje.FINALIZADO)
                .mapToLong(Viaje::calcularTiempoTotal)
                .sum();

        return new ReporteUsuarioDTO.UsuarioActivo(
                idUsuario,
                cantidadViajes,
                kilometrosTotales,
                tiempoTotal
        );
    }

    private void verificarYMarcarPausaExtendida(Pausa pausa) {
        if (pausa.getHoraFin() != null) {
            Duration duracion = Duration.between(pausa.getHoraInicio(), pausa.getHoraFin());
            if (duracion.toMinutes() > MINUTOS_PAUSA_MAXIMA) {
                pausa.setExtendida(true);
            }
        }
    }
}
