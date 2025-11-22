package edu.tudai.arq.userservice.service;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import edu.tudai.arq.userservice.dto.UsuarioDTO;
import edu.tudai.arq.userservice.entity.Cuenta;
import edu.tudai.arq.userservice.entity.TipoCuenta;
import edu.tudai.arq.userservice.exception.CuentaNotFoundException;
import edu.tudai.arq.userservice.mapper.CuentaMapper;
import edu.tudai.arq.userservice.mapper.UsuarioMapper;
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
    private final UsuarioMapper usuarioMapper;

    public CuentaServiceImpl(CuentaRepository repo, CuentaMapper mapper, UsuarioMapper usuarioMapper) {
        this.repo = repo;
        this.mapper = mapper;
        this.usuarioMapper = usuarioMapper;
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
    public CuentaDTO.Response descontarSaldo(Long id, CuentaDTO.DescontarSaldo in) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        c.descontarSaldo(in.monto());
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

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> getUsuariosByCuenta(Long idCuenta) {
        Cuenta c = repo.findById(idCuenta)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + idCuenta));

        return c.getUsuarios().stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ==================== MÉTODOS PREMIUM ====================

    @Override
    @Transactional
    public CuentaDTO.Response upgradeToPremium(Long id) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        if (c.isPremium()) {
            throw new IllegalStateException("La cuenta ya es PREMIUM");
        }

        if (c.getSaldo() < Cuenta.MONTO_PREMIUM_MENSUAL) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente para upgrade a premium. Requerido: " + Cuenta.MONTO_PREMIUM_MENSUAL +
                    ", Disponible: " + c.getSaldo()
            );
        }

        c.descontarSaldo(Cuenta.MONTO_PREMIUM_MENSUAL);

        c.setTipoCuenta(TipoCuenta.PREMIUM);
        c.renovarCupo();

        c = repo.save(c);
        return mapper.toResponse(c);
    }

    @Override
    @Transactional
    public void renovarCupo(Long id) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        if (!c.isPremium()) {
            throw new IllegalStateException("Solo las cuentas PREMIUM pueden renovar cupo");
        }

        if (c.getSaldo() < Cuenta.MONTO_PREMIUM_MENSUAL) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente para renovación premium. Requerido: " + Cuenta.MONTO_PREMIUM_MENSUAL +
                    ", Disponible: " + c.getSaldo()
            );
        }

        // Descontar el pago mensual
        c.descontarSaldo(Cuenta.MONTO_PREMIUM_MENSUAL);

        c.renovarCupo();
        repo.save(c);
    }

    @Override
    @Transactional
    public Double usarKilometrosGratis(Long id, Double kilometros) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        Double kmUsados = c.usarKilometrosGratis(kilometros);
        repo.save(c);
        return kmUsados;
    }

    @Override
    @Transactional
    public CuentaDTO.ResultadoDescuentoKm descontarKilometrosGratis(Long id, CuentaDTO.DescontarKilometros in) {
        Cuenta c = repo.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + id));

        Double kilometrosTotales = in.kilometros();

        Double kilometrosDescontados = c.usarKilometrosGratis(kilometrosTotales);
        Double kilometrosACobrar = kilometrosTotales - kilometrosDescontados;

        repo.save(c);

        return new CuentaDTO.ResultadoDescuentoKm(
                kilometrosDescontados,
                kilometrosACobrar
        );
    }
}