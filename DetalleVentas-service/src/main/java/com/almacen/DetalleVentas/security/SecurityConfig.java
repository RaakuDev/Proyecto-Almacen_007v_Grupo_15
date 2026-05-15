package com.almacen.DetalleVentas.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

                        // Ver detalles: ADMIN, CAJERO y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/detalles/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // Crear detalle: ADMIN y CAJERO
                        // porque al generar una venta también puede necesitar registrar detalle
                        .requestMatchers(HttpMethod.POST, "/api/v1/detalles/**")
                        .hasAnyRole("ADMIN", "CAJERO")

                        // Modificar detalle: solo ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/detalles/**")
                        .hasRole("ADMIN")

                        // Eliminar detalle: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/detalles/**")
                        .hasRole("ADMIN")

                        // Todo lo demás requiere token
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}