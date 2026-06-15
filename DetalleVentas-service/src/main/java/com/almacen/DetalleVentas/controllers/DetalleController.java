package com.almacen.DetalleVentas.controllers;

import com.almacen.DetalleVentas.dtos.request.DetalleRequest;
import com.almacen.DetalleVentas.dtos.response.DetalleResponse;
import com.almacen.DetalleVentas.services.DetalleService;
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
@RequestMapping("/api/v1/detalles")
@RequiredArgsConstructor
@Tag(name = "Detalles de Venta", description = "Operaciones relacionadas con los detalles de ventas")
public class DetalleController {

    private final DetalleService detalleService;

    @GetMapping
    @Operation(summary = "Obtener todos los detalles", description = "Lista todos los detalles de ventas registrados en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de detalles obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = DetalleResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "403", description = "No autorizado - Token JWT inválido o ausente")
    })
    public ResponseEntity<List<DetalleResponse>> obtenerTodos() {
        return ResponseEntity.ok(detalleService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle por ID", description = "Retorna el detalle correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DetalleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<DetalleResponse> obtenerPorId(
            @Parameter(description = "ID del detalle a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(detalleService.obtenerPorId(id));
    }

    @GetMapping("/venta/{ventaId}")
    @Operation(summary = "Obtener detalles por ID de venta", description = "Retorna todos los detalles correspondientes a una venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalles encontrados exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = DetalleResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<DetalleResponse>> obtenerPorVentaId(
            @Parameter(description = "ID de la venta", required = true, example = "1")
            @PathVariable Long ventaId) {
        return ResponseEntity.ok(detalleService.obtenerPorVentaId(ventaId));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo detalle", description = "Registra un nuevo detalle de venta en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Detalle creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DetalleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<DetalleResponse> guardar(@Valid @RequestBody DetalleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(detalleService.guardar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar detalle por ID", description = "Actualiza los datos de un detalle existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DetalleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<DetalleResponse> actualizar(
            @Parameter(description = "ID del detalle a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody DetalleRequest request
    ) {
        return ResponseEntity.ok(detalleService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar detalle por ID", description = "Elimina del sistema el detalle correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Detalle no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del detalle a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        detalleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}