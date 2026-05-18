package com.almacen.proveedores_service.security;

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

                        // Crear proveedores: ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/v1/proveedores/**")
                        .hasRole("ADMIN")

                        // Ver proveedores: ADMIN, CAJERO y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/proveedores/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // Actualizar proveedores: ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/proveedores/**")
                        .hasRole("ADMIN")

                        // Eliminar proveedores: ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/proveedores/**")
                        .hasRole("ADMIN")

                        // Cualquier otra petición requiere token válido
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}