package com.almacen.productos_service.services;

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

    public List<ProductoModel> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<ProductoModel> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public ProductoModel guardar(ProductoModel producto) {

        try {

            categoriaClient.obtenerCatPorId(producto.getCategoriaId());
        } catch (Exception e) {
            throw new NotFoundException("Categoría no existe");
        }

        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}