package edu.tudai.arq.userservice.security;

import edu.tudai.arq.userservice.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long accessTokenExpiration;   // segundos
    private final long refreshTokenExpiration;  // segundos

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration:900}") long accessTokenExpiration,
                   @Value("${jwt.refresh-token-expiration:604800}") long refreshTokenExpiration) {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8); // HS256 => >= 32 bytes
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("jwt.secret must be at least 32 bytes for HS256 (>=32 bytes).");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(Usuario usuario, List<Long> cuentasHabilitadas) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId());
        claims.put("email", usuario.getEmail());
        claims.put("rol", usuario.getRol().name());
        claims.put("cuentasHabilitadas", cuentasHabilitadas);
        return createToken(claims, usuario.getEmail(), accessTokenExpiration);
    }

    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, userId.toString(), refreshTokenExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationSeconds) {
        Date now = new Date();
        Date expiry = Date.from(Instant.ofEpochMilli(now.getTime()).plusSeconds(expirationSeconds));
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256) // Opción A: HS256
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims) && !"refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(claims) && "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserIdFromRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        // (opcional) validar que realmente sea un refresh
        Object type = claims.get("type");
        if (type == null || !"refresh".equals(type.toString())) {
            throw new IllegalArgumentException("El token no es un refresh token");
        }
        return claims.get("userId", Long.class);
    }

    public Long getUserIdFromToken(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    public String getEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String getRolFromToken(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<Long> getCuentasHabilitadasFromToken(String token) {
        return (List<Long>) extractAllClaims(token).get("cuentasHabilitadas");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
