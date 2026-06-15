package com.almacen.pedidos_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

import com.almacen.pedidos_service.dtos.response.ClienteResponse;
import com.almacen.pedidos_service.dtos.response.ProductoResponse;
import com.almacen.pedidos_service.dtos.response.ProveedorResponse;
import com.almacen.pedidos_service.webclient.ClienteClient;
import com.almacen.pedidos_service.webclient.ProductoClient;
import com.almacen.pedidos_service.webclient.ProveedorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ClienteClient clienteClient;

    @Transactional(readOnly = true)
    public List<PedidosResponse> obtenerTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidosResponse obtenerPorId(Long id) {
        return pedidoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("No existe el pedido con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<PedidosResponse> obtenerPorCliente(Long clienteId) {
        log.info("Buscando pedidos del cliente con id: {}", clienteId);
        return pedidoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidosResponse> obtenerPorEstado(String estado) {
        log.info("Buscando pedidos con estado: {}", estado);
        return pedidoRepository.findByEstado(estado)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PedidosResponse guardar(PedidosRequest request) {
        // 1. Validaciones externas ANTES de abrir transacción de escritura
        obtenerClienteDesdeServicio(request.getClienteId());
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());

        // Verificación rápida de productos
        request.getItems().forEach(item -> productoClient.obtenerProductoPorId(item.getProductoId()));

        PedidoModel pedido = new PedidoModel();
        pedido.setFechaPedido(request.getFechaPedido());
        pedido.setEstado(request.getEstado());
        pedido.setProveedorId(request.getProveedorId());
        pedido.setClienteId(request.getClienteId());
        pedido.setProductosIds(request.getItems().stream()
                .map(item -> item.getProductoId() + ":" + item.getCantidad())
                .collect(Collectors.joining(",")));

        PedidoModel guardado = pedidoRepository.save(pedido);
        log.info("Pedido guardado con id: {}", guardado.getId());
        return toResponse(guardado, proveedor);
    }

    public PedidosResponse actualizar(Long id, PedidosRequest request) {
        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el pedido con id: " + id));

        obtenerClienteDesdeServicio(request.getClienteId());
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());

        request.getItems().forEach(item -> productoClient.obtenerProductoPorId(item.getProductoId()));

        pedido.setFechaPedido(request.getFechaPedido());
        pedido.setEstado(request.getEstado());
        pedido.setProveedorId(request.getProveedorId());
        pedido.setClienteId(request.getClienteId());
        pedido.setProductosIds(request.getItems().stream()
                .map(item -> item.getProductoId() + ":" + item.getCantidad())
                .collect(Collectors.joining(",")));

        PedidoModel actualizado = pedidoRepository.save(pedido);
        return toResponse(actualizado, proveedor);
    }

    public PedidosResponse cambiarEstado(Long id, String estado) {
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

    private PedidosResponse toResponse(PedidoModel model, ProveedorResponse proveedor) {
        // Manejo de nulos en productosIds para evitar NullPointerException
        List<ProductoResponse> productos = (model.getProductosIds() == null || model.getProductosIds().isEmpty())
                ? Collections.emptyList()
                : Arrays.stream(model.getProductosIds().split(","))
                .map(entry -> {
                    try {
                        String[] partes = entry.trim().split(":");
                        Long productoId = Long.parseLong(partes[0]);
                        Integer cantidad = Integer.parseInt(partes[1]);
                        ProductoResponse producto = productoClient.obtenerProductoPorId(productoId);
                        if (producto != null) {
                            producto.setCantidad(cantidad);
                        }
                        return producto;
                    } catch (Exception e) {
                        log.error("Error procesando producto en entry: {}", entry);
                        return null;
                    }
                })
                .filter(p -> p != null)
                .toList();

        ClienteResponse cliente = null;
        if (model.getClienteId() != null) {
            try {
                cliente = clienteClient.obtenerClientePorId(model.getClienteId());
            } catch (Exception e) {
                log.error("Error al obtener cliente con id: {}", model.getClienteId(), e);
            }
        }

        return PedidosResponse.builder()
                .id(model.getId())
                .fechaPedido(model.getFechaPedido())
                .estado(model.getEstado())
                .proveedorId(model.getProveedorId())
                .clienteId(model.getClienteId())
                .cliente(cliente)
                .proveedor(proveedor)
                .productos(productos)
                .build();
    }

    private PedidosResponse toResponse(PedidoModel model) {
        ProveedorResponse proveedor = null;
        if (model.getProveedorId() != null) {
            try {
                proveedor = proveedorClient.obtenerProveedorPorId(model.getProveedorId());
            } catch (Exception e) {
                log.error("Error al obtener proveedor para el response");
            }
        }
        return toResponse(model, proveedor);
    }

    private ProveedorResponse obtenerProveedorDesdeServicio(Long proveedorId) {
        try {
            return proveedorClient.obtenerProveedorPorId(proveedorId);
        } catch (Exception e) {
            throw new NotFoundException("No existe o no se pudo validar el proveedor con id: " + proveedorId);
        }
    }
    private void obtenerClienteDesdeServicio(Long clienteId) {
        try {
            clienteClient.obtenerClientePorId(clienteId);
        } catch (Exception e) {
            throw new NotFoundException("No existe o no se pudo validar el cliente con id: " + clienteId);
        }
    }}