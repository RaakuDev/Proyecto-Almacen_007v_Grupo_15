package com.almacen.pedidos_service.service;

import java.util.List;

import com.almacen.pedidos_service.dtos.response.ProveedorResponse;
import com.almacen.pedidos_service.webclient.ProductoClient;
import com.almacen.pedidos_service.webclient.ProveedorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.almacen.pedidos_service.dtos.response.PedidosResponse;
import com.almacen.pedidos_service.dtos.request.PedidosRequest;
import com.almacen.pedidos_service.exceptions.NotFoundException;
import com.almacen.pedidos_service.model.PedidoModel;
import com.almacen.pedidos_service.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProveedorClient proveedorClient;
    private final ProductoClient productoClient;

    public List<PedidosResponse> obtenerTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PedidosResponse obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("No existe el pedido con id: " + id));
    }

    public List<PedidosResponse> obtenerPorCliente(Long clienteId) {
        log.info("Buscando pedidos del cliente con id: {}", clienteId);

        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PedidosResponse> obtenerPorEstado(String estado) {
        log.info("Buscando pedidos con estado: {}", estado);

        return pedidoRepository.findByEstado(estado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PedidosResponse guardar(PedidosRequest request) {
        obtenerProveedorDesdeServicio(request.getProveedorId());

        request.getItems().forEach(item ->
                productoClient.obtenerProductoPorId(item.getProductoId())
        );

        PedidoModel pedido = new PedidoModel();
        pedido.setFechaPedido(request.getFechaPedido());
        pedido.setEstado(request.getEstado());
        pedido.setProveedorId(request.getProveedorId());
        pedido.setClienteId(request.getClienteId());
        pedido.setItems(request.getItems().toString());

        PedidoModel guardado = pedidoRepository.save(pedido);

        return toResponse(guardado);
    }

    public PedidosResponse actualizar(Long id, PedidosRequest request) {
        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el pedido con id: " + id));

        obtenerProveedorDesdeServicio(request.getProveedorId());

        request.getItems().forEach(item ->
                productoClient.obtenerProductoPorId(item.getProductoId())
        );

        pedido.setFechaPedido(request.getFechaPedido());
        pedido.setEstado(request.getEstado());
        pedido.setProveedorId(request.getProveedorId());
        pedido.setClienteId(request.getClienteId());
        pedido.setItems(request.getItems().toString());

        PedidoModel actualizado = pedidoRepository.save(pedido);

        return toResponse(actualizado);
    }

    public PedidosResponse cambiarEstado(Long id, String estado) {
        log.info("Cambiando estado del pedido ID: {} a {}", id, estado);

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el pedido con ID: " + id));

        pedido.setEstado(estado);

        PedidoModel actualizado = pedidoRepository.save(pedido);

        return toResponse(actualizado);
    }

    public void eliminar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new NotFoundException("No existe el pedido con id: " + id);
        }

        pedidoRepository.deleteById(id);
    }

    private PedidosResponse toResponse(PedidoModel model) {
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(model.getProveedorId());

        return PedidosResponse.builder()
                .id(model.getId())
                .fechaPedido(model.getFechaPedido())
                .estado(model.getEstado())
                .proveedorId(model.getProveedorId())
                .clienteId(model.getClienteId())
                .proveedor(proveedor)
                .build();
    }

    private ProveedorResponse obtenerProveedorDesdeServicio(Long proveedorId) {
        try {
            return proveedorClient.obtenerProveedorPorId(proveedorId);
        } catch (Exception e) {
            log.error("No existe el proveedor con id: {}", proveedorId);
            throw new NotFoundException("No existe el proveedor con id: " + proveedorId);
        }
    }
}