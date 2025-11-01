package edu.tudai.arq.userservice.service;

import edu.tudai.arq.userservice.dto.UsuarioDTO;
import edu.tudai.arq.userservice.entity.Cuenta;
import edu.tudai.arq.userservice.entity.Usuario;
import edu.tudai.arq.userservice.entity.UsuarioCuenta;
import edu.tudai.arq.userservice.exception.UsuarioNotFoundException;
import edu.tudai.arq.userservice.exception.CuentaNotFoundException;
import edu.tudai.arq.userservice.mapper.UsuarioMapper;
import edu.tudai.arq.userservice.repository.CuentaRepository;
import edu.tudai.arq.userservice.repository.UsuarioRepository;
import edu.tudai.arq.userservice.repository.UsuarioCuentaRepository;
import edu.tudai.arq.userservice.service.interfaces.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final CuentaRepository cuentaRepo;
    private final UsuarioCuentaRepository usuarioCuentaRepo;
    private final UsuarioMapper mapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepo, CuentaRepository cuentaRepo,
                              UsuarioCuentaRepository usuarioCuentaRepo, UsuarioMapper mapper) {
        this.usuarioRepo = usuarioRepo;
        this.cuentaRepo = cuentaRepo;
        this.usuarioCuentaRepo = usuarioCuentaRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UsuarioDTO.Response create(UsuarioDTO.Create in) {
        if (usuarioRepo.findByEmail(in.email()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + in.email());
        }

        Usuario u = mapper.toEntity(in);
        u = usuarioRepo.save(u);
        return mapper.toResponse(u);
    }

    @Override
    @Transactional
    public UsuarioDTO.Response update(Long id, UsuarioDTO.Update in) {
        Usuario u = usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));

        mapper.update(u, in);
        u = usuarioRepo.save(u);
        return mapper.toResponse(u);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!usuarioRepo.existsById(id)) {
            throw new UsuarioNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO.Response findById(Long id) {
        Usuario u = usuarioRepo.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + id));
        return mapper.toResponse(u);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> findAll() {
        return usuarioRepo.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO.Response findByEmail(String email) {
        Usuario u = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con email: " + email));
        return mapper.toResponse(u);
    }

    @Override
    @Transactional
    public void asociarCuenta(Long idUsuario, Long idCuenta) {
        Usuario u = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no encontrado con ID: " + idUsuario));

        Cuenta c = cuentaRepo.findById(idCuenta)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada con ID: " + idCuenta));

        UsuarioCuenta uc = new UsuarioCuenta(u, c);
        usuarioCuentaRepo.save(uc);
    }

    @Override
    @Transactional
    public void desasociarCuenta(Long idUsuario, Long idCuenta) {
        usuarioCuentaRepo.deleteByUsuarioIdAndCuentaId(idUsuario, idCuenta);
    }
}