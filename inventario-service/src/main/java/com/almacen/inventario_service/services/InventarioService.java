package com.almacen.inventario_service.services;

import com.almacen.inventario_service.dtos.request.InventarioRequest;
import com.almacen.inventario_service.dtos.response.InventarioResponse;
import com.almacen.inventario_service.dtos.response.ProductoResponse;
import com.almacen.inventario_service.exceptions.NotFoundException;
import com.almacen.inventario_service.models.InventarioModel;
import com.almacen.inventario_service.repositories.InventarioRepository;
import com.almacen.inventario_service.webclient.ProductoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoClient productoClient;

    public InventarioService(InventarioRepository inventarioRepository, ProductoClient productoClient) {
        this.inventarioRepository = inventarioRepository;
        this.productoClient = productoClient;
    }

    public List<InventarioResponse> obtenerTodos() {
        log.info("Obteniendo todo el inventario");
        return inventarioRepository.findAll()
                .stream()
                .map(this::mapToResponseConProducto)
                .toList();
    }

    public InventarioResponse descontarStock(Long productoId, Integer cantidad) {

        log.info("Descontando stock del producto ID: {}", productoId);

        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NotFoundException(
                        "No existe inventario para el producto ID: " + productoId));

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0");
        }

        if (inventario.getStockActual() < cantidad) {
            throw new IllegalArgumentException(
                    "Stock insuficiente. Stock actual: "
                            + inventario.getStockActual());
        }

        inventario.setStockActual(
                inventario.getStockActual() - cantidad);

        InventarioModel actualizado = inventarioRepository.save(inventario);

        log.info("Stock actualizado correctamente");

        return mapToResponseConProducto(actualizado);
    }

    public InventarioResponse obtenerPorProductoId(Long productoId) {
        log.info("Buscando inventario para producto con id: {}", productoId);

        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NotFoundException("No existe inventario para el producto con id " + productoId));

        return mapToResponseConProducto(inventario);
    }

    public InventarioResponse obtenerPorId(Long id) {
        log.info("Buscando inventario con id: {}", id);

        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el inventario con id: " + id));

        return mapToResponseConProducto(inventario);
    }

    public List<InventarioResponse> obtenerPorCategoria(Long categoriaId) {
        log.info("Buscando inventario por categoría con id: {}", categoriaId);

        List<ProductoResponse> productos = productoClient.obtenerProductosPorCategoria(categoriaId);

        return productos.stream()
                .map(producto -> inventarioRepository
                        .findByProductoId(producto.getId())
                        .map(inventario -> mapToResponse(inventario, producto))
                        .orElse(null))
                .filter(inventario -> inventario != null)
                .toList();
    }

    public InventarioResponse guardar(InventarioRequest request) {
        log.info("Guardando nuevo inventario para producto id: {}", request.getProductoId());

        ProductoResponse producto = obtenerProductoDesdeServicio(request.getProductoId());

        InventarioModel inventario = new InventarioModel();
        inventario.setProductoId(request.getProductoId());
        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());

        InventarioModel guardado = inventarioRepository.save(inventario);

        log.info("Inventario guardado con id: {}", guardado.getId());

        return mapToResponse(guardado, producto);
    }

    public InventarioResponse actualizar(Long id, InventarioRequest request) {
        log.info("Actualizando inventario con id: {}", id);

        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el inventario con id: {}", id);
                    return new NotFoundException("No existe el inventario con id: " + id);
                });

        ProductoResponse producto = obtenerProductoDesdeServicio(request.getProductoId());

        inventario.setStockActual(request.getStockActual());
        inventario.setStockMinimo(request.getStockMinimo());
        inventario.setProductoId(request.getProductoId());

        InventarioModel actualizado = inventarioRepository.save(inventario);

        log.info("Inventario actualizado con id: {}", actualizado.getId());

        return mapToResponse(actualizado, producto);
    }

    public InventarioResponse aumentarStock(Long productoId, Integer cantidad) {

        log.info("Aumentando stock del producto ID: {} en cantidad: {}",
                productoId, cantidad);

        InventarioModel inventario = inventarioRepository
                .findByProductoId(productoId)
                .orElseThrow(() -> {
                    log.error("No existe inventario para producto ID: {}",
                            productoId);

                    return new NotFoundException(
                            "No existe inventario para producto ID: "
                                    + productoId);
                });

        inventario.setStockActual(
                inventario.getStockActual() + cantidad);

        InventarioModel actualizado = inventarioRepository.save(inventario);

        log.info("Stock aumentado correctamente para producto ID: {}",
                productoId);

        return mapToResponseConProducto(actualizado);
    }

    public void eliminar(Long id) {
        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el inventario con id: {}", id);
                    return new NotFoundException("No existe el inventario con id: " + id);
                });

        inventarioRepository.delete(inventario);

        log.info("Inventario eliminado, con id: {}", id);
    }

    public List<InventarioResponse> obtenerBajoStock() {
        log.info("Obteniendo productos de bajo stock");

        return inventarioRepository.findAll()
                .stream()
                .filter(inv -> inv.getStockActual() < inv.getStockMinimo())
                .map(this::mapToResponseConProducto)
                .toList();
    }

    private ProductoResponse obtenerProductoDesdeServicio(Long productoId) {
        try {
            return productoClient.obtenerProductoPorId(productoId);
        } catch (Exception e) {
            log.error("No existe el producto con id: {}", productoId);
            throw new NotFoundException("No existe el producto con id: " + productoId);
        }
    }

    private InventarioResponse mapToResponseConProducto(InventarioModel inventario) {
        ProductoResponse producto = obtenerProductoDesdeServicio(inventario.getProductoId());
        return mapToResponse(inventario, producto);
    }

    private InventarioResponse mapToResponse(InventarioModel inventario, ProductoResponse producto) {
        return InventarioResponse.builder()
                .id(inventario.getId())
                .stockActual(inventario.getStockActual())
                .stockMinimo(inventario.getStockMinimo())
                .productoId(inventario.getProductoId())
                .producto(producto)
                .build();
    }
}