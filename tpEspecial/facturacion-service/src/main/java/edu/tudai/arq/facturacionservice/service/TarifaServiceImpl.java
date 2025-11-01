package edu.tudai.arq.facturacionservice.service;

import edu.tudai.arq.facturacionservice.dto.TarifaDTO;
import edu.tudai.arq.facturacionservice.entity.Tarifa;
import edu.tudai.arq.facturacionservice.entity.TipoTarifa;
import edu.tudai.arq.facturacionservice.exception.TarifaNotFoundException;
import edu.tudai.arq.facturacionservice.mapper.TarifaMapper;
import edu.tudai.arq.facturacionservice.repository.TarifaRepository;
import edu.tudai.arq.facturacionservice.service.interfaces.TarifaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarifaServiceImpl implements TarifaService {

    private final TarifaRepository tarifaRepo;
    private final TarifaMapper mapper;

    public TarifaServiceImpl(TarifaRepository tarifaRepo, TarifaMapper mapper) {
        this.tarifaRepo = tarifaRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public TarifaDTO.Response create(TarifaDTO.Create in) {
        Tarifa tarifa = mapper.toEntity(in);
        tarifa = tarifaRepo.save(tarifa);
        return mapper.toResponse(tarifa);
    }

    @Override
    @Transactional
    public TarifaDTO.Response update(Long id, TarifaDTO.Update in) {
        Tarifa tarifa = tarifaRepo.findById(id)
                .orElseThrow(() -> new TarifaNotFoundException("Tarifa no encontrada con ID: " + id));

        mapper.update(tarifa, in);
        tarifa = tarifaRepo.save(tarifa);
        return mapper.toResponse(tarifa);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!tarifaRepo.existsById(id)) {
            throw new TarifaNotFoundException("Tarifa no encontrada con ID: " + id);
        }
        tarifaRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TarifaDTO.Response findById(Long id) {
        Tarifa tarifa = tarifaRepo.findById(id)
                .orElseThrow(() -> new TarifaNotFoundException("Tarifa no encontrada con ID: " + id));
        return mapper.toResponse(tarifa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarifaDTO.Response> findAll() {
        return tarifaRepo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarifaDTO.Response> findByTipo(String tipoTarifa) {
        TipoTarifa tipo = TipoTarifa.valueOf(tipoTarifa);
        return tarifaRepo.findByTipoTarifa(tipo).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarifaDTO.Response> findActivas() {
        return tarifaRepo.findByActiva(true).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TarifaDTO.Response findTarifaVigente(String tipoTarifa, LocalDate fecha) {
        TipoTarifa tipo = TipoTarifa.valueOf(tipoTarifa);
        Tarifa tarifa = tarifaRepo.findTarifaVigente(tipo, fecha)
                .orElseThrow(() -> new TarifaNotFoundException(
                        "No hay tarifa vigente de tipo " + tipoTarifa + " para la fecha " + fecha));
        return mapper.toResponse(tarifa);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TarifaDTO.Response> findTarifasVigentesEn(LocalDate fecha) {
        return tarifaRepo.findTarifasVigentesEn(fecha).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void desactivarTarifa(Long id, LocalDate fechaFin) {
        Tarifa tarifa = tarifaRepo.findById(id)
                .orElseThrow(() -> new TarifaNotFoundException("Tarifa no encontrada con ID: " + id));

        tarifa.desactivar(fechaFin);
        tarifaRepo.save(tarifa);
    }
}

