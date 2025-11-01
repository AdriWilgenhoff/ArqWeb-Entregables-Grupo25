package edu.tudai.arq.userservice.service;

import edu.tudai.arq.userservice.dto.AuthDTO;
import edu.tudai.arq.userservice.entity.Cuenta;
import edu.tudai.arq.userservice.entity.Rol;
import edu.tudai.arq.userservice.entity.Usuario;
import edu.tudai.arq.userservice.entity.UsuarioCuenta;
import edu.tudai.arq.userservice.exception.UnauthorizedException;
import edu.tudai.arq.userservice.repository.CuentaRepository;
import edu.tudai.arq.userservice.repository.UsuarioRepository;
import edu.tudai.arq.userservice.repository.UsuarioCuentaRepository;
import edu.tudai.arq.userservice.security.JwtUtil;
import edu.tudai.arq.userservice.service.interfaces.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepo;
    private final CuentaRepository cuentaRepo;
    private final UsuarioCuentaRepository usuarioCuentaRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UsuarioRepository usuarioRepo,
                           CuentaRepository cuentaRepo,
                           UsuarioCuentaRepository usuarioCuentaRepo,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.usuarioRepo = usuarioRepo;
        this.cuentaRepo = cuentaRepo;
        this.usuarioCuentaRepo = usuarioCuentaRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        // Buscar usuario por email
        Usuario usuario = usuarioRepo.findByEmail(request.email())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Obtener cuentas habilitadas
        List<Long> cuentasHabilitadas = usuario.getCuentasAsociadas().stream()
                .map(uc -> uc.getCuenta())
                .filter(Cuenta::getHabilitada)
                .map(Cuenta::getId)
                .collect(Collectors.toList());

        // Verificar que tenga al menos una cuenta habilitada
        if (cuentasHabilitadas.isEmpty() && usuario.getRol() == Rol.USUARIO) {
            throw new UnauthorizedException("Usuario sin cuentas habilitadas");
        }

        // Generar tokens
        String accessToken = jwtUtil.generateAccessToken(usuario, cuentasHabilitadas);
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getId());

        return new AuthDTO.TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtUtil.getAccessTokenExpiration(),
                usuario.getRol().name(),
                cuentasHabilitadas
        );
    }

    @Override
    @Transactional
    public AuthDTO.RegisterResponse register(AuthDTO.RegisterRequest request) {
        // Verificar que el email no exista
        if (usuarioRepo.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario(
                request.nombre(),
                request.apellido(),
                request.email().toLowerCase(),
                request.numeroCelular(),
                passwordEncoder.encode(request.password()),
                Rol.USUARIO  // Por defecto, los nuevos registros son CLIENTE
        );
        usuario = usuarioRepo.save(usuario);

        // Crear cuenta
        String numeroIdentificatorio = "CTA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Cuenta cuenta = new Cuenta(numeroIdentificatorio, request.idCuentaMercadoPago());
        cuenta = cuentaRepo.save(cuenta);

        // Asociar usuario con cuenta
        UsuarioCuenta usuarioCuenta = new UsuarioCuenta(usuario, cuenta);
        usuarioCuentaRepo.save(usuarioCuenta);

        return new AuthDTO.RegisterResponse(
                usuario.getId(),
                cuenta.getId(),
                usuario.getEmail(),
                cuenta.getNumeroIdentificatorio()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthDTO.TokenResponse refresh(AuthDTO.RefreshRequest request) {
        // Validar refresh token
        if (!jwtUtil.validateRefreshToken(request.refreshToken())) {
            throw new UnauthorizedException("Refresh token inválido o expirado");
        }

        // Extraer userId del refresh token
        Long userId = jwtUtil.getUserIdFromRefreshToken(request.refreshToken());

        // Buscar usuario
        Usuario usuario = usuarioRepo.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        // Obtener cuentas habilitadas actualizadas
        List<Long> cuentasHabilitadas = usuario.getCuentasAsociadas().stream()
                .map(uc -> uc.getCuenta())
                .filter(Cuenta::getHabilitada)
                .map(Cuenta::getId)
                .collect(Collectors.toList());

        // Generar nuevo access token
        String accessToken = jwtUtil.generateAccessToken(usuario, cuentasHabilitadas);

        return new AuthDTO.TokenResponse(
                accessToken,
                request.refreshToken(), // Devolver el mismo refresh token
                "Bearer",
                jwtUtil.getAccessTokenExpiration(),
                usuario.getRol().name(),
                cuentasHabilitadas
        );
    }

    @Override
    public void logout(String token) {
        // Aquí puedes implementar blacklist de tokens si lo necesitas
        // Por ahora, solo validamos que el token sea válido
        if (!jwtUtil.validateAccessToken(token)) {
            throw new UnauthorizedException("Token inválido");
        }

        // En una implementación completa, agregarías el token a una blacklist
        // blacklistService.addToken(token);
    }
}