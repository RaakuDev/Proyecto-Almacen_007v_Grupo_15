package com.almacen.DetalleVentas.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.almacen.DetalleVentas.dtos.request.LoginRequest;
import com.almacen.DetalleVentas.dtos.response.LoginResponse;
import com.almacen.DetalleVentas.security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        if (!request.getUsername().equals("admin") || !request.getPassword().equals("1234")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generarToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}