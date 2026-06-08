package com.almacen.productos_service.controllers;

import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.dtos.response.ProductoResponse;
import com.almacen.productos_service.services.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con los productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Lista todos los productos registrados")
    public ResponseEntity<List<ProductoResponse>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Muestra información del producto ID ingresado")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener producto por categoría", description = "Muestra productos de la categoria ID ingresada")
    public ResponseEntity<List<ProductoResponse>> obtenerPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/proveedor/{proveedorId}")
    @Operation(summary = "Obtener producto por proveedor", description = "Muestra todos los productos que se encargan a un proveedor por ID")
    public ResponseEntity<List<ProductoResponse>> obtenerPorProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(productoService.obtenerPorProveedor(proveedorId));
    }

    

    @PostMapping
    @Operation(summary = "Crear un producto nuevo", description = "Registra un producto con todos los datos requeridos")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto por ID", description = "Actualiza los datos de un producto ya registrado por ID")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request
    ) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto por ID", description = "Elimina un producto registrado por ID")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado");
    }
}