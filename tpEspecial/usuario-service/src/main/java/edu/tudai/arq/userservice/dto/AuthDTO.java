package edu.tudai.arq.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDTO {

    @Schema(description = "DTO para solicitud de registro")
    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
            @Schema(description = "Nombre del usuario", example = "Juan")
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
            @Schema(description = "Apellido del usuario", example = "Pérez")
            String apellido,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "Debe ser un email válido")
            @Schema(description = "Email del usuario", example = "juan.perez@email.com")
            String email,

            @NotBlank(message = "El número de celular es obligatorio")
            @Pattern(regexp = "^[0-9]{10,15}$", message = "El número de celular debe tener entre 10 y 15 dígitos")
            @Schema(description = "Número de celular del usuario", example = "2214567890")
            String numeroCelular,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
            @Schema(description = "Contraseña del usuario", example = "password123")
            String password
    ) {}

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

