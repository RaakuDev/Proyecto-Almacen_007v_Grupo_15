package com.almacen.clientes.controllers;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.dtos.response.PedidoResponse;
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

    @GetMapping
    public ResponseEntity<List<ClientesResponse>> obtenerTodos() {
        return ResponseEntity.ok(clientesService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientesResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clientesService.obtenerPorId(id));
    }

    @GetMapping("/{clienteId}/pedidos")
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosDelCliente(
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(clientesService.obtenerPedidosDelCliente(clienteId));
    }

    @PostMapping
    public ResponseEntity<ClientesResponse> crear(@Valid @RequestBody ClientesRequest request) {
        return ResponseEntity.ok(clientesService.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientesResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClientesRequest request
    ) {
        return ResponseEntity.ok(clientesService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clientesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}