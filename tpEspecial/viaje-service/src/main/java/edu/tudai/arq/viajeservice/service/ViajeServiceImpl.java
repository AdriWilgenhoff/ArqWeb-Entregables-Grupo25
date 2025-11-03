package edu.tudai.arq.viajeservice.service;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.dto.ViajeDTO;
import edu.tudai.arq.viajeservice.entity.EstadoViaje;
import edu.tudai.arq.viajeservice.entity.Pausa;
import edu.tudai.arq.viajeservice.entity.Viaje;
import edu.tudai.arq.viajeservice.exception.PausaNotFoundException;
import edu.tudai.arq.viajeservice.exception.ViajeInvalidoException;
import edu.tudai.arq.viajeservice.exception.ViajeNotFoundException;
import edu.tudai.arq.viajeservice.feignclient.MonopatinFeignClient;
import edu.tudai.arq.viajeservice.feignclient.ParadaFeignClient;
import edu.tudai.arq.viajeservice.feignclient.UsuarioFeignClient;
import edu.tudai.arq.viajeservice.mapper.PausaMapper;
import edu.tudai.arq.viajeservice.mapper.ViajeMapper;
import edu.tudai.arq.viajeservice.repository.PausaRepository;
import edu.tudai.arq.viajeservice.repository.ViajeRepository;
import edu.tudai.arq.viajeservice.service.interfaces.ViajeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ViajeServiceImpl implements ViajeService {

    private final ViajeRepository viajeRepo;
    private final PausaRepository pausaRepo;
    private final ViajeMapper viajeMapper;
    private final PausaMapper pausaMapper;
    private final ParadaFeignClient paradaClient;
    private final MonopatinFeignClient monopatinClient;
    private final UsuarioFeignClient usuarioClient;

    private static final int MINUTOS_PAUSA_MAXIMA = 15;
    private static final double RADIO_VALIDACION_PARADA_KM = 0.05; // 50 metros

    public ViajeServiceImpl(ViajeRepository viajeRepo, PausaRepository pausaRepo,
                            ViajeMapper viajeMapper, PausaMapper pausaMapper,
                            ParadaFeignClient paradaClient, MonopatinFeignClient monopatinClient,
                            UsuarioFeignClient usuarioClient) {
        this.viajeRepo = viajeRepo;
        this.pausaRepo = pausaRepo;
        this.viajeMapper = viajeMapper;
        this.pausaMapper = pausaMapper;
        this.paradaClient = paradaClient;
        this.monopatinClient = monopatinClient;
        this.usuarioClient = usuarioClient;
    }

    @Override
    @Transactional
    public ViajeDTO.Response iniciarViaje(ViajeDTO.Create in) {
        // 1. Validar que la cuenta esté habilitada y tenga saldo
        try {
            var cuentaResponse = usuarioClient.getCuentaById(in.idCuenta());
            if (cuentaResponse.getBody() == null) {
                throw new ViajeInvalidoException("Cuenta no encontrada con ID: " + in.idCuenta());
            }
            var cuenta = cuentaResponse.getBody();
            if (!cuenta.habilitada()) {
                throw new ViajeInvalidoException("La cuenta con ID " + in.idCuenta() + " está anulada/deshabilitada");
            }
            if (cuenta.saldo() == null || cuenta.saldo() <= 0) {
                throw new ViajeInvalidoException("La cuenta no tiene saldo suficiente para iniciar un viaje");
            }
        } catch (Exception e) {
            throw new ViajeInvalidoException("Error al validar cuenta: " + e.getMessage());
        }

        // 2. Validar que el monopatín esté disponible
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

        // 3. Validar que el monopatín no tenga un viaje activo
        viajeRepo.findViajeActivoByMonopatin(in.idMonopatin()).ifPresent(v -> {
            throw new ViajeInvalidoException("El monopatín con ID " + in.idMonopatin() + " ya tiene un viaje activo");
        });

        // 4. Crear el viaje
        Viaje viaje = viajeMapper.toEntity(in);
        viaje = viajeRepo.save(viaje);

        // 5. Actualizar estado del monopatín a EN_USO
        try {
            var monopatinUpdate = new MonopatinFeignClient.MonopatinUpdate(
                    "EN_USO", null, null, null, null
            );
            monopatinClient.updateMonopatin(in.idMonopatin(), monopatinUpdate);
        } catch (Exception e) {
            // Log warning pero no fallar el viaje
            System.err.println("Advertencia: No se pudo actualizar estado del monopatín: " + e.getMessage());
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

        // 1. Obtener ubicación actual del monopatín
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

        // 2. Validar que esté cerca de una parada (según enunciado: CONDICIÓN BÁSICA)
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

            // Usar la parada más cercana si no se especificó una
            if (in.idParadaFin() == null) {
                var paradaMasCercana = paradasResponse.getBody().get(0);
                System.out.println("Parada automáticamente seleccionada: " + paradaMasCercana.nombre());
            }

        } catch (ViajeInvalidoException e) {
            throw e; // Re-lanzar excepciones de validación
        } catch (Exception e) {
            // Si falla la comunicación con parada-service, log pero permitir finalizar
            System.err.println("Advertencia: No se pudo validar parada: " + e.getMessage());
        }

        // 3. Verificar si hay una pausa activa y finalizarla
        pausaRepo.findByIdViajeAndHoraFinIsNull(id).ifPresent(pausa -> {
            pausa.finalizarPausa();
            verificarYMarcarPausaExtendida(pausa);
            pausaRepo.save(pausa);
        });

        // 4. Finalizar el viaje
        viaje.finalizarViaje(in.idParadaFin(), in.kilometrosRecorridos(), in.costoTotal());
        viaje = viajeRepo.save(viaje);

        // 5. Actualizar estado y ubicación del monopatín a DISPONIBLE
        try {
            var monopatinUpdate = new MonopatinFeignClient.MonopatinUpdate(
                    "DISPONIBLE",
                    monopatin.latitud(),
                    monopatin.longitud(),
                    monopatin.kilometrosTotales() + (in.kilometrosRecorridos() != null ? in.kilometrosRecorridos() : 0),
                    monopatin.tiempoUsoTotal() + viaje.calcularTiempoTotal()
            );
            monopatinClient.updateMonopatin(viaje.getIdMonopatin(), monopatinUpdate);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo actualizar monopatín: " + e.getMessage());
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

        // Verificar que no haya una pausa activa
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

    // Método auxiliar para verificar si una pausa excedió los 15 minutos
    private void verificarYMarcarPausaExtendida(Pausa pausa) {
        if (pausa.getHoraFin() != null) {
            Duration duracion = Duration.between(pausa.getHoraInicio(), pausa.getHoraFin());
            if (duracion.toMinutes() > MINUTOS_PAUSA_MAXIMA) {
                pausa.setExtendida(true);
            }
        }
    }
}
