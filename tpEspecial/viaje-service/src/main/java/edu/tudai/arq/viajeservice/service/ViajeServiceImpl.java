package edu.tudai.arq.viajeservice.service;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.dto.ViajeDTO;
import edu.tudai.arq.viajeservice.entity.EstadoViaje;
import edu.tudai.arq.viajeservice.entity.Pausa;
import edu.tudai.arq.viajeservice.entity.Viaje;
import edu.tudai.arq.viajeservice.exception.PausaNotFoundException;
import edu.tudai.arq.viajeservice.exception.ViajeInvalidoException;
import edu.tudai.arq.viajeservice.exception.ViajeNotFoundException;
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

    private static final int MINUTOS_PAUSA_MAXIMA = 15;

    public ViajeServiceImpl(ViajeRepository viajeRepo, PausaRepository pausaRepo,
                            ViajeMapper viajeMapper, PausaMapper pausaMapper) {
        this.viajeRepo = viajeRepo;
        this.pausaRepo = pausaRepo;
        this.viajeMapper = viajeMapper;
        this.pausaMapper = pausaMapper;
    }

    @Override
    @Transactional
    public ViajeDTO.Response iniciarViaje(ViajeDTO.Create in) {
        // Validar que el monopatín no tenga un viaje activo
        viajeRepo.findViajeActivoByMonopatin(in.idMonopatin()).ifPresent(v -> {
            throw new ViajeInvalidoException("El monopatín con ID " + in.idMonopatin() + " ya tiene un viaje activo");
        });

        Viaje viaje = viajeMapper.toEntity(in);
        viaje = viajeRepo.save(viaje);
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

        // Verificar si hay una pausa activa y finalizarla
        pausaRepo.findByIdViajeAndHoraFinIsNull(id).ifPresent(pausa -> {
            pausa.finalizarPausa();
            verificarYMarcarPausaExtendida(pausa);
            pausaRepo.save(pausa);
        });

        viaje.finalizarViaje(in.idParadaFin(), in.kilometrosRecorridos(), in.costoTotal());
        viaje = viajeRepo.save(viaje);
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

