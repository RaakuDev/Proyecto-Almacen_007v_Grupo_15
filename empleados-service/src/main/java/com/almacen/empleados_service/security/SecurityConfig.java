package com.almacen.empleados_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Crear empleados: ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/empleados/**")
                        .hasRole("ADMIN")

                        // Ver empleados: ADMIN y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/empleados/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // Actualizar empleados: ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/empleados/**")
                        .hasRole("ADMIN")

                        // Eliminar empleados: ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/empleados/**")
                        .hasRole("ADMIN")

                        // Cualquier otra petición requiere token
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}