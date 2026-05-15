package com.almacen.usuarios.controllers;

import com.almacen.usuarios.dtos.request.LoginRequest;
import com.almacen.usuarios.dtos.response.LoginResponse;
import com.almacen.usuarios.security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        // Usuario quemado para pruebas iniciales
        if (
                !request.getUsername().equals("admin")
                || !request.getPassword().equals("1234")
        ) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        // Rol quemado para que el token tenga permisos
        String rol = "ADMIN";

        String token = jwtService.generarToken(
                request.getUsername(),
                rol
        );

        return ResponseEntity.ok(
                new LoginResponse(token, "Bearer")
        );
    }
}