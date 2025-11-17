package edu.tudai.arq.userservice.service;

import edu.tudai.arq.userservice.dto.AuthDTO;
import edu.tudai.arq.userservice.entity.Rol;
import edu.tudai.arq.userservice.entity.Usuario;
import edu.tudai.arq.userservice.repository.UsuarioRepository;
import edu.tudai.arq.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthDTO.LoginResponse register(AuthDTO.RegisterRequest request) {
        // Verificar que no exista un usuario con ese email
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un usuario con el email: " + request.email());
        }

        // Crear el nuevo usuario con la contraseña hasheada
        Usuario nuevoUsuario = new Usuario(
                request.nombre().trim(),
                request.apellido().trim(),
                request.email().trim().toLowerCase(),
                request.numeroCelular().trim(),
                passwordEncoder.encode(request.password()), // Hashear la contraseña
                Rol.USUARIO // Por defecto se registran como USUARIO
        );

        nuevoUsuario = usuarioRepository.save(nuevoUsuario);

        // Generar token JWT
        String token = jwtUtil.generateToken(
                nuevoUsuario.getId(),
                nuevoUsuario.getEmail(),
                nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido(),
                nuevoUsuario.getRol().name()
        );

        return new AuthDTO.LoginResponse(
                token,
                nuevoUsuario.getId(),
                nuevoUsuario.getEmail(),
                nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido(),
                nuevoUsuario.getRol().name()
        );
    }

    @Transactional(readOnly = true)
    public AuthDTO.LoginResponse login(AuthDTO.LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtUtil.generateToken(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getRol().name()
        );

        return new AuthDTO.LoginResponse(
                token,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getRol().name()
        );
    }
}

