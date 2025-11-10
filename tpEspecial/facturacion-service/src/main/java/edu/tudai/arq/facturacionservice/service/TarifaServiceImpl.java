package edu.tudai.arq.facturacionservice.service;

import edu.tudai.arq.facturacionservice.dto.TarifaDTO;
import edu.tudai.arq.facturacionservice.entity.Tarifa;
import edu.tudai.arq.facturacionservice.entity.TipoTarifa;
import edu.tudai.arq.facturacionservice.exception.TarifaDuplicadaException;
import edu.tudai.arq.facturacionservice.exception.TarifaNotFoundException;
import edu.tudai.arq.facturacionservice.mapper.TarifaMapper;
import edu.tudai.arq.facturacionservice.repository.TarifaRepository;
import edu.tudai.arq.facturacionservice.service.interfaces.TarifaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
        TipoTarifa tipoTarifa = TipoTarifa.valueOf(in.tipoTarifa());
        Optional<Tarifa> tarifaExistente = tarifaRepo.findByTipoTarifaAndFechaVigenciaHastaIsNull(tipoTarifa);

        if (tarifaExistente.isPresent()) {
            Tarifa existente = tarifaExistente.get();
            throw new TarifaDuplicadaException(
                String.format(
                    "No se puede crear una nueva tarifa de tipo %s. Ya existe una tarifa (ID: %d) " +
                    "vigente desde %s sin fecha de finalización. Debe editar la tarifa existente " +
                    "estableciendo una fecha de fin de vigencia antes de crear una nueva.",
                    tipoTarifa,
                    existente.getId(),
                    existente.getFechaVigenciaDesde()
                )
            );
        }

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
    public List<TarifaDTO.Response> findActivas() {
        return tarifaRepo.findTarifasVigentes().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
