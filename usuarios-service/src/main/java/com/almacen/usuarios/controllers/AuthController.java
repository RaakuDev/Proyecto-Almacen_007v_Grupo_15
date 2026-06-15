package com.almacen.usuarios.controllers;

import com.almacen.usuarios.dtos.request.LoginRequest;
import com.almacen.usuarios.dtos.response.LoginResponse;
import com.almacen.usuarios.models.UsuarioModel;
import com.almacen.usuarios.repositories.UsuarioRepository;
import com.almacen.usuarios.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Operaciones de acceso y generación de token JWT")
public class AuthController {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Operation(summary = "Login de usuario", description = "Valida credenciales y retorna el token JWT para acceder al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario inactivo"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el request")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        UsuarioModel usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!usuario.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!usuario.isEstado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generarToken(
                usuario.getUsername(),
                usuario.getRol().name()
        );

        return ResponseEntity.ok(
                new LoginResponse(token, "Bearer")
        );
    }
}