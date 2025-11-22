package edu.tudai.arq.userservice.controller;

import edu.tudai.arq.userservice.dto.CuentaDTO;
import edu.tudai.arq.userservice.dto.UsuarioDTO;
import edu.tudai.arq.userservice.exception.ApiError;
import edu.tudai.arq.userservice.service.interfaces.UsuarioService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Validated
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema de monopatines.")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Requiere rol ADMINISTRADOR. Crea un usuario con rol específico. Para registro de usuarios comunes usar /auth/register. Acceso: http://localhost:8080/api/v1/usuarios"
    )
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: email duplicado)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<UsuarioDTO.Response> create(@Valid @RequestBody UsuarioDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un usuario existente por ID",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/usuarios/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR o ser el propio usuario")
    public ResponseEntity<UsuarioDTO.Response> update(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un usuario por ID",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/usuarios/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "El usuario tiene cuentas asociadas",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/usuarios/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<UsuarioDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/usuarios"
    )
    @ApiResponse(responseCode = "200", description = "Lista de todos los usuarios",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<List<UsuarioDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Buscar usuario por email",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/usuarios/email/{email}"
    )
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<UsuarioDTO.Response> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @GetMapping("/{idUsuario}/cuentas")
    @Operation(
            summary = "Obtener todas las cuentas de un usuario",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/usuarios/{idUsuario}/cuentas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de cuentas del usuario",
            content = @Content(schema = @Schema(implementation = CuentaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    public ResponseEntity<List<CuentaDTO.Response>> getCuentasByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.getCuentasByUsuario(idUsuario));
    }

    @PostMapping("/{idUsuario}/cuentas/{idCuenta}")
    @Operation(
            summary = "Asociar una cuenta a un usuario",
            description = "Requiere rol ADMINISTRADOR. Crea la relación entre un usuario y una cuenta existentes. Acceso: http://localhost:8080/api/v1/usuarios/{idUsuario}/cuentas/{idCuenta}"
    )
    @ApiResponse(responseCode = "204", description = "Asociación creada exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario o cuenta no encontrados",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "La asociación ya existe",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR o ser el propio usuario")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void asociarCuenta(@PathVariable Long idUsuario, @PathVariable Long idCuenta) {
        service.asociarCuenta(idUsuario, idCuenta);
    }

    @DeleteMapping("/{idUsuario}/cuentas/{idCuenta}")
    @Operation(
            summary = "Desasociar una cuenta de un usuario",
            description = "Requiere rol ADMINISTRADOR. Elimina la relación entre un usuario y una cuenta. Acceso: http://localhost:8080/api/v1/usuarios/{idUsuario}/cuentas/{idCuenta}"
    )
    @ApiResponse(responseCode = "204", description = "Asociación eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Asociación no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR o ser el propio usuario")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desasociarCuenta(@PathVariable Long idUsuario, @PathVariable Long idCuenta) {
        service.desasociarCuenta(idUsuario, idCuenta);
    }
}