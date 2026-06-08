package com.almacen.categorias_service.controllers;

import com.almacen.categorias_service.dtos.request.CategoriaRequest;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorías", description = "Operaciones relacionadas con las categorías de productos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // GET ALL
    @GetMapping
    @Operation(summary = "Obtener todas las categorías", description = "Lista todas las categorías registradas")
    public ResponseEntity<List<CategoriaResponse>> obtenerTodos() {
        return ResponseEntity.ok(categoriaService.obtenerTodos());
    }

    // GET BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Muestra la categoría perteneciente a la ID ingresada")
    public ResponseEntity<CategoriaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    // CREATE
    @PostMapping
    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema")
    public ResponseEntity<CategoriaResponse> guardar(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse nueva = categoriaService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría por ID", description = "Actualiza datos de una categoría ya existente")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría por ID", description = "Borra del sistema una categoría")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok("Categoría eliminada");
    }
}