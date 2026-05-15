package com.almacen.ventas.Security;

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

                        // Crear ventas: ADMIN y CAJERO
                        .requestMatchers(HttpMethod.POST, "/api/v1/ventas/**")
                        .hasAnyRole("ADMIN", "CAJERO")

                        // Ver ventas: ADMIN, CAJERO y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/ventas/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // Actualizar ventas: solo ADMIN
                        .requestMatchers(HttpMethod.PUT, "/api/v1/ventas/**")
                        .hasRole("ADMIN")

                        // Eliminar ventas: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/ventas/**")
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