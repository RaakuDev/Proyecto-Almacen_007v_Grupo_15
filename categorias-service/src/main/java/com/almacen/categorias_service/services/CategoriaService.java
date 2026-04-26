package com.almacen.categorias_service.services;

import com.almacen.categorias_service.dtos.request.CategoriaRequest;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.exceptions.NotFoundException;
import com.almacen.categorias_service.models.CategoriaModel;
import com.almacen.categorias_service.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Obtener todas las categorías
    public List<CategoriaResponse> obtenerTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Obtener por ID
    public CategoriaResponse obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new NotFoundException("No existe la categoría con id: " + id)
                );
    }

    // Crear categoría
    public CategoriaResponse guardar(CategoriaRequest request) {

        CategoriaModel categoria = new CategoriaModel();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        CategoriaModel guardada = categoriaRepository.save(categoria);

        return toResponse(guardada);
    }

    // Actualizar categoría
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {

        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("No existe la categoría con id: " + id)
                );

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        CategoriaModel actualizada = categoriaRepository.save(categoria);

        return toResponse(actualizada);
    }

    // Eliminar categoría
    public void eliminar(Long id) {
        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("No existe la categoría con id: " + id)
                );

        categoriaRepository.delete(categoria);
    }

    // Mapper privado
    private CategoriaResponse toResponse(CategoriaModel cat) {
        return CategoriaResponse.builder()
                .id(cat.getId())
                .nombre(cat.getNombre())
                .descripcion(cat.getDescripcion())
                .build();
    }
}