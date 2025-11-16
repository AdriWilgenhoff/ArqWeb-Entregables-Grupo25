package edu.tudai.arq.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

    @Schema(description = "DTO para solicitud de login")
    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Debe ser un email válido")
            @Schema(description = "Email del usuario", example = "juan.perez@email.com")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            @Schema(description = "Contraseña del usuario", example = "password123")
            String password
    ) {}

    @Schema(description = "DTO para respuesta de login exitoso")
    public record LoginResponse(
            @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            String token,

            @Schema(description = "Tipo de token", example = "Bearer")
            String type,

            @Schema(description = "ID del usuario autenticado", example = "1")
            Long userId,

            @Schema(description = "Email del usuario", example = "juan.perez@email.com")
            String email,

            @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
            String nombre,

            @Schema(description = "Rol del usuario", example = "USUARIO")
            String rol
    ) {
        public LoginResponse(String token, Long userId, String email, String nombre, String rol) {
            this(token, "Bearer", userId, email, nombre, rol);
        }
    }
}

