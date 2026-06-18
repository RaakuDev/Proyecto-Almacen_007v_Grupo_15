package com.almacen.categorias_service.controllers;

import com.almacen.categorias_service.dtos.request.CategoriaRequest;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.services.CategoriaService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @GetMapping
    @Operation(
            summary = "Obtener todas las categorías",
            description = "Lista todas las categorías registradas en el sistema"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de categorías obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = CategoriaResponse.class)),
                            examples = @ExampleObject(
                                    name = "Ejemplo respuesta",
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "nombre": "Lácteos",
                                                    "descripcion": "Leche, queso, yogur"
                                                },
                                                {
                                                    "id": 2,
                                                    "nombre": "Bebidas",
                                                    "descripcion": "Gaseosas, jugos, energéticas"
                                                }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Token JWT inválido o ausente",
                    content = @Content
            )
    })
    public ResponseEntity<List<CategoriaResponse>> obtenerTodos() {
        return ResponseEntity.ok(categoriaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Retorna la categoría correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponse.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo exitoso",
                                    value = """
                                            {
                                                "id": 1,
                                                "nombre": "Lácteos",
                                                "descripcion": "Leche, queso, yogur"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe la categoría con id: 1",
                                                "path": "/api/v1/categorias/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<CategoriaResponse> obtenerPorId(
            @Parameter(description = "ID de la categoría a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva categoría", description = "Registra una nueva categoría en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 3,
                                                "nombre": "Snacks",
                                                "descripcion": "Papas fritas, galletas, chocolates"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 400,
                                                "error": "Bad Request",
                                                "message": "Error de validación en los datos enviados",
                                                "details": {
                                                    "nombre": "El nombre es obligatorio"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<CategoriaResponse> guardar(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse nueva = categoriaService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría por ID", description = "Actualiza los datos de una categoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoriaResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "nombre": "Lácteos y Derivados",
                                                "descripcion": "Leche, queso, yogur y mantequilla"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<CategoriaResponse> actualizar(
            @Parameter(description = "ID de la categoría a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría por ID", description = "Elimina del sistema la categoría correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Categoría eliminada\""))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID de la categoría a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok("Categoría eliminada");
    }
}