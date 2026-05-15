package com.almacen.clientes.security;

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

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth

                        // Ver clientes: ADMIN, CAJERO y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/clientes/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // Crear clientes: ADMIN y CAJERO
                        .requestMatchers(HttpMethod.POST, "/api/v1/clientes/**")
                        .hasAnyRole("ADMIN", "CAJERO")

                        // Actualizar clientes: ADMIN y SUPERVISOR
                        .requestMatchers(HttpMethod.PUT, "/api/v1/clientes/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // Eliminar clientes: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/clientes/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}