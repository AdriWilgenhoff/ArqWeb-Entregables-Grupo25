package edu.tudai.arq.userservice.controller;

import edu.tudai.arq.userservice.dto.AuthDTO;
import edu.tudai.arq.userservice.exception.ApiError;
import edu.tudai.arq.userservice.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Autenticación", description = "API para autenticación y autorización de usuarios")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión",
            description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = AuthDTO.TokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Usuario con cuenta deshabilitada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<AuthDTO.TokenResponse> login(@Valid @RequestBody AuthDTO.LoginRequest in) {
        return ResponseEntity.ok(service.login(in));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario con una cuenta inicial asociada")
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthDTO.RegisterResponse.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<AuthDTO.RegisterResponse> register(@Valid @RequestBody AuthDTO.RegisterRequest in) {
        var out = service.register(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token de acceso",
            description = "Genera un nuevo token de acceso usando el refresh token")
    @ApiResponse(responseCode = "200", description = "Token renovado exitosamente",
            content = @Content(schema = @Schema(implementation = AuthDTO.TokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "Refresh token inválido o expirado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<AuthDTO.TokenResponse> refresh(@Valid @RequestBody AuthDTO.RefreshRequest in) {
        return ResponseEntity.ok(service.refresh(in));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión",
            description = "Invalida el token actual del usuario (si se implementa blacklist)")
    @ApiResponse(responseCode = "204", description = "Logout exitoso")
    @ApiResponse(responseCode = "401", description = "Token inválido",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String token) {
        service.logout(token.replace("Bearer ", ""));
    }
}