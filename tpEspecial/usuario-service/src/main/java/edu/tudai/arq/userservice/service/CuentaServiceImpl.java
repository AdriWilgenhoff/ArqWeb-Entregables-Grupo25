package edu.tudai.arq.userservice.service;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import edu.tudai.arq.userservice.entity.Cuenta;
import edu.tudai.arq.userservice.exception.CuentaNotFoundException;
import edu.tudai.arq.userservice.exception.SaldoInsuficienteException;
import edu.tudai.arq.userservice.mapper.CuentaMapper;
import edu.tudai.arq.userservice.repository.CuentaRepository;
import edu.tudai.arq.userservice.service.interfaces.CuentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository repo;
    private final CuentaMapper mapper;

    public CuentaServiceImpl(CuentaRepository repo, CuentaMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CuentaDTO.Response create(CuentaDTO.Create in) {
        if (repo.findByNumeroIdentificatorio(in.numeroIdentificatorio()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con el número: " + in.numeroIdentificatorio());
        }

        Cuenta c = mapper.toEntity(in);
        c = repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public CuentaDTO.Response update(Long id, CuentaDTO.Update in) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        mapper.update(c, in);
        c = repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new CuentaNotFoundException("Cuenta no encontrada con ID: " + id);
        }
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO.Response findById(Long id) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));
        return mapper.toResponse(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO.Response> findAll() {
        return repo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO.Response findByNumeroIdentificatorio(String numeroIdentificatorio) {
        Cuenta c = repo.findByNumeroIdentificatorio(numeroIdentificatorio)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con número: " + numeroIdentificatorio));
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public CuentaDTO.Response cargarSaldo(Long id, CuentaDTO.CargarSaldo in) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        c.cargarSaldo(in.monto());
        c = repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public void anularCuenta(Long id) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));
        c.setHabilitada(false);
        repo.save(c);
    }

    @Override
    @Transactional
    public void habilitarCuenta(Long id) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));
        c.setHabilitada(true);
        repo.save(c);
    }
}