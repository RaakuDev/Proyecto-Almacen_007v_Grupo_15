package com.almacen.pedidos_service.controller;

import com.almacen.pedidos_service.dtos.request.PedidosRequest;
import com.almacen.pedidos_service.dtos.response.PedidosResponse;
import com.almacen.pedidos_service.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones relacionadas con los pedidos del almacén")
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Obtener todos los pedidos", description = "Lista todos los pedidos registrados en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de pedidos obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PedidosResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "403", description = "No autorizado - Token JWT inválido o ausente")
    })
    public ResponseEntity<List<PedidosResponse>> obtenerTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Retorna el pedido correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<PedidosResponse> obtenerPorId(
            @Parameter(description = "ID del pedido a buscar", required = true, example = "1")
            @PathVariable @NonNull Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener pedidos por ID de cliente", description = "Retorna todos los pedidos de un cliente específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PedidosResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<PedidosResponse>> obtenerPorCliente(
            @Parameter(description = "ID del cliente", required = true, example = "1")
            @PathVariable Long clienteId
    ) {
        return ResponseEntity.ok(pedidoService.obtenerPorCliente(clienteId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener pedidos por estado", description = "Retorna todos los pedidos con un estado específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedidos encontrados exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = PedidosResponse.class)))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<PedidosResponse>> obtenerPorEstado(
            @Parameter(description = "Estado del pedido (PENDIENTE, APROBADO, RECHAZADO, ENTREGADO)", required = true, example = "PENDIENTE")
            @PathVariable String estado
    ) {
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}/estado/{estado}")
    @Operation(summary = "Cambiar estado de un pedido", description = "Actualiza el estado de un pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<PedidosResponse> cambiarEstado(
            @Parameter(description = "ID del pedido", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado (PENDIENTE, APROBADO, RECHAZADO, ENTREGADO)", required = true, example = "APROBADO")
            @PathVariable String estado
    ) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo pedido", description = "Registra un nuevo pedido en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<PedidosResponse> guardar(@Valid @RequestBody PedidosRequest request) {
        PedidosResponse nuevo = pedidoService.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pedido por ID", description = "Actualiza los datos de un pedido existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PedidosResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<PedidosResponse> actualizar(
            @Parameter(description = "ID del pedido a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PedidosRequest request
    ) {
        return ResponseEntity.ok(pedidoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido por ID", description = "Elimina del sistema el pedido correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del pedido a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.ok("Pedido eliminado");
    }
}