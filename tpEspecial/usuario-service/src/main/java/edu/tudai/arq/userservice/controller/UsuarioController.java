package edu.tudai.arq.userservice.controller;

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
@Tag(name = "Usuarios", description = "API para gestión de usuarios del sistema de monopatines")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos (ej: email duplicado)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<UsuarioDTO.Response> create(@Valid @RequestBody UsuarioDTO.Create in) {
        var out = service.create(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente por ID")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<UsuarioDTO.Response> update(@PathVariable Long id,
                                                      @Valid @RequestBody UsuarioDTO.Update in) {
        return ResponseEntity.ok(service.update(id, in));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "El usuario tiene cuentas asociadas",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<UsuarioDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de todos los usuarios",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    public ResponseEntity<List<UsuarioDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar usuario por email")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<UsuarioDTO.Response> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @PostMapping("/{idUsuario}/cuentas/{idCuenta}")
    @Operation(summary = "Asociar una cuenta a un usuario",
            description = "Crea la relación entre un usuario y una cuenta existentes")
    @ApiResponse(responseCode = "204", description = "Asociación creada exitosamente")
    @ApiResponse(responseCode = "404", description = "Usuario o cuenta no encontrados",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "La asociación ya existe",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void asociarCuenta(@PathVariable Long idUsuario, @PathVariable Long idCuenta) {
        service.asociarCuenta(idUsuario, idCuenta);
    }

    @DeleteMapping("/{idUsuario}/cuentas/{idCuenta}")
    @Operation(summary = "Desasociar una cuenta de un usuario",
            description = "Elimina la relación entre un usuario y una cuenta")
    @ApiResponse(responseCode = "204", description = "Asociación eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Asociación no encontrada",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desasociarCuenta(@PathVariable Long idUsuario, @PathVariable Long idCuenta) {
        service.desasociarCuenta(idUsuario, idCuenta);
    }
}