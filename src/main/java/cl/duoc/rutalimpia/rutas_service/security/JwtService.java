package cl.duoc.rutalimpia.rutas_service.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(
            @Value("${app.jwt.secret}") String jwtSecret
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Validar y obtener la información del token
    public Claims obtenerClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Obtener el ID del usuario autenticado
    public Long obtenerUsuarioId(String token) {

        Claims claims = obtenerClaims(token);

        return Long.valueOf(claims.getSubject());
    }

    // Obtener el rol del usuario
    public String obtenerRol(String token) {

        Claims claims = obtenerClaims(token);

        return claims.get("rol", String.class);
    }

    // Verificar si el token es válido
    public boolean validarToken(String token) {

        try {
            obtenerClaims(token);
            return true;

        } catch (Exception ex) {
            return false;
        }
    }
}
