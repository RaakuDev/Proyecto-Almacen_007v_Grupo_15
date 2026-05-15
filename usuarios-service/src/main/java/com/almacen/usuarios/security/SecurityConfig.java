package com.almacen.usuarios.security;

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

                        // Login libre
                        .requestMatchers("/auth/**").permitAll()

                        // USUARIOS: solo ADMIN
                        .requestMatchers("/api/v1/usuarios/**")
                        .hasRole("ADMIN")

                        // VENTAS: ADMIN y CAJERO pueden crear y ver
                        .requestMatchers(HttpMethod.POST, "/api/v1/ventas/**")
                        .hasAnyRole("ADMIN", "CAJERO")

                        .requestMatchers(HttpMethod.GET, "/api/v1/ventas/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // DETALLE VENTAS: CAJERO solo ve, ADMIN modifica
                        .requestMatchers(HttpMethod.GET, "/api/v1/detalles/**")
                        .hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        .requestMatchers(HttpMethod.POST, "/api/v1/detalles/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/detalles/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/detalles/**")
                        .hasRole("ADMIN")

                        // PRODUCTOS: ADMIN y SUPERVISOR
                        .requestMatchers("/api/v1/productos/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // INVENTARIO: ADMIN y SUPERVISOR
                        .requestMatchers("/api/v1/inventario/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // REPORTES: ADMIN y SUPERVISOR
                        .requestMatchers("/api/v1/reportes/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        // Todo lo demás requiere token
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}


//ADMIN → todo
//SUPERVISOR → ver ventas, detalles, productos, inventario y reportes
//CAJERO → crear/ver ventas y ver detalles