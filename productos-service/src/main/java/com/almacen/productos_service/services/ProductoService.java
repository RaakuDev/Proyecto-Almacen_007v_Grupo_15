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
            InventarioClient inventarioClient, ProveedorClient proveedorClient
    ) {
        this.productoRepository = productoRepository;
        this.categoriaClient = categoriaClient;
        this.inventarioClient = inventarioClient;
        this.proveedorClient = proveedorClient;
    }

    public List<ProductoResponse> obtenerTodos() {
        log.info("Obteniendo todos los productos");

        return productoRepository.findAll()
                .stream()
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public ProductoResponse obtenerPorId(Long id) {
        log.info("Buscando producto con id: {}", id);

        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id: " + id));

        return mapToResponseConCategoria(producto);
    }

    public List<ProductoResponse> obtenerPorCategoria(Long categoriaId) {
        log.info("Buscando productos por categoría con id: {}", categoriaId);

        obtenerCategoriaDesdeServicio(categoriaId);

        return productoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public List<ProductoResponse> obtenerPorProveedor(Long proveedorId) {
        log.info("Buscando productos por proveedor con id: {}", proveedorId);

        obtenerProveedorDesdeServicio(proveedorId);

        return productoRepository.findByProveedorId(proveedorId)
                .stream()
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public ProductoResponse guardar(ProductoRequest request) {
        log.info("Guardando nuevo producto: {}", request.getNombre());

        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(request.getCategoriaId());
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());

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
        log.info("Actualizando producto con id: {}", id);

        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el producto con id: {}", id);
                    return new NotFoundException("No existe el producto con id: " + id);
                });

        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(request.getCategoriaId());
        ProveedorResponse proveedor = obtenerProveedorDesdeServicio(request.getProveedorId());

        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setCategoriaId(request.getCategoriaId());
        producto.setProveedorId(request.getProveedorId());

        ProductoModel actualizado = productoRepository.save(producto);

        log.info("Producto actualizado con id: {}", actualizado.getId());

        return mapToResponse(actualizado, categoria);
    }

    public void eliminar(Long id) {
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
        try {
            return categoriaClient.obtenerCatPorId(categoriaId);
        } catch (Exception e) {
            log.error("No existe la categoría con id: {}", categoriaId);
            throw new NotFoundException("No existe la categoría con id: " + categoriaId);
        }
    }

    private ProveedorResponse obtenerProveedorDesdeServicio(Long proveedorId) {
        try {
            return proveedorClient.obtenerProveedorPorId(proveedorId);
        } catch (Exception e) {
            log.error("No existe el proveedor con id: {}", proveedorId);
            throw new NotFoundException("No existe el proveedor con id: " + proveedorId);
        }
    }

    private ProductoResponse mapToResponseConCategoria(ProductoModel producto) {
        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(producto.getCategoriaId());
        return mapToResponse(producto, categoria);
    }

    private ProductoResponse mapToResponse(ProductoModel producto, CategoriaResponse categoria) {
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