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

        List<InventarioModel> inventarios = inventarioRepository.findAll();

        if (inventarios.isEmpty()) {
            log.error("No existen registros de inventario");
            throw new NotFoundException("No existen registros de inventario");
        }

        return inventarios.stream()
                .map(this::mapToResponseConProducto)
                .toList();
    }

    public InventarioResponse descontarStock(Long productoId, Integer cantidad) {

        if (productoId == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (productoId <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        if (cantidad == null) {
            log.error("La cantidad no puede ser nula");
            throw new NotFoundException("La cantidad no puede ser nula");
        }

        if (cantidad <= 0) {
            log.error("La cantidad debe ser mayor a cero");
            throw new NotFoundException("La cantidad debe ser mayor a cero");
        }

        log.info("Descontando stock del producto ID: {}", productoId);

        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NotFoundException(
                        "No existe inventario para el producto ID: " + productoId));

        if (inventario.getStockActual() < cantidad) {
            log.error("Stock insuficiente. Stock actual: {}", inventario.getStockActual());
            throw new NotFoundException(
                    "Stock insuficiente. Stock actual: " + inventario.getStockActual());
        }

        inventario.setStockActual(inventario.getStockActual() - cantidad);

        InventarioModel actualizado = inventarioRepository.save(inventario);

        log.info("Stock actualizado correctamente");

        return mapToResponseConProducto(actualizado);
    }

    public InventarioResponse obtenerPorProductoId(Long productoId) {

        if (productoId == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (productoId <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        log.info("Buscando inventario para producto con id: {}", productoId);

        InventarioModel inventario = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new NotFoundException(
                        "No existe inventario para el producto con id " + productoId));

        return mapToResponseConProducto(inventario);
    }

    public InventarioResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id del inventario no puede ser nulo");
            throw new NotFoundException("El id del inventario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del inventario debe ser mayor a cero");
            throw new NotFoundException("El id del inventario debe ser mayor a cero");
        }

        log.info("Buscando inventario con id: {}", id);

        InventarioModel inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "No existe el inventario con id: " + id));

        return mapToResponseConProducto(inventario);
    }

    public List<InventarioResponse> obtenerPorCategoria(Long categoriaId) {

        if (categoriaId == null) {
            log.error("El id de la categoría no puede ser nulo");
            throw new NotFoundException("El id de la categoría no puede ser nulo");
        }

        if (categoriaId <= 0) {
            log.error("El id de la categoría debe ser mayor a cero");
            throw new NotFoundException("El id de la categoría debe ser mayor a cero");
        }

        log.info("Buscando inventario por categoría con id: {}", categoriaId);

        List<ProductoResponse> productos = productoClient.obtenerProductosPorCategoria(categoriaId);

        if (productos == null || productos.isEmpty()) {
            log.error("No existen productos para la categoría con id: {}", categoriaId);
            throw new NotFoundException("No existen productos para la categoría con id: " + categoriaId);
        }

        List<InventarioResponse> inventarios = productos.stream()
                .map(producto -> inventarioRepository
                        .findByProductoId(producto.getId())
                        .map(inventario -> mapToResponse(inventario, producto))
                        .orElse(null))
                .filter(inventario -> inventario != null)
                .toList();

        if (inventarios.isEmpty()) {
            log.error("No existen inventarios para la categoría con id: {}", categoriaId);
            throw new NotFoundException("No existen inventarios para la categoría con id: " + categoriaId);
        }

        return inventarios;
    }

    public InventarioResponse guardar(InventarioRequest request) {

        if (request == null) {
            log.error("Los datos del inventario no pueden ser nulos");
            throw new NotFoundException("Los datos del inventario no pueden ser nulos");
        }

        if (request.getProductoId() == null || request.getProductoId() <= 0) {
            log.error("El id del producto es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
        }

        if (request.getStockActual() == null) {
            log.error("El stock actual no puede ser nulo");
            throw new NotFoundException("El stock actual no puede ser nulo");
        }

        if (request.getStockActual() < 0) {
            log.error("El stock actual no puede ser menor a cero");
            throw new NotFoundException("El stock actual no puede ser menor a cero");
        }

        if (request.getStockMinimo() == null) {
            log.error("El stock mínimo no puede ser nulo");
            throw new NotFoundException("El stock mínimo no puede ser nulo");
        }

        if (request.getStockMinimo() < 0) {
            log.error("El stock mínimo no puede ser menor a cero");
            throw new NotFoundException("El stock mínimo no puede ser menor a cero");
        }

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

        if (id == null) {
            log.error("El id del inventario no puede ser nulo");
            throw new NotFoundException("El id del inventario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del inventario debe ser mayor a cero");
            throw new NotFoundException("El id del inventario debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos del inventario no pueden ser nulos");
            throw new NotFoundException("Los datos del inventario no pueden ser nulos");
        }

        if (request.getProductoId() == null || request.getProductoId() <= 0) {
            log.error("El id del producto es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id del producto es obligatorio y debe ser mayor a cero");
        }

        if (request.getStockActual() == null) {
            log.error("El stock actual no puede ser nulo");
            throw new NotFoundException("El stock actual no puede ser nulo");
        }

        if (request.getStockActual() < 0) {
            log.error("El stock actual no puede ser menor a cero");
            throw new NotFoundException("El stock actual no puede ser menor a cero");
        }

        if (request.getStockMinimo() == null) {
            log.error("El stock mínimo no puede ser nulo");
            throw new NotFoundException("El stock mínimo no puede ser nulo");
        }

        if (request.getStockMinimo() < 0) {
            log.error("El stock mínimo no puede ser menor a cero");
            throw new NotFoundException("El stock mínimo no puede ser menor a cero");
        }

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

        if (productoId == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (productoId <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        if (cantidad == null) {
            log.error("La cantidad no puede ser nula");
            throw new NotFoundException("La cantidad no puede ser nula");
        }

        if (cantidad <= 0) {
            log.error("La cantidad debe ser mayor a cero");
            throw new NotFoundException("La cantidad debe ser mayor a cero");
        }

        log.info("Aumentando stock del producto ID: {} en cantidad: {}",
                productoId, cantidad);

        InventarioModel inventario = inventarioRepository
                .findByProductoId(productoId)
                .orElseThrow(() -> {
                    log.error("No existe inventario para producto ID: {}", productoId);
                    return new NotFoundException(
                            "No existe inventario para producto ID: " + productoId);
                });

        inventario.setStockActual(inventario.getStockActual() + cantidad);

        InventarioModel actualizado = inventarioRepository.save(inventario);

        log.info("Stock aumentado correctamente para producto ID: {}", productoId);

        return mapToResponseConProducto(actualizado);
    }

    public void eliminar(Long id) {

        if (id == null) {
            log.error("El id del inventario no puede ser nulo");
            throw new NotFoundException("El id del inventario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del inventario debe ser mayor a cero");
            throw new NotFoundException("El id del inventario debe ser mayor a cero");
        }

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

        List<InventarioResponse> inventarios = inventarioRepository.findAll()
                .stream()
                .filter(inv -> inv.getStockActual() < inv.getStockMinimo())
                .map(this::mapToResponseConProducto)
                .toList();

        if (inventarios.isEmpty()) {
            log.error("No existen productos con bajo stock");
            throw new NotFoundException("No existen productos con bajo stock");
        }

        return inventarios;
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

        if (inventario == null) {
            log.error("El inventario no puede ser nulo");
            throw new NotFoundException("El inventario no puede ser nulo");
        }

        if (producto == null) {
            log.error("El producto no puede ser nulo");
            throw new NotFoundException("El producto no puede ser nulo");
        }

        return InventarioResponse.builder()
                .id(inventario.getId())
                .stockActual(inventario.getStockActual())
                .stockMinimo(inventario.getStockMinimo())
                .productoId(inventario.getProductoId())
                .producto(producto)
                .build();
    }
}