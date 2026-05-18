package com.almacen.pedidos_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.almacen.pedidos_service.dtos.response.PedidosResponse;
import com.almacen.pedidos_service.dtos.request.PedidosRequest;
import com.almacen.pedidos_service.service.PedidoService;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidosResponse>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidosResponse> obtenerPorId(@PathVariable @NonNull Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidosResponse>> obtenerPorCliente(
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(pedidoService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidosResponse>> obtenerPorEstado(
            @PathVariable String estado
    ) {
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}/estado/{estado}")
    public ResponseEntity<PedidosResponse> cambiarEstado(
            @PathVariable Long id,
            @PathVariable String estado
    ) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    @PostMapping
    public ResponseEntity<PedidosResponse> guardar(@Valid @RequestBody PedidosRequest request) {
        PedidosResponse nuevo = pedidoService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidosResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PedidosRequest request
    ) {
        return ResponseEntity.ok(pedidoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.ok("Pedido eliminado");
    }
}