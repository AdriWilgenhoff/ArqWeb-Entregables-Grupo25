package edu.tudai.arq.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public class AuthDTO {

    @Schema(description = "Request para login", name = "LoginRequest")
    public record LoginRequest(
            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email debe ser válido")
            String email,

            @NotBlank(message = "La contraseña es obligatoria")
            String password
    ) {}

    @Schema(description = "Request para registro", name = "RegisterRequest")
    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio")
            @Size(max = 100)
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            @Size(max = 100)
            String apellido,

            @NotBlank(message = "El email es obligatorio")
            @Email(message = "El email debe ser válido")
            String email,

            @NotBlank(message = "El número de celular es obligatorio")
            @Pattern(regexp = "^[0-9+\\-\\s()]+$")
            String numeroCelular,

            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
            String password,

            @NotBlank(message = "El ID de Mercado Pago es obligatorio")
            String idCuentaMercadoPago
    ) {}

    @Schema(description = "Response con token JWT", name = "TokenResponse")
    public record TokenResponse(
            @Schema(description = "Token de acceso JWT", example = "eyJhbGciOiJIUzI1...")
            String accessToken,

            @Schema(description = "Token de refresco", example = "eyJhbGciOiJIUzI1...")
            String refreshToken,

            @Schema(description = "Tipo de token", example = "Bearer")
            String tokenType,

            @Schema(description = "Tiempo de expiración en segundos", example = "3600")
            Long expiresIn,

            @Schema(description = "Rol del usuario", example = "CLIENTE")
            String rol,

            @Schema(description = "IDs de cuentas habilitadas asociadas al usuario", example = "[1, 2, 3]")
            List<Long> cuentasHabilitadas
    ) {}

    @Schema(description = "Response de registro", name = "RegisterResponse")
    public record RegisterResponse(
            @Schema(description = "ID del usuario creado", example = "1")
            Long userId,

            @Schema(description = "ID de la cuenta creada", example = "1")
            Long cuentaId,

            @Schema(description = "Email del usuario", example = "usuario@email.com")
            String email,

            @Schema(description = "Número identificatorio de la cuenta", example = "CTA-001")
            String numeroIdentificatorioCuenta
    ) {}

    @Schema(description = "Request para refresh token", name = "RefreshRequest")
    public record RefreshRequest(
            @NotBlank(message = "El refresh token es obligatorio")
            String refreshToken
    ) {}
}