package cl.duoc.rutalimpia.rutas_service.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalKeyFilter extends OncePerRequestFilter {

    private final String internalKey;

    public InternalKeyFilter(
            @Value("${app.internal.key}") String internalKey
    ) {
        this.internalKey = internalKey;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String ruta = request.getServletPath();

        return !ruta.startsWith("/api/v1/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String claveRecibida = request.getHeader("X-Internal-Key");

        if (claveRecibida == null ||
                !MessageDigest.isEqual(
                        claveRecibida.getBytes(StandardCharsets.UTF_8),
                        internalKey.getBytes(StandardCharsets.UTF_8)
                )) {

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(
                    "{\"status\":403,"
                    + "\"error\":\"Forbidden\","
                    + "\"mensaje\":\"Clave interna inválida o ausente\"}"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}