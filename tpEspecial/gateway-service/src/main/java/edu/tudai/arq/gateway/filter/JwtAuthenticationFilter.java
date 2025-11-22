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

    // Endpoints que NO requieren autenticación
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui.html",
            "/webjars/",
            "/api-docs"
    );

    // === SOLO ADMINISTRADOR ===
    private boolean isAdminOnly(String path, String method) {
        // Mantenimientos
        if (path.contains("/estadisticas/")) return true;
        if (path.contains("/reporte-uso") && path.startsWith("/api/v1/mantenimientos")) return true;
        if (path.startsWith("/api/v1/mantenimientos") && "DELETE".equals(method)) return true;

        // Monopatines
        if (path.contains("/reporte") && path.startsWith("/api/v1/monopatines")) return true;
        if (path.contains("/con-mas-viajes")) return true;

        // Viajes - Solo ADMIN
        if (path.equals("/api/v1/viajes/monopatines-con-mas-viajes")) return true;
        if (path.equals("/api/v1/viajes/reportes/usuarios-mas-activos")) return true;
        if (path.startsWith("/api/v1/viajes") && "DELETE".equals(method)) return true;

        // Cuentas
        if (path.contains("/anular") || path.contains("/habilitar")) return true;

        // Facturaciones
        if (path.equals("/api/v1/facturaciones") && "GET".equals(method)) return true;
        if (path.contains("/total-por-periodo")) return true;
        if (path.startsWith("/api/v1/facturaciones") && "DELETE".equals(method)) return true;

        // Tarifas (excepto /activas)
        if (path.startsWith("/api/v1/tarifas") && !path.equals("/api/v1/tarifas/activas")) return true;

        // Paradas (POST, PUT, DELETE)
        if (path.startsWith("/api/v1/paradas") && !"GET".equals(method)) return true;

        return false;
    }

    // === MANTENIMIENTO + ADMIN ===
    private boolean isMantenimientoAccess(String path, String method) {
        if (path.startsWith("/api/v1/mantenimientos")) return true;
        if (path.equals("/api/v1/tarifas/activas") && "GET".equals(method)) return true;
        if (path.startsWith("/api/v1/facturaciones")) return true;
        return false;
    }

    // === USUARIO + MANTENIMIENTO + ADMIN ===
    private boolean isUsuarioAccess(String path, String method) {
        // Monopatines (GET)
        if (path.startsWith("/api/v1/monopatines") && "GET".equals(method)) return true;

        // Viajes y pausas
        if (path.startsWith("/api/v1/viajes")) return true;
        if (path.startsWith("/api/v1/pausas")) return true;

        // Chat IA (la validación PREMIUM se hace dentro del servicio)
        if (path.startsWith("/api/v1/chat")) return true;

        // Cuentas (operaciones específicas o crear)
        if (path.startsWith("/api/v1/cuentas")) {
            if (path.matches("/api/v1/cuentas/\\d+.*")) return true;
            if ("POST".equals(method)) return true;
        }

        // Usuarios (GET específico)
        if (path.startsWith("/api/v1/usuarios") && "GET".equals(method)) {
            return path.matches("/api/v1/usuarios/\\d+.*");
        }

        // Paradas (GET)
        if (path.startsWith("/api/v1/paradas") && "GET".equals(method)) return true;

        // Tarifas activas
        if (path.equals("/api/v1/tarifas/activas") && "GET".equals(method)) return true;

        // Facturaciones
        if (path.startsWith("/api/v1/facturaciones")) return true;

        return false;
    }

    private boolean hasPermission(String path, String method, String rol) {
        // 1. ADMIN tiene acceso a todo
        if ("ADMINISTRADOR".equals(rol)) return true;

        // 2. Si es endpoint solo ADMIN, denegar
        if (isAdminOnly(path, method)) return false;

        // 3. MANTENIMIENTO tiene acceso a sus endpoints
        if ("MANTENIMIENTO".equals(rol)) {
            return isMantenimientoAccess(path, method);
        }

        // 4. USUARIO tiene acceso a sus endpoints
        if ("USUARIO".equals(rol)) {
            return isUsuarioAccess(path, method);
        }

        // 5. Por defecto denegar
        return false;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        log.debug("Processing request: {} {}", request.getMethod(), path);

        if (isPublicPath(path)) {
            log.debug("Public path, skipping authentication: {}", path);
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid or expired token for path: {}", path);
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }

            String email = jwtUtil.extractEmail(token);
            Long userId = jwtUtil.extractUserId(token);
            String rol = jwtUtil.extractRol(token);

            log.debug("Token validated successfully. User: {} (ID: {}), Role: {}", email, userId, rol);

            if (!hasPermission(path, request.getMethod().name(), rol)) {
                log.warn("Access denied for user {} with role {} to path: {}", email, rol, path);
                return onError(exchange, "Access denied - Insufficient permissions", HttpStatus.FORBIDDEN);
            }

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


    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        String errorType;
        String errorMessage;

        if (status == HttpStatus.UNAUTHORIZED) {
            errorType = "No autenticado";
            errorMessage = "Debe proporcionar un token JWT válido";
        } else if (status == HttpStatus.FORBIDDEN) {
            errorType = "No autorizado";
            errorMessage = "No tiene permisos para acceder a este recurso";
        } else {
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
        return -100;
    }
}

