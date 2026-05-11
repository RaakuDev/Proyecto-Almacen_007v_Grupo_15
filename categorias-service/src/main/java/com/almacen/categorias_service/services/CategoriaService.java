package com.almacen.categorias_service.services;

import com.almacen.categorias_service.dtos.request.CategoriaRequest;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.exceptions.NotFoundException;
import com.almacen.categorias_service.models.CategoriaModel;
import com.almacen.categorias_service.repositories.CategoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // Obtener todas las categorías
    public List<CategoriaResponse> obtenerTodos() {
        log.info("Obteniendo todas las categorías");
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Obtener por ID
    public CategoriaResponse obtenerPorId(Long id) {
        log.info("Buscando categoría con id: {}", id);
        return categoriaRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->{
                    log.error("No existe la categoría con id: {}", id);
                    return new NotFoundException("No existe la categoría con id: " + id);
                });
    }

    // Crear categoría
    public CategoriaResponse guardar(CategoriaRequest request) {
        log.info("Guardando nueva categoría: {}", request.getNombre());
        CategoriaModel categoria = new CategoriaModel();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        CategoriaModel guardada = categoriaRepository.save(categoria);
        log.info("Categoría guardada con id: {}", guardada.getId());
        return toResponse(guardada);
    }

    // Actualizar categoría
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        log.info("Actualizando categoría con id: {}", id);
        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe la categoría con id: {}", id);
                    return new NotFoundException("No existe la categoría con id: " + id);
                });

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        CategoriaModel actualizada = categoriaRepository.save(categoria);
        log.info("Categoría actualizada con id: {}", actualizada.getId());
        return toResponse(actualizada);
    }

    // Eliminar categoría
    public void eliminar(Long id) {
        log.info("Eliminando categoría con id: {}", id);
        CategoriaModel categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe la categoría con id: {}", id);
                    return new NotFoundException("No existe la categoría con id: " + id);
                });

        categoriaRepository.delete(categoria);
        log.info("Categoría eliminada con id: {}", id);
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