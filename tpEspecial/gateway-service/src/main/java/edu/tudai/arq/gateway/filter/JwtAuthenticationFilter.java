package edu.tudai.arq.gateway.filter;

import edu.tudai.arq.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    // Rutas públicas que no requieren autenticación
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui.html",
            "/webjars/",
            "/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Processing request: {} {}", request.getMethod(), path);

        // Permitir acceso a rutas públicas
        if (isPublicPath(path)) {
            log.debug("Public path, skipping authentication: {}", path);
            return chain.filter(exchange);
        }

        // Verificar si existe el header Authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
        }

        // Extraer el token
        String token = authHeader.substring(7);

        try {
            // Validar el token
            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid or expired token for path: {}", path);
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            // Extraer información del token
            String email = jwtUtil.extractEmail(token);
            Long userId = jwtUtil.extractUserId(token);
            String rol = jwtUtil.extractRol(token);

            log.debug("Token validated successfully. User: {} (ID: {}), Role: {}", email, userId, rol);

            // Validar permisos según el rol y la ruta
            if (!hasPermission(path, request.getMethod().name(), rol)) {
                log.warn("Access denied for user {} with role {} to path: {}", email, rol, path);
                return onError(exchange, "Access denied - Insufficient permissions", HttpStatus.FORBIDDEN);
            }

            // Agregar información del usuario al request para que los microservicios la puedan usar
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", rol)
                    .build();

            log.debug("Request authorized, forwarding to microservice");
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            return onError(exchange, "Token processing error", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean hasPermission(String path, String method, String rol) {
        // ADMINISTRADOR tiene acceso completo
        if ("ADMINISTRADOR".equals(rol)) {
            return true;
        }

        // Reglas específicas para MANTENIMIENTO
        if ("MANTENIMIENTO".equals(rol)) {
            // Puede acceder a endpoints de mantenimiento (excepto DELETE, estadísticas y reportes)
            if (path.startsWith("/api/v1/mantenimientos")) {
                // Estadísticas solo para ADMINISTRADOR
                if (path.contains("/estadisticas/")) {
                    return false;
                }
                // Reporte de uso solo para ADMINISTRADOR
                if (path.contains("/reporte-uso")) {
                    return false;
                }
                // DELETE solo para ADMINISTRADOR
                if ("DELETE".equals(method)) {
                    return false;
                }
                // GET, POST, PUT permitidos (operaciones de mantenimiento)
                return true;
            }
            // NO puede ver reportes de monopatines (removido - solo ADMIN)
            return false;
        }

        // Reglas específicas para USUARIO
        if ("USUARIO".equals(rol)) {
            // Los usuarios pueden:
            // - Ver monopatines (GET)
            if (path.startsWith("/api/v1/monopatines") && "GET".equals(method)) {
                // Bloquear reportes y estadísticas (solo ADMIN)
                if (path.contains("/reporte") || path.contains("/con-mas-viajes")) {
                    return false;
                }
                // Permitir: GET /monopatines, GET /monopatines/{id}, GET /monopatines/cercanos
                return true;
            }
            // - Gestionar viajes y pausas
            if (path.startsWith("/api/v1/viajes") || path.startsWith("/api/v1/pausas")) {
                return true;
            }
            // - Ver y gestionar sus cuentas
            if (path.startsWith("/api/v1/cuentas")) {
                // Los usuarios pueden gestionar sus cuentas pero no ver todas
                if (path.matches("/api/v1/cuentas/\\d+.*")) { // Operaciones sobre cuenta específica
                    return true;
                }
                if ("POST".equals(method)) { // Crear cuenta
                    return true;
                }
                return false;
            }
            // - Ver sus usuarios
            if (path.startsWith("/api/v1/usuarios") && "GET".equals(method)) {
                if (path.matches("/api/v1/usuarios/\\d+.*")) { // Solo su usuario específico
                    return true;
                }
                return false;
            }
            // - Ver paradas
            if (path.startsWith("/api/v1/paradas") && "GET".equals(method)) {
                return true;
            }
            // - Ver facturaciones propias
            if (path.startsWith("/api/v1/facturaciones") && "GET".equals(method)) {
                if (path.matches("/api/v1/facturaciones/cuenta/\\d+.*")) {
                    return true;
                }
                return false;
            }
            return false;
        }

        // Por defecto denegar acceso
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String errorType;
        String errorMessage;

        if (status == HttpStatus.UNAUTHORIZED) {
            // 401 - No autenticado
            errorType = "No autenticado";
            errorMessage = "Debe proporcionar un token JWT válido";
        } else if (status == HttpStatus.FORBIDDEN) {
            // 403 - No autorizado
            errorType = "No autorizado";
            errorMessage = "No tiene permisos para acceder a este recurso";
        } else {
            // Otros errores
            errorType = status.getReasonPhrase();
            errorMessage = message;
        }

        String errorJson = String.format(
            "{\"error\":\"%s\",\"message\":\"%s\"}",
            errorType,
            errorMessage
        );

        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(errorJson.getBytes()))
        );
    }

    @Override
    public int getOrder() {
        return -100; // Alta prioridad para ejecutarse antes que otros filtros
    }
}

