package cl.duoc.rutalimpia.rutas_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final InternalKeyFilter internalKeyFilter;

    public SecurityConfig(
            JwtAuthFilter jwtAuthFilter,
            InternalKeyFilter internalKeyFilter
    ) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.internalKeyFilter = internalKeyFilter;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(
        JwtAuthFilter jwtAuthFilter
    ) {
    FilterRegistrationBean<JwtAuthFilter> registration =
            new FilterRegistrationBean<>(jwtAuthFilter);

    registration.setEnabled(false);

    return registration;
    }

    @Bean
    public FilterRegistrationBean<InternalKeyFilter> internalFilterRegistration(
        InternalKeyFilter internalKeyFilter
    ) {
    FilterRegistrationBean<InternalKeyFilter> registration =
            new FilterRegistrationBean<>(internalKeyFilter);

    registration.setEnabled(false);

    return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            // Desactivar CSRF para nuestra API REST
            .csrf(csrf -> csrf.disable())

            // No utilizar sesiones HTTP
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Configurar permisos
            .authorizeHttpRequests(auth -> auth

                // Endpoints internos: acceso controlado por InternalKeyFilter
                .requestMatchers("/api/v1/internal/**")
                .permitAll()

                // Consultar hoja del conductor
                .requestMatchers("/api/v1/rutas/mi-hoja")
                .hasRole("CONDUCTOR")

                // Registrar resultado de una parada
                .requestMatchers("/api/v1/rutas/paradas/*/resultado")
                .hasRole("CONDUCTOR")

                // Listar hojas de ruta
                .requestMatchers("/api/v1/rutas")
                .hasRole("ADMIN")

                // Cualquier otra petición requiere autenticación
                .anyRequest()
                .authenticated()
            )

            // Respuestas de autenticación y autorización
            .exceptionHandling(ex -> ex

                .authenticationEntryPoint(
                    (request, response, exception) ->
                        response.sendError(
                            HttpStatus.UNAUTHORIZED.value(),
                            "No autenticado"
                        )
                )

                .accessDeniedHandler(
                    (request, response, exception) ->
                        response.sendError(
                            HttpStatus.FORBIDDEN.value(),
                            "Acceso denegado"
                        )
                )
            )

            // Incorporar filtros personalizados
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            )

            .addFilterBefore(
                internalKeyFilter,
                JwtAuthFilter.class
            );

        return http.build();
    }
}
