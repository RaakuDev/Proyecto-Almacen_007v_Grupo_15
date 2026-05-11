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

    public InventarioResponse obtenerPorProductoId(Long productoId) {
        log.info("Buscando producto con id: {}", productoId);
        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NotFoundException("No existe inventario para el producto con id "+ productoId));
                return mapToResponseConProducto(inventario);
    }

    public InventarioResponse obtenerPorId(Long id) {
        log.info("Buscando inventario con id: {}", id);
        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el inventario con id: " + id));
        return mapToResponseConProducto(inventario);
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

    public void eliminar(Long id) {
        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el inventario con id: {}", id);
                    return new NotFoundException("No existe el inventario con id: " + id);
                });
        inventarioRepository.delete(inventario);
        log.info("Inventario eliminado, con id: {}", id);
    }

    private ProductoResponse obtenerProductoDesdeServicio(Long productoId) {
        try {
            return productoClient.obtenerProductoPorId(productoId);
        } catch (Exception e) {
            log.error("No existe el producto con id: {}", productoId);
            throw new NotFoundException("No existe el producto con id: " + productoId);
        }
    }

    public List<InventarioResponse> obtenerBajoStock() {
        log.info("Obteniendo productos de bajo stock");
        return inventarioRepository.findAll()
                .stream()
                .filter(inv -> inv.getStockActual() < inv.getStockMinimo())
                .map(this::mapToResponseConProducto)
                .toList();
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
