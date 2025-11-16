package edu.tudai.arq.userservice.service;

import edu.tudai.arq.userservice.dto.AuthDTO;
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

