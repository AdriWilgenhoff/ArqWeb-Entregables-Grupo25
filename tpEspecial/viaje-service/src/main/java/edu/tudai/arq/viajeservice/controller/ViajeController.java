package edu.tudai.arq.viajeservice.controller;

import edu.tudai.arq.viajeservice.dto.PausaDTO;
import edu.tudai.arq.viajeservice.dto.ReporteUsuarioDTO;
import edu.tudai.arq.viajeservice.dto.ViajeDTO;
import edu.tudai.arq.viajeservice.exception.ApiError;
import edu.tudai.arq.viajeservice.service.interfaces.ViajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/viajes")
@Validated
@Tag(name = "Viajes", description = "API para gestión de viajes de monopatines")
public class ViajeController {

    private final ViajeService service;

    public ViajeController(ViajeService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
            summary = "Iniciar un nuevo viaje",
            description = "Accesible para USUARIO y ADMINISTRADOR. Crea un nuevo viaje y lo asocia a un usuario, cuenta y monopatín. Acceso: http://localhost:8080/api/v1/viajes"
    )
    @ApiResponse(responseCode = "201", description = "Viaje iniciado exitosamente",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o monopatín ya en uso",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<ViajeDTO.Response> iniciarViaje(@Valid @RequestBody ViajeDTO.Create in) {
        var out = service.iniciarViaje(in);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    @PutMapping("/{id}/finalizar")
    @Operation(
            summary = "Finalizar un viaje existente",
            description = "Accesible para usuarios autenticados. Finaliza un viaje en curso, registrando la parada final, kilómetros y costo. Acceso: http://localhost:8080/api/v1/viajes/{id}/finalizar"
    )
    @ApiResponse(responseCode = "200", description = "Viaje finalizado exitosamente",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "El viaje ya está finalizado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<ViajeDTO.Response> finalizarViaje(
            @PathVariable Long id,
            @Valid @RequestBody ViajeDTO.Finalizacion in) {
        return ResponseEntity.ok(service.finalizarViaje(id, in));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar viaje por ID",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/viajes/{id}"
    )
    @ApiResponse(responseCode = "200", description = "Viaje encontrado",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<ViajeDTO.Response> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(
            summary = "Obtener todos los viajes",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/viajes"
    )
    @ApiResponse(responseCode = "200", description = "Lista de todos los viajes",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<List<ViajeDTO.Response>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un viaje por ID",
            description = "Requiere rol ADMINISTRADOR. Acceso: http://localhost:8080/api/v1/viajes/{id}"
    )
    @ApiResponse(responseCode = "204", description = "Viaje eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/pausar")
    @Operation(
            summary = "Pausar un viaje en curso",
            description = "Accesible para usuarios autenticados. Pausa un viaje que está en curso. El monopatín se puede apagar pero sigue asignado. Acceso: http://localhost:8080/api/v1/viajes/{id}/pausar"
    )
    @ApiResponse(responseCode = "200", description = "Viaje pausado exitosamente",
            content = @Content(schema = @Schema(implementation = PausaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "El viaje no puede ser pausado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<PausaDTO.Response> pausarViaje(@PathVariable Long id) {
        return ResponseEntity.ok(service.pausarViaje(id));
    }

    @PostMapping("/{id}/reanudar")
    @Operation(
            summary = "Reanudar un viaje pausado",
            description = "Accesible para usuarios autenticados. Reanuda un viaje que estaba pausado. Acceso: http://localhost:8080/api/v1/viajes/{id}/reanudar"
    )
    @ApiResponse(responseCode = "200", description = "Viaje reanudado exitosamente",
            content = @Content(schema = @Schema(implementation = PausaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "400", description = "El viaje no está pausado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<PausaDTO.Response> reanudarViaje(@PathVariable Long id) {
        return ResponseEntity.ok(service.reanudarViaje(id));
    }

    @GetMapping("/{id}/pausas")
    @Operation(
            summary = "Obtener todas las pausas de un viaje",
            description = "Accesible para usuarios autenticados. Acceso: http://localhost:8080/api/v1/viajes/{id}/pausas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de pausas del viaje",
            content = @Content(schema = @Schema(implementation = PausaDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "Viaje no encontrado",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<List<PausaDTO.Response>> getPausasByViaje(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPausasByViaje(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(
            summary = "Buscar viajes por usuario",
            description = "Accesible para usuarios autenticados. Obtiene todos los viajes realizados por un usuario específico. Acceso: http://localhost:8080/api/v1/viajes/usuario/{idUsuario}"
    )
    @ApiResponse(responseCode = "200", description = "Lista de viajes del usuario",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<List<ViajeDTO.Response>> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(service.findByUsuario(idUsuario));
    }

    @GetMapping("/cuenta/{idCuenta}")
    @Operation(
            summary = "Buscar viajes por cuenta",
            description = "Accesible para usuarios autenticados. Obtiene todos los viajes asociados a una cuenta específica. Acceso: http://localhost:8080/api/v1/viajes/cuenta/{idCuenta}"
    )
    @ApiResponse(responseCode = "200", description = "Lista de viajes de la cuenta",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<List<ViajeDTO.Response>> findByCuenta(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(service.findByCuenta(idCuenta));
    }

    @GetMapping("/monopatin/{idMonopatin}")
    @Operation(
            summary = "Buscar viajes por monopatín",
            description = "Accesible para usuarios autenticados. Obtiene todos los viajes realizados con un monopatín específico. Acceso: http://localhost:8080/api/v1/viajes/monopatin/{idMonopatin}"
    )
    @ApiResponse(responseCode = "200", description = "Lista de viajes del monopatín",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<List<ViajeDTO.Response>> findByMonopatin(@PathVariable Long idMonopatin) {
        return ResponseEntity.ok(service.findByMonopatin(idMonopatin));
    }

    @GetMapping("/activos")
    @Operation(
            summary = "Obtener todos los viajes activos",
            description = "Accesible para usuarios autenticados. Obtiene todos los viajes que están EN_CURSO o PAUSADOS. Acceso: http://localhost:8080/api/v1/viajes/activos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de viajes activos",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Resumen.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<List<ViajeDTO.Resumen>> findViajesActivos() {
        return ResponseEntity.ok(service.findViajesActivos());
    }

    @GetMapping("/monopatin/{idMonopatin}/activo")
    @Operation(
            summary = "Obtener viaje activo de un monopatín",
            description = "Accesible para usuarios autenticados. Obtiene el viaje activo (EN_CURSO o PAUSADO) de un monopatín específico. Acceso: http://localhost:8080/api/v1/viajes/monopatin/{idMonopatin}/activo"
    )
    @ApiResponse(responseCode = "200", description = "Viaje activo encontrado",
            content = @Content(schema = @Schema(implementation = ViajeDTO.Response.class)))
    @ApiResponse(responseCode = "404", description = "No hay viaje activo para este monopatín",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<ViajeDTO.Response> findViajeActivoByMonopatin(@PathVariable Long idMonopatin) {
        return ResponseEntity.ok(service.findViajeActivoByMonopatin(idMonopatin));
    }

    // ==================== REPORTES ====================

    @GetMapping("/monopatines-con-mas-viajes")
    @Operation(
            summary = "Obtener IDs de monopatines con más de X viajes (Reporte interno para monopatin-service)",
            description = "Requiere rol ADMINISTRADOR. Devuelve una lista de IDs de monopatines que tienen más de X viajes en un año determinado. Acceso: http://localhost:8080/api/v1/viajes/monopatines-con-mas-viajes"
    )
    @ApiResponse(responseCode = "200", description = "Lista de IDs de monopatines")
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<List<Long>> getMonopatinesConMasDeXViajes(
            @RequestParam Integer cantidadViajes,
            @RequestParam Integer anio) {
        return ResponseEntity.ok(service.getMonopatinesConMasDeXViajes(cantidadViajes, anio));
    }

    @GetMapping("/reportes/usuarios-mas-activos")
    @Operation(
            summary = "Usuarios que más utilizan monopatines (Requerimiento e)",
            description = "Requiere rol ADMINISTRADOR. Como administrador quiero ver los usuarios que más utilizan los monopatines, " +
                    "filtrado por período y por tipo de usuario (BASICA o PREMIUM). " +
                    "Retorna la lista ordenada por tiempo total de uso (descendente). Acceso: http://localhost:8080/api/v1/viajes/reportes/usuarios-mas-activos"
    )
    @ApiResponse(responseCode = "200", description = "Lista de usuarios más activos ordenados por tiempo de uso",
            content = @Content(schema = @Schema(implementation = ReporteUsuarioDTO.UsuarioActivo.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol ADMINISTRADOR")
    public ResponseEntity<List<ReporteUsuarioDTO.UsuarioActivo>> getUsuariosMasActivos(
            @RequestParam(required = false)
            @Parameter(description = "Fecha desde (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-01T00:00:00")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaDesde,

            @RequestParam(required = false)
            @Parameter(description = "Fecha hasta (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2024-12-31T23:59:59")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaHasta,

            @RequestParam(required = false)
            @Parameter(description = "Tipo de cuenta (BASICA o PREMIUM). Si no se especifica, muestra todos", example = "PREMIUM")
            String tipoCuenta
    ) {
        return ResponseEntity.ok(service.getUsuariosMasActivos(fechaDesde, fechaHasta, tipoCuenta));
    }

    @GetMapping("/reportes/mi-uso")
    @Operation(
            summary = "Consultar uso de monopatines de un usuario (Requerimiento h)",
            description = "Accesible para usuarios autenticados. Como usuario quiero saber cuánto he usado los monopatines en un período. " +
                    "Muestra cantidad de viajes, kilómetros totales y tiempo total de uso. Acceso: http://localhost:8080/api/v1/viajes/reportes/mi-uso"
    )
    @ApiResponse(responseCode = "200", description = "Reporte de uso del usuario",
            content = @Content(schema = @Schema(implementation = ReporteUsuarioDTO.UsuarioActivo.class)))
    @ApiResponse(responseCode = "401", description = "No autenticado - Token requerido")
    @ApiResponse(responseCode = "403", description = "No autorizado - Requiere rol USUARIO o ADMINISTRADOR")
    public ResponseEntity<ReporteUsuarioDTO.UsuarioActivo> getMiUso(
            @RequestParam
            @Parameter(description = "ID del usuario", example = "1", required = true)
            Long idUsuario,

            @RequestParam(required = false)
            @Parameter(description = "Fecha desde (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2024-01-01T00:00:00")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaDesde,

            @RequestParam(required = false)
            @Parameter(description = "Fecha hasta (formato: yyyy-MM-dd'T'HH:mm:ss)", example = "2024-12-31T23:59:59")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaHasta
    ) {
        return ResponseEntity.ok(service.getUsoDeUsuario(idUsuario, fechaDesde, fechaHasta));
    }
}

