package com.almacen.categorias_service.security;

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

                        // Ver categorías: ADMIN, CAJERO y SUPERVISOR
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // Crear categorías: ADMIN y SUPERVISOR
                        .requestMatchers(HttpMethod.POST, "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // Actualizar categorías: ADMIN y SUPERVISOR
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // Eliminar categorías: solo ADMIN
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}