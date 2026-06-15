package com.almacen.proveedores_service.controllers;

import com.almacen.proveedores_service.dtos.request.ProveedorRequest;
import com.almacen.proveedores_service.dtos.response.ProductoResponse;
import com.almacen.proveedores_service.dtos.response.ProveedorResponse;
import com.almacen.proveedores_service.services.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Operaciones relacionadas con los proveedores del almacén")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    @Operation(summary = "Obtener todos los proveedores", description = "Lista todos los proveedores registrados en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de proveedores obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProveedorResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "403", description = "No autorizado - Token JWT inválido o ausente")
    })
    public ResponseEntity<List<ProveedorResponse>> obtenerTodos() {
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID", description = "Retorna el proveedor correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProveedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ProveedorResponse> obtenerPorId(
            @Parameter(description = "ID del proveedor a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.obtenerPorId(id));
    }

    @GetMapping("/{proveedorId}/productos")
    @Operation(summary = "Obtener productos de un proveedor", description = "Retorna todos los productos asociados a un proveedor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Productos encontrados exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ProductoResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<ProductoResponse>> obtenerProductosDelProveedor(
            @Parameter(description = "ID del proveedor", required = true, example = "1")
            @PathVariable Long proveedorId
    ) {
        return ResponseEntity.ok(
                proveedorService.obtenerProductosDelProveedor(proveedorId)
        );
    }

    @PostMapping
    @Operation(summary = "Crear nuevo proveedor", description = "Registra un nuevo proveedor en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProveedorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ProveedorResponse> guardar(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse nuevo = proveedorService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor por ID", description = "Actualiza los datos de un proveedor existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProveedorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ProveedorResponse> actualizar(
            @Parameter(description = "ID del proveedor a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequest request
    ) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor por ID", description = "Elimina del sistema el proveedor correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proveedor eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Proveedor no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del proveedor a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        proveedorService.eliminar(id);
        return ResponseEntity.ok("Proveedor eliminado");
    }
}