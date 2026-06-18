package com.almacen.inventario_service.controller;

import com.almacen.inventario_service.dtos.request.InventarioRequest;
import com.almacen.inventario_service.dtos.response.InventarioResponse;
import com.almacen.inventario_service.services.InventarioService;
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
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Operaciones relacionadas con el inventario y stock de productos")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los inventarios", description = "Obtiene una lista de todo el stock de los productos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InventarioResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "stockActual": 50,
                                                    "stockMinimo": 10,
                                                    "productoId": 1,
                                                    "producto": {
                                                        "id": 1,
                                                        "nombre": "Arroz 1kg",
                                                        "precio": 1500
                                                    }
                                                }
                                            ]
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<InventarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario por ID", description = "Obtiene el registro de stock correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "stockActual": 50,
                                                "stockMinimo": 10,
                                                "productoId": 1,
                                                "producto": {
                                                    "id": 1,
                                                    "nombre": "Arroz 1kg",
                                                    "precio": 1500
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe el inventario con id: 1",
                                                "path": "/api/v1/inventario/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<InventarioResponse> obtenerPorId(
            @Parameter(description = "ID del inventario a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Obtener inventario por ID de producto", description = "Obtiene el registro de stock del producto correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "stockActual": 50,
                                                "stockMinimo": 10,
                                                "productoId": 1,
                                                "producto": {
                                                    "id": 1,
                                                    "nombre": "Arroz 1kg",
                                                    "precio": 1500
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe inventario para el producto con id: 1",
                                                "path": "/api/v1/inventario/producto/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<InventarioResponse> obtenerPorProductoId(
            @Parameter(description = "ID del producto a buscar en inventario", required = true, example = "1")
            @PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerPorProductoId(productoId));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Obtener inventario por ID de categoría", description = "Obtiene el stock de todos los productos pertenecientes a una categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InventarioResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<InventarioResponse>> obtenerPorCategoria(
            @Parameter(description = "ID de la categoría a filtrar", required = true, example = "1")
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(inventarioService.obtenerPorCategoria(categoriaId));
    }

    @GetMapping("/bajo-stock")
    @Operation(summary = "Obtener productos con bajo stock", description = "Lista los productos cuyo stock actual es menor al stock mínimo definido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = InventarioResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 3,
                                                    "stockActual": 5,
                                                    "stockMinimo": 10,
                                                    "productoId": 3,
                                                    "producto": {
                                                        "id": 3,
                                                        "nombre": "Aceite 1L",
                                                        "precio": 2500
                                                    }
                                                }
                                            ]
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<InventarioResponse>> obtenerBajoStock() {
        return ResponseEntity.ok(inventarioService.obtenerBajoStock());
    }

    @PostMapping
    @Operation(summary = "Crear inventario para un producto", description = "Registra el stock inicial de un producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 11,
                                                "stockActual": 100,
                                                "stockMinimo": 20,
                                                "productoId": 11,
                                                "producto": {
                                                    "id": 11,
                                                    "nombre": "Coca Cola 1.5L",
                                                    "precio": 1500
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
                                                    "stockActual": "El stock actual es obligatorio",
                                                    "productoId": "El id del producto es obligatorio"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<InventarioResponse> crear(@Valid @RequestBody InventarioRequest request) {
        InventarioResponse nuevo = inventarioService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inventario de un producto", description = "Actualiza el registro de stock de un producto por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "stockActual": 80,
                                                "stockMinimo": 15,
                                                "productoId": 1,
                                                "producto": {
                                                    "id": 1,
                                                    "nombre": "Arroz 1kg",
                                                    "precio": 1500
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el request"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<InventarioResponse> actualizar(
            @Parameter(description = "ID del inventario a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizar(id, request));
    }

    @PutMapping("/producto/{productoId}/aumentar/{cantidad}")
    @Operation(summary = "Aumentar stock de un producto", description = "Incrementa el stock del producto según la cantidad ingresada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock aumentado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "stockActual": 60,
                                                "stockMinimo": 10,
                                                "productoId": 1,
                                                "producto": {
                                                    "id": 1,
                                                    "nombre": "Arroz 1kg",
                                                    "precio": 1500
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<InventarioResponse> aumentarStock(
            @Parameter(description = "ID del producto a aumentar stock", required = true, example = "1")
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad a aumentar", required = true, example = "10")
            @PathVariable Integer cantidad) {
        return ResponseEntity.ok(inventarioService.aumentarStock(productoId, cantidad));
    }

    @PutMapping("/producto/{productoId}/descontar/{cantidad}")
    @Operation(summary = "Descontar stock de un producto", description = "Reduce el stock del producto según la cantidad ingresada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock descontado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventarioResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "stockActual": 40,
                                                "stockMinimo": 10,
                                                "productoId": 1,
                                                "producto": {
                                                    "id": 1,
                                                    "nombre": "Arroz 1kg",
                                                    "precio": 1500
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en inventario"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<InventarioResponse> descontarStock(
            @Parameter(description = "ID del producto a descontar stock", required = true, example = "1")
            @PathVariable Long productoId,
            @Parameter(description = "Cantidad a descontar", required = true, example = "10")
            @PathVariable Integer cantidad) {
        return ResponseEntity.ok(inventarioService.descontarStock(productoId, cantidad));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inventario de un producto", description = "Elimina el registro de inventario correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario eliminado exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Inventario eliminado\""))),
            @ApiResponse(responseCode = "404", description = "Inventario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del inventario a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        inventarioService.eliminar(id);
        return ResponseEntity.ok("Inventario eliminado");
    }
}