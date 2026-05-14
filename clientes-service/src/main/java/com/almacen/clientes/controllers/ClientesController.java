package com.almacen.clientes.controllers;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.services.ClientesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClientesController {

    private final ClientesService clientesService;

    // 🔹 GET /clientes
    @GetMapping
    public ResponseEntity<List<ClientesResponse>> obtenerTodos() {
        return ResponseEntity.ok(clientesService.obtenerTodos());
    }

    // 🔹 GET /clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ClientesResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clientesService.obtenerPorId(id));
    }

    // 🔹 POST /clientes
    @PostMapping
    public ResponseEntity<ClientesResponse> crear(@Valid @RequestBody ClientesRequest request) {
        return ResponseEntity.ok(clientesService.guardar(request));
    }

    // 🔹 PUT /clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ClientesResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClientesRequest request
    ) {
        return ResponseEntity.ok(clientesService.actualizar(id, request));
    }

    // 🔹 DELETE /clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clientesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}