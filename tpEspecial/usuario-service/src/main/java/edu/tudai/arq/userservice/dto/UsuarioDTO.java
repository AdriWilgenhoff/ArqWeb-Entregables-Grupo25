package edu.tudai.arq.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class UsuarioDTO {

    /** DTO para crear Usuario */
    @Schema(description = "DTO de entrada para crear un nuevo Usuario", name = "UsuarioCreate")
    public record Create(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
            String apellido,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email debe ser válido")
            @Size(max = 150, message = "El email no puede exceder los 150 caracteres")
            String email,

            @NotBlank(message = "El número de celular es obligatorio")
            @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "El número de celular debe ser válido")
            @Size(max = 20, message = "El número no puede exceder los 20 caracteres")
            String numeroCelular,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
            String password,

            @NotNull(message = "El rol es obligatorio")
            @Pattern(regexp = "CLIENTE|ENCARGADO_MANTENIMIENTO|ADMINISTRADOR",
                    message = "El rol debe ser CLIENTE, ENCARGADO_MANTENIMIENTO o ADMINISTRADOR")
            String rol
    ) {}

    /** DTO para actualizar */
    @Schema(description = "DTO de entrada para actualizar un Usuario", name = "UsuarioUpdate")
    public record Update(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100)
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(max = 100)
            String apellido,

            @NotBlank(message = "El número de celular es obligatorio")
            @Pattern(regexp = "^[0-9+\\-\\s()]+$")
            @Size(max = 20)
            String numeroCelular,

            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres si se proporciona")
            String password,

            @Pattern(regexp = "CLIENTE|ENCARGADO_MANTENIMIENTO|ADMINISTRADOR")
            String rol
    ) {}

    /** DTO de salida */
    @Schema(description = "DTO de respuesta para Usuarios", name = "UsuarioResponse")
    public record Response(
            @Schema(description = "ID del usuario", example = "1")
            Long id,

            @Schema(description = "Nombre del usuario", example = "Juan")
            String nombre,

            @Schema(description = "Apellido del usuario", example = "Pérez")
            String apellido,

            @Schema(description = "Email del usuario", example = "juan.perez@email.com")
            String email,

            @Schema(description = "Número de celular", example = "+54 2494 123456")
            String numeroCelular,

            @Schema(description = "Fecha de alta", example = "2024-01-15")
            String fechaAlta,

            @Schema(description = "Rol del usuario", example = "CLIENTE")
            String rol
    ) {}
}