package com.almacen.categorias_service.controllers;

import com.almacen.categorias_service.dtos.request.CategoriaRequest;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.services.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // GET ALL
    @GetMapping
    public List<CategoriaResponse> obtenerTodos() {
        return categoriaService.obtenerTodos();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<CategoriaResponse> guardar(@RequestBody CategoriaRequest request) {
        CategoriaResponse nueva = categoriaService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(@PathVariable Long id,
                                                        @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok("Categoría eliminada");
    }
}