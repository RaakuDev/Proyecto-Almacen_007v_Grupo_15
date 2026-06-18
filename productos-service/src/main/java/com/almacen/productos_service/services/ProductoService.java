package com.almacen.productos_service.services;

import com.almacen.productos_service.dtos.request.InventarioRequest;
import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.dtos.response.CategoriaResponse;
import com.almacen.productos_service.dtos.response.ProductoResponse;
import com.almacen.productos_service.dtos.response.ProveedorResponse;
import com.almacen.productos_service.exceptions.NotFoundException;
import com.almacen.productos_service.models.ProductoModel;
import com.almacen.productos_service.repositories.ProductoRepository;
import com.almacen.productos_service.webclient.CategoriaClient;
import com.almacen.productos_service.webclient.InventarioClient;
import com.almacen.productos_service.webclient.ProveedorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaClient categoriaClient;
    private final InventarioClient inventarioClient;
    private final ProveedorClient proveedorClient;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaClient categoriaClient,
            InventarioClient inventarioClient,
            ProveedorClient proveedorClient
    ) {
        this.productoRepository = productoRepository;
        this.categoriaClient = categoriaClient;
        this.inventarioClient = inventarioClient;
        this.proveedorClient = proveedorClient;
    }

    public List<ProductoResponse> obtenerTodos() {
        log.info("Obteniendo todos los productos");

        List<ProductoModel> productos = productoRepository.findAll();

        if (productos.isEmpty()) {
            log.error("No existen productos registrados");
            throw new NotFoundException("No existen productos registrados");
        }

        return productos.stream()
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public ProductoResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        log.info("Buscando producto con id: {}", id);

        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el producto con id: {}", id);
                    return new NotFoundException("No existe el producto con id: " + id);
                });

        return mapToResponseConCategoria(producto);
    }

    public List<ProductoResponse> obtenerPorCategoria(Long categoriaId) {

        if (categoriaId == null) {
            log.error("El id de categoría no puede ser nulo");
            throw new NotFoundException("El id de categoría no puede ser nulo");
        }

        if (categoriaId <= 0) {
            log.error("El id de categoría debe ser mayor a cero");
            throw new NotFoundException("El id de categoría debe ser mayor a cero");
        }

        log.info("Buscando productos por categoría con id: {}", categoriaId);

        obtenerCategoriaDesdeServicio(categoriaId);

        List<ProductoModel> productos = productoRepository.findByCategoriaId(categoriaId);

        if (productos.isEmpty()) {
            log.error("No existen productos para la categoría con id: {}", categoriaId);
            throw new NotFoundException("No existen productos para la categoría con id: " + categoriaId);
        }

        return productos.stream()
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public List<ProductoResponse> obtenerPorProveedor(Long proveedorId) {

        if (proveedorId == null) {
            log.error("El id de proveedor no puede ser nulo");
            throw new NotFoundException("El id de proveedor no puede ser nulo");
        }

        if (proveedorId <= 0) {
            log.error("El id de proveedor debe ser mayor a cero");
            throw new NotFoundException("El id de proveedor debe ser mayor a cero");
        }

        log.info("Buscando productos por proveedor con id: {}", proveedorId);

        obtenerProveedorDesdeServicio(proveedorId);

        return productoRepository.findByProveedorId(proveedorId)
                .stream()

        obtenerProveedorDesdeServicio(proveedorId);

        List<ProductoModel> productos = productoRepository.findByProveedorId(proveedorId);

        if (productos.isEmpty()) {
            log.error("No existen productos para el proveedor con id: {}", proveedorId);
            throw new NotFoundException("No existen productos para el proveedor con id: " + proveedorId);
        }

        return productos.stream()
         Stashed changes
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public ProductoResponse guardar(ProductoRequest request) {

        if (request == null) {
            log.error("Los datos del producto no pueden ser nulos");
            throw new NotFoundException("Los datos del producto no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre del producto es obligatorio");
            throw new NotFoundException("El nombre del producto es obligatorio");
        }

        if (request.getPrecio() == null) {
            log.error("El precio del producto no puede ser nulo");
            throw new NotFoundException("El precio del producto no puede ser nulo");
        }

        if (request.getPrecio() < 0) {
            log.error("El precio del producto no puede ser negativo");
            throw new NotFoundException("El precio del producto no puede ser negativo");
        }

        if (request.getCategoriaId() == null || request.getCategoriaId() <= 0) {
            log.error("El id de categoría es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id de categoría es obligatorio y debe ser mayor a cero");
        }

        if (request.getProveedorId() == null || request.getProveedorId() <= 0) {
            log.error("El id de proveedor es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id de proveedor es obligatorio y debe ser mayor a cero");
        }

        if (request.getStockInicial() == null) {
            log.error("El stock inicial no puede ser nulo");
            throw new NotFoundException("El stock inicial no puede ser nulo");
        }

        if (request.getStockInicial() < 0) {
            log.error("El stock inicial no puede ser negativo");
            throw new NotFoundException("El stock inicial no puede ser negativo");
        }

        if (request.getStockMinimo() == null) {
            log.error("El stock mínimo no puede ser nulo");
            throw new NotFoundException("El stock mínimo no puede ser nulo");
        }

        if (request.getStockMinimo() < 0) {
            log.error("El stock mínimo no puede ser negativo");
            throw new NotFoundException("El stock mínimo no puede ser negativo");
        }

        log.info("Guardando nuevo producto: {}", request.getNombre());

        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(request.getCategoriaId());
        obtenerProveedorDesdeServicio(request.getProveedorId());

        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());
       main

        ProductoModel producto = new ProductoModel();
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setProveedorId(request.getProveedorId());

        ProductoModel guardado = productoRepository.save(producto);

        log.info("Producto guardado con id: {}", guardado.getId());

        InventarioRequest inventarioRequest = InventarioRequest.builder()
                .productoId(guardado.getId())
                .stockActual(request.getStockInicial())
                .stockMinimo(request.getStockMinimo())
                .build();

        try {
            inventarioClient.crearInventario(inventarioRequest);
            log.info("Inventario creado automáticamente para producto id: {}", guardado.getId());
        } catch (Exception e) {
            log.error("ERROR AL CREAR INVENTARIO para producto id {}: {}", guardado.getId(), e.getMessage());
            throw e;
        }

        return mapToResponse(guardado, categoria);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {

        if (id == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos del producto no pueden ser nulos");
            throw new NotFoundException("Los datos del producto no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre del producto es obligatorio");
            throw new NotFoundException("El nombre del producto es obligatorio");
        }

        if (request.getPrecio() == null) {
            log.error("El precio del producto no puede ser nulo");
            throw new NotFoundException("El precio del producto no puede ser nulo");
        }

        if (request.getPrecio() < 0) {
            log.error("El precio del producto no puede ser negativo");
            throw new NotFoundException("El precio del producto no puede ser negativo");
        }

        if (request.getCategoriaId() == null || request.getCategoriaId() <= 0) {
            log.error("El id de categoría es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id de categoría es obligatorio y debe ser mayor a cero");
        }

        if (request.getProveedorId() == null || request.getProveedorId() <= 0) {
            log.error("El id de proveedor es obligatorio y debe ser mayor a cero");
            throw new NotFoundException("El id de proveedor es obligatorio y debe ser mayor a cero");
        }

        log.info("Actualizando producto con id: {}", id);

        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el producto con id: {}", id);
                    return new NotFoundException("No existe el producto con id: " + id);
                });

        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(request.getCategoriaId());
        obtenerProveedorDesdeServicio(request.getProveedorId());

        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());
        main

        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setProveedorId(request.getProveedorId());

        ProductoModel actualizado = productoRepository.save(producto);

        log.info("Producto actualizado con id: {}", actualizado.getId());

        return mapToResponse(actualizado, categoria);
    }

    public void eliminar(Long id) {

        if (id == null) {
            log.error("El id del producto no puede ser nulo");
            throw new NotFoundException("El id del producto no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del producto debe ser mayor a cero");
            throw new NotFoundException("El id del producto debe ser mayor a cero");
        }

        log.info("Eliminando producto con id: {}", id);

        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el producto con id: {}", id);
                    return new NotFoundException("No existe el producto con id: " + id);
                });

        productoRepository.delete(producto);

        log.info("Producto eliminado con id: {}", id);
    }

    private CategoriaResponse obtenerCategoriaDesdeServicio(Long categoriaId) {

        if (categoriaId == null) {
            log.error("El id de categoría no puede ser nulo");
            throw new NotFoundException("El id de categoría no puede ser nulo");
        }

        if (categoriaId <= 0) {
            log.error("El id de categoría debe ser mayor a cero");
            throw new NotFoundException("El id de categoría debe ser mayor a cero");
        }

        try {
            CategoriaResponse categoria = categoriaClient.obtenerCatPorId(categoriaId);

            if (categoria == null) {
                log.error("No existe la categoría con id: {}", categoriaId);
                throw new NotFoundException("No existe la categoría con id: " + categoriaId);
            }

            return categoria;

        } catch (Exception e) {
            log.error("No existe la categoría con id: {}", categoriaId);
            throw new NotFoundException("No existe la categoría con id: " + categoriaId);
        }
    }

    private ProveedorResponse obtenerProveedorDesdeServicio(Long proveedorId) {

        if (proveedorId == null) {
            log.error("El id de proveedor no puede ser nulo");
            throw new NotFoundException("El id de proveedor no puede ser nulo");
        }

        if (proveedorId <= 0) {
            log.error("El id de proveedor debe ser mayor a cero");
            throw new NotFoundException("El id de proveedor debe ser mayor a cero");
        }

        try {
            ProveedorResponse proveedor = proveedorClient.obtenerProveedorPorId(proveedorId);

            if (proveedor == null) {
                log.error("No existe el proveedor con id: {}", proveedorId);
                throw new NotFoundException("No existe el proveedor con id: " + proveedorId);
            }

            return proveedor;

        } catch (Exception e) {
            log.error("No existe el proveedor con id: {}", proveedorId);
            throw new NotFoundException("No existe el proveedor con id: " + proveedorId);
        }
    }

    private ProductoResponse mapToResponseConCategoria(ProductoModel producto) {

        if (producto == null) {
            log.error("El producto no puede ser nulo");
            throw new NotFoundException("El producto no puede ser nulo");
        }

        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(producto.getCategoriaId());

        return mapToResponse(producto, categoria);
    }

    private ProductoResponse mapToResponse(ProductoModel producto, CategoriaResponse categoria) {

        if (producto == null) {
            log.error("El producto no puede ser nulo");
            throw new NotFoundException("El producto no puede ser nulo");
        }

        if (categoria == null) {
            log.error("La categoría del producto no puede ser nula");
            throw new NotFoundException("La categoría del producto no puede ser nula");
        }

        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(producto.getProveedorId());

        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .categoriaId(producto.getCategoriaId())
                .proveedorId(producto.getProveedorId())
                .categoria(categoria)
                .proveedor(proveedor)
                .build();
    }
}