package com.almacen.pedidos_service.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

import com.almacen.pedidos_service.dtos.request.PedidoItem;
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

        log.info("Obteniendo todos los pedidos");

        List<PedidoModel> pedidos = pedidoRepository.findAll();

        if (pedidos.isEmpty()) {
            log.error("No existen pedidos registrados");
            throw new NotFoundException("No existen pedidos registrados");
        }

        return pedidos.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidosResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id del pedido no puede ser nulo");
            throw new NotFoundException("El id del pedido no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del pedido debe ser mayor a cero");
            throw new NotFoundException("El id del pedido debe ser mayor a cero");
        }

        log.info("Buscando pedido con id: {}", id);

        return pedidoRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("No existe el pedido con id: {}", id);
                    return new NotFoundException("No existe el pedido con id: " + id);
                });
    }

    @Transactional(readOnly = true)
    public List<PedidosResponse> obtenerPorCliente(Long clienteId) {

        if (clienteId == null) {
            log.error("El id del cliente no puede ser nulo");
            throw new NotFoundException("El id del cliente no puede ser nulo");
        }

        if (clienteId <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        log.info("Buscando pedidos del cliente con id: {}", clienteId);

        obtenerClienteDesdeServicio(clienteId);

        List<PedidoModel> pedidos = pedidoRepository.findByClienteId(clienteId);

        if (pedidos.isEmpty()) {
            log.error("No existen pedidos para el cliente con id: {}", clienteId);
            throw new NotFoundException("No existen pedidos para el cliente con id: " + clienteId);
        }

        return pedidos.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PedidosResponse> obtenerPorEstado(String estado) {

        if (estado == null || estado.trim().isEmpty()) {
            log.error("El estado del pedido no puede ser nulo o vacío");
            throw new NotFoundException("El estado del pedido no puede ser nulo o vacío");
        }

        log.info("Buscando pedidos con estado: {}", estado);

        List<PedidoModel> pedidos = pedidoRepository.findByEstado(estado);

        if (pedidos.isEmpty()) {
            log.error("No existen pedidos con estado: {}", estado);
            throw new NotFoundException("No existen pedidos con estado: " + estado);
        }

        return pedidos.stream()
                .map(this::toResponse)
                .toList();
    }

    public PedidosResponse guardar(PedidosRequest request) {
        // 1. Validaciones externas ANTES de abrir transacción de escritura

        if (request == null) {
            log.error("Los datos del pedido no pueden ser nulos");
            throw new NotFoundException("Los datos del pedido no pueden ser nulos");
        }

        if (request.getFechaPedido() == null) {
            log.error("La fecha del pedido es obligatoria");
            throw new NotFoundException("La fecha del pedido es obligatoria");
        }

        if (request.getEstado() == null || request.getEstado().trim().isEmpty()) {
            log.error("El estado del pedido es obligatorio");
            throw new NotFoundException("El estado del pedido es obligatorio");
        }

        if (request.getProveedorId() == null || request.getProveedorId() <= 0) {
            log.error("El id del proveedor es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del proveedor es obligatorio y debe ser mayor a cero");
        }

        if (request.getClienteId() == null || request.getClienteId() <= 0) {
            log.error("El id del cliente es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del cliente es obligatorio y debe ser mayor a cero");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.error("El pedido debe tener al menos un producto");
            throw new NotFoundException("El pedido debe tener al menos un producto");
        }

        for (PedidoItem item : request.getItems()) {

            if (item == null) {
                log.error("El item del pedido no puede ser nulo");
                throw new NotFoundException("El item del pedido no puede ser nulo");
            }

            if (item.getProductoId() == null || item.getProductoId() <= 0) {
                log.error("El id del producto es obligatorio y debe ser mayor a cero");
                throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
            }

            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                log.error("La cantidad del producto debe ser mayor a cero");
                throw new NotFoundException("La cantidad del producto debe ser mayor a cero");
            }
        }

        obtenerClienteDesdeServicio(request.getClienteId());

        obtenerClienteDesdeServicio(request.getClienteId());
  main
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());

        for (PedidoItem item : request.getItems()) {
            ProductoResponse producto = obtenerProductoDesdeServicio(item.getProductoId());

            if (producto == null) {
                log.error("No existe el producto con id: {}", item.getProductoId());
                throw new NotFoundException("No existe el producto con id: " + item.getProductoId());
            }
        }

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

        if (id == null) {
            log.error("El id del pedido no puede ser nulo");
            throw new NotFoundException("El id del pedido no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del pedido debe ser mayor a cero");
            throw new NotFoundException("El id del pedido debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos del pedido no pueden ser nulos");
            throw new NotFoundException("Los datos del pedido no pueden ser nulos");
        }

        if (request.getFechaPedido() == null) {
            log.error("La fecha del pedido es obligatoria");
            throw new NotFoundException("La fecha del pedido es obligatoria");
        }

        if (request.getEstado() == null || request.getEstado().trim().isEmpty()) {
            log.error("El estado del pedido es obligatorio");
            throw new NotFoundException("El estado del pedido es obligatorio");
        }

        if (request.getProveedorId() == null || request.getProveedorId() <= 0) {
            log.error("El id del proveedor es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del proveedor es obligatorio y debe ser mayor a cero");
        }

        if (request.getClienteId() == null || request.getClienteId() <= 0) {
            log.error("El id del cliente es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del cliente es obligatorio y debe ser mayor a cero");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            log.error("El pedido debe tener al menos un producto");
            throw new NotFoundException("El pedido debe tener al menos un producto");
        }

        for (PedidoItem item : request.getItems()) {

            if (item == null) {
                log.error("El item del pedido no puede ser nulo");
                throw new NotFoundException("El item del pedido no puede ser nulo");
            }

            if (item.getProductoId() == null || item.getProductoId() <= 0) {
                log.error("El id del producto es obligatorio y debe ser mayor a cero");
                throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
            }

            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                log.error("La cantidad del producto debe ser mayor a cero");
                throw new NotFoundException("La cantidad del producto debe ser mayor a cero");
            }
        }

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el pedido con id: {}", id);
                    return new NotFoundException("No existe el pedido con id: " + id);
                });

        obtenerClienteDesdeServicio(request.getClienteId());
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());

        for (PedidoItem item : request.getItems()) {
            ProductoResponse producto = obtenerProductoDesdeServicio(item.getProductoId());

            if (producto == null) {
                log.error("No existe el producto con id: {}", item.getProductoId());
                throw new NotFoundException("No existe el producto con id: " + item.getProductoId());
            }
        }

        pedido.setFechaPedido(request.getFechaPedido());
        pedido.setEstado(request.getEstado());
        pedido.setProveedorId(request.getProveedorId());
        pedido.setClienteId(request.getClienteId());
        pedido.setProductosIds(request.getItems().stream()
                .map(item -> item.getProductoId() + ":" + item.getCantidad())
                .collect(Collectors.joining(",")));

        PedidoModel actualizado = pedidoRepository.save(pedido);

        log.info("Pedido actualizado con id: {}", actualizado.getId());

        return toResponse(actualizado, proveedor);
    }

    public PedidosResponse cambiarEstado(Long id, String estado) {

        if (id == null) {
            log.error("El id del pedido no puede ser nulo");
            throw new NotFoundException("El id del pedido no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del pedido debe ser mayor a cero");
            throw new NotFoundException("El id del pedido debe ser mayor a cero");
        }

        if (estado == null || estado.trim().isEmpty()) {
            log.error("El estado del pedido no puede ser nulo o vacío");
            throw new NotFoundException("El estado del pedido no puede ser nulo o vacío");
        }

        PedidoModel pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el pedido con ID: {}", id);
                    return new NotFoundException("No existe el pedido con ID: " + id);
                });

        pedido.setEstado(estado);

        PedidoModel actualizado = pedidoRepository.save(pedido);

        log.info("Estado del pedido actualizado con id: {}", actualizado.getId());

        return toResponse(actualizado);
    }

    public void eliminar(Long id) {

        if (id == null) {
            log.error("El id del pedido no puede ser nulo");
            throw new NotFoundException("El id del pedido no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del pedido debe ser mayor a cero");
            throw new NotFoundException("El id del pedido debe ser mayor a cero");
        }

        if (!pedidoRepository.existsById(id)) {
            log.error("No existe el pedido con id: {}", id);
            throw new NotFoundException("No existe el pedido con id: " + id);
        }

        pedidoRepository.deleteById(id);

        log.info("Pedido eliminado con id: {}", id);
    }

    private PedidosResponse toResponse(PedidoModel model, ProveedorResponse proveedor) {

        if (model == null) {
            log.error("El pedido no puede ser nulo");
            throw new NotFoundException("El pedido no puede ser nulo");
        }

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

        if (model == null) {
            log.error("El pedido no puede ser nulo");
            throw new NotFoundException("El pedido no puede ser nulo");
        }

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

        if (proveedorId == null) {
            log.error("El id del proveedor no puede ser nulo");
            throw new NotFoundException("El id del proveedor no puede ser nulo");
        }

        if (proveedorId <= 0) {
            log.error("El id del proveedor debe ser mayor a cero");
            throw new NotFoundException("El id del proveedor debe ser mayor a cero");
        }

        try {
            ProveedorResponse proveedor = proveedorClient.obtenerProveedorPorId(proveedorId);

            if (proveedor == null) {
                log.error("No existe el proveedor con id: {}", proveedorId);
                throw new NotFoundException("No existe el proveedor con id: " + proveedorId);
            }

            return proveedor;

        } catch (Exception e) {
            throw new NotFoundException("No existe o no se pudo validar el proveedor con id: " + proveedorId);
        }
    }

    private void obtenerClienteDesdeServicio(Long clienteId) {

        if (clienteId == null) {
            log.error("El id del cliente no puede ser nulo");
            throw new NotFoundException("El id del cliente no puede ser nulo");
        }

        if (clienteId <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        try {
            ClienteResponse cliente = clienteClient.obtenerClientePorId(clienteId);

            if (cliente == null) {
                log.error("No existe el cliente con id: {}", clienteId);
                throw new NotFoundException("No existe el cliente con id: " + clienteId);
            }

        } catch (Exception e) {
            throw new NotFoundException("No existe o no se pudo validar el cliente con id: " + clienteId);
        }
    }

    private ProductoResponse obtenerProductoDesdeServicio(Long productoId) {

        if (productoId == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (productoId <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        try {
            ProductoResponse producto = productoClient.obtenerProductoPorId(productoId);

            if (producto == null) {
                log.error("No existe el producto con id: {}", productoId);
                throw new NotFoundException("No existe el producto con id: " + productoId);
            }

            return producto;

        } catch (Exception e) {
            throw new NotFoundException("No existe o no se pudo validar el producto con id: " + productoId);
        }
    }
}

    private void obtenerClienteDesdeServicio(Long clienteId) {
        try {
            clienteClient.obtenerClientePorId(clienteId);
        } catch (Exception e) {
            throw new NotFoundException("No existe o no se pudo validar el cliente con id: " + clienteId);
        }
    }}
main
