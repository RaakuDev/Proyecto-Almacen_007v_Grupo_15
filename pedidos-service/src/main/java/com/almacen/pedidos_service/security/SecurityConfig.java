package com.almacen.pedidos_service.security;

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

                        // Crear pedidos: ADMIN y CAJERO
                        .requestMatchers(HttpMethod.POST, "/api/v1/pedidos/**")
                        .hasAnyRole("ADMIN", "CAJERO")

                        // Ver pedidos: ADMIN, CAJERO y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // Actualizar pedidos: ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/pedidos/**")
                        .hasRole("ADMIN")

                        // Eliminar pedidos: ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/pedidos/**")
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