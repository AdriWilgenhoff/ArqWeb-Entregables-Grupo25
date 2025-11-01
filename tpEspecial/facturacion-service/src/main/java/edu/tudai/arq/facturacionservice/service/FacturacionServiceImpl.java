package edu.tudai.arq.facturacionservice.service;

import edu.tudai.arq.facturacionservice.dto.FacturacionDTO;
import edu.tudai.arq.facturacionservice.entity.Facturacion;
import edu.tudai.arq.facturacionservice.exception.FacturacionNotFoundException;
import edu.tudai.arq.facturacionservice.mapper.FacturacionMapper;
import edu.tudai.arq.facturacionservice.repository.FacturacionRepository;
import edu.tudai.arq.facturacionservice.service.interfaces.FacturacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturacionServiceImpl implements FacturacionService {

    private final FacturacionRepository facturacionRepo;
    private final FacturacionMapper mapper;

    public FacturacionServiceImpl(FacturacionRepository facturacionRepo, FacturacionMapper mapper) {
        this.facturacionRepo = facturacionRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public FacturacionDTO.Response create(FacturacionDTO.Create in) {
        // Validar que no exista una facturación para este viaje
        if (facturacionRepo.findByIdViaje(in.idViaje()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una facturación para el viaje con ID: " + in.idViaje());
        }

        Facturacion facturacion = mapper.toEntity(in);
        facturacion = facturacionRepo.save(facturacion);
        return mapper.toResponse(facturacion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!facturacionRepo.existsById(id)) {
            throw new FacturacionNotFoundException("Facturación no encontrada con ID: " + id);
        }
        facturacionRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FacturacionDTO.Response findById(Long id) {
        Facturacion facturacion = facturacionRepo.findById(id)
                .orElseThrow(() -> new FacturacionNotFoundException("Facturación no encontrada con ID: " + id));
        return mapper.toResponse(facturacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturacionDTO.Response> findAll() {
        return facturacionRepo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FacturacionDTO.Response findByViaje(Long idViaje) {
        Facturacion facturacion = facturacionRepo.findByIdViaje(idViaje)
                .orElseThrow(() -> new FacturacionNotFoundException(
                        "No se encontró facturación para el viaje con ID: " + idViaje));
        return mapper.toResponse(facturacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturacionDTO.Response> findByCuenta(Long idCuenta) {
        return facturacionRepo.findByIdCuenta(idCuenta).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturacionDTO.Response> findByFechaEntre(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return facturacionRepo.findByFechaBetween(fechaDesde, fechaHasta).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturacionDTO.Response> findByCuentaAndFechaEntre(
            Long idCuenta, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return facturacionRepo.findByIdCuentaAndFechaBetween(idCuenta, fechaDesde, fechaHasta).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalFacturado(LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return facturacionRepo.calcularTotalFacturado(fechaDesde, fechaHasta);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalFacturadoPorCuenta(
            Long idCuenta, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        return facturacionRepo.calcularTotalFacturadoPorCuenta(idCuenta, fechaDesde, fechaHasta);
    }
}
