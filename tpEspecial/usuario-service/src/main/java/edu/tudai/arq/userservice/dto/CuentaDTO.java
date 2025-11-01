package edu.tudai.arq.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public class CuentaDTO {

    /** DTO para crear Cuenta */
    @Schema(description = "DTO de entrada para crear una nueva Cuenta", name = "CuentaCreate")
    public record Create(
            @NotBlank(message = "El número identificatorio es obligatorio")
            @Size(max = 50, message = "El número identificatorio no puede exceder los 50 caracteres")
            String numeroIdentificatorio,

            @NotBlank(message = "El ID de cuenta Mercado Pago es obligatorio")
            @Size(max = 100, message = "El ID de MP no puede exceder los 100 caracteres")
            String idCuentaMercadoPago,

            @Min(value = 0, message = "El saldo inicial no puede ser negativo")
            Double saldoInicial
    ) {}

    /** DTO para actualizar */
    @Schema(description = "DTO de entrada para actualizar una Cuenta", name = "CuentaUpdate")
    public record Update(
            @NotBlank(message = "El ID de cuenta Mercado Pago es obligatorio")
            @Size(max = 100)
            String idCuentaMercadoPago,

            Boolean habilitada
    ) {}

    /** DTO para cargar saldo */
    @Schema(description = "DTO para cargar saldo en una Cuenta", name = "CuentaCargarSaldo")
    public record CargarSaldo(
            @NotNull(message = "El monto es obligatorio")
            @Positive(message = "El monto debe ser positivo")
            Double monto
    ) {}

    /** DTO de salida */
    @Schema(description = "DTO de respuesta para Cuentas", name = "CuentaResponse")
    public record Response(
            @Schema(description = "ID de la cuenta", example = "1")
            Long id,

            @Schema(description = "Número identificatorio único", example = "CTA-001234")
            String numeroIdentificatorio,

            @Schema(description = "Fecha de alta", example = "2024-01-15")
            String fechaAlta,

            @Schema(description = "Saldo actual de la cuenta", example = "1500.00")
            Double saldo,

            @Schema(description = "Estado de la cuenta", example = "true")
            Boolean habilitada,

            @Schema(description = "ID de cuenta de Mercado Pago", example = "MP-123456789")
            String idCuentaMercadoPago
    ) {}
}