package com.almacen.productos_service.controllers;

import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.models.ProductoModel;
import com.almacen.productos_service.services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoModel> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ProductoModel obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @PostMapping
    public ProductoModel crear(@Valid @RequestBody ProductoRequest request) {

        ProductoModel producto = ProductoModel.builder()
                .nombre(request.getNombre())
                .precio(request.getPrecio())
                .stock(request.getStock())
                .categoriaId(request.getCategoriaId())
                .build();

        return productoService.guardar(producto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}