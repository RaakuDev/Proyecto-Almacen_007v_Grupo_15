package com.almacen.productos_service.services;

import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.dtos.response.CategoriaResponse;
import com.almacen.productos_service.dtos.response.ProductoResponse;
import com.almacen.productos_service.exceptions.NotFoundException;
import com.almacen.productos_service.models.ProductoModel;
import com.almacen.productos_service.repositories.ProductoRepository;
import com.almacen.productos_service.webclient.CategoriaClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaClient categoriaClient;

    public ProductoService(ProductoRepository productoRepository, CategoriaClient categoriaClient) {
        this.productoRepository = productoRepository;
        this.categoriaClient = categoriaClient;
    }

    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapToResponseConCategoria)
                .toList();
    }

    public ProductoResponse obtenerPorId(Long id) {
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id: " + id));
        return mapToResponseConCategoria(producto);
    }

    public ProductoResponse guardar(ProductoRequest request) {
        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(request.getCategoriaId());

        ProductoModel producto = new ProductoModel();
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoriaId(request.getCategoriaId());

        ProductoModel guardado = productoRepository.save(producto);
        return mapToResponse(guardado, categoria);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id: " + id));

        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(request.getCategoriaId());

        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setCategoriaId(request.getCategoriaId());

        ProductoModel actualizado = productoRepository.save(producto);
        return mapToResponse(actualizado, categoria);
    }

    public void eliminar(Long id) {
        ProductoModel producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id: " + id));
        productoRepository.delete(producto);
    }

    private CategoriaResponse obtenerCategoriaDesdeServicio(Long categoriaId) {
        try {
            return categoriaClient.obtenerCatPorId(categoriaId);
        } catch (Exception e) {
            throw new NotFoundException("No existe la categoría con id: " + categoriaId);
        }
    }

    private ProductoResponse mapToResponseConCategoria(ProductoModel producto) {
        CategoriaResponse categoria = obtenerCategoriaDesdeServicio(producto.getCategoriaId());
        return mapToResponse(producto, categoria);
    }

    private ProductoResponse mapToResponse(ProductoModel producto, CategoriaResponse categoria) {
        return ProductoResponse.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoriaId(producto.getCategoriaId())
                .categoria(categoria)
                .build();
    }
}