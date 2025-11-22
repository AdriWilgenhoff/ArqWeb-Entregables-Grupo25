package edu.tudai.arq.userservice.mapper;

import edu.tudai.arq.userservice.dto.UsuarioDTO;
import edu.tudai.arq.userservice.entity.Rol;
import edu.tudai.arq.userservice.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    private static String normalizeString(String s) {
        return s == null ? null : s.trim();
    }

    public Usuario toEntity(UsuarioDTO.Create in) {
        Usuario u = new Usuario(
                normalizeString(in.nombre()),
                normalizeString(in.apellido()),
                normalizeString(in.email()).toLowerCase(),
                normalizeString(in.numeroCelular()),
                passwordEncoder.encode(in.password()), // Hashear la contraseña
                Rol.valueOf(in.rol())
        );
        return u;
    }

    public void update(Usuario u, UsuarioDTO.Update in) {
        u.setNombre(normalizeString(in.nombre()));
        u.setApellido(normalizeString(in.apellido()));
        u.setNumeroCelular(normalizeString(in.numeroCelular()));

        if (in.password() != null && !in.password().isBlank()) {
            u.setPasswordHash(passwordEncoder.encode(in.password().trim())); // Hashear la contraseña
        }

        if (in.rol() != null) {
            u.setRol(Rol.valueOf(in.rol()));
        }
    }

    public UsuarioDTO.Response toResponse(Usuario u) {
        return new UsuarioDTO.Response(
                u.getId(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                u.getNumeroCelular(),
                u.getFechaAlta().toString(),
                u.getRol().name()
        );
    }
}
