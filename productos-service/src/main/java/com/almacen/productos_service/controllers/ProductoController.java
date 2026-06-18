package com.almacen.productos_service.controllers;

import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.dtos.response.ProductoResponse;
import com.almacen.productos_service.services.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con los productos del almacén")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos", description = "Lista todos los productos registrados con su categoría y proveedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "nombre": "Arroz 1kg",
                                                    "precio": 1500,
                                                    "categoriaId": 1,
                                                    "proveedorId": 1,
                                                    "categoria": {
                                                        "id": 1,
                                                        "nombre": "Abarrotes",
                                                        "descripcion": "Productos secos"
                                                    },
                                                    "proveedor": {
                                                        "id": 1,
                                                        "nombre": "Distribuidora Central",
                                                        "contacto": "contacto@central.cl",
                                                        "rut": "76.123.456-1"
                                                    }
                                                }
                                            ]
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<ProductoResponse>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Retorna el producto correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "nombre": "Arroz 1kg",
                                                "precio": 1500,
                                                "categoriaId": 1,
                                                "proveedorId": 1,
                                                "categoria": {
                                                    "id": 1,
                                                    "nombre": "Abarrotes",
                                                    "descripcion": "Productos secos"
                                                },
                                                "proveedor": {
                                                    "id": 1,
                                                    "nombre": "Distribuidora Central",
                                                    "contacto": "contacto@central.cl",
                                                    "rut": "76.123.456-1"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe el producto con id: 1",
                                                "path": "/api/v1/productos/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ProductoResponse> obtenerPorId(
            @Parameter(description = "ID del producto a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener productos por categoría", description = "Lista todos los productos pertenecientes a una categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe la categoría con id: 1",
                                                "path": "/api/v1/productos/categoria/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<ProductoResponse>> obtenerPorCategoria(
            @Parameter(description = "ID de la categoría a filtrar", required = true, example = "1")
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/proveedor/{proveedorId}")
    @Operation(summary = "Obtener productos por proveedor", description = "Lista todos los productos asociados a un proveedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe el proveedor con id: 1",
                                                "path": "/api/v1/productos/proveedor/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<ProductoResponse>> obtenerPorProveedor(
            @Parameter(description = "ID del proveedor a filtrar", required = true, example = "1")
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(productoService.obtenerPorProveedor(proveedorId));
    }

    @PostMapping
    @Operation(summary = "Crear un producto nuevo", description = "Registra un producto con todos los datos requeridos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 11,
                                                "nombre": "Coca Cola 1.5L",
                                                "precio": 1500,
                                                "categoriaId": 2,
                                                "proveedorId": 1,
                                                "categoria": {
                                                    "id": 2,
                                                    "nombre": "Bebidas",
                                                    "descripcion": "Gaseosas, jugos, energéticas"
                                                },
                                                "proveedor": {
                                                    "id": 1,
                                                    "nombre": "Distribuidora Central",
                                                    "contacto": "contacto@central.cl",
                                                    "rut": "76.123.456-1"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Error de validación en los datos enviados",
                                                "details": {
                                                    "nombre": "El nombre es obligatorio",
                                                    "precio": "El precio es obligatorio"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Categoría o proveedor no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guardar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto por ID", description = "Actualiza los datos de un producto ya registrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductoResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "nombre": "Arroz 2kg",
                                                "precio": 2800,
                                                "categoriaId": 1,
                                                "proveedorId": 1,
                                                "categoria": {
                                                    "id": 1,
                                                    "nombre": "Abarrotes",
                                                    "descripcion": "Productos secos"
                                                },
                                                "proveedor": {
                                                    "id": 1,
                                                    "nombre": "Distribuidora Central",
                                                    "contacto": "contacto@central.cl",
                                                    "rut": "76.123.456-1"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el request"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ProductoResponse> actualizar(
            @Parameter(description = "ID del producto a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un producto por ID", description = "Elimina un producto registrado por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Producto eliminado\""))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del producto a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado");
    }
}