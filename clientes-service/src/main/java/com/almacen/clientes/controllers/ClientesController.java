package com.almacen.clientes.controllers;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.dtos.response.PedidoResponse;
import com.almacen.clientes.services.ClientesService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Operaciones relacionadas con los clientes del almacén")
public class ClientesController {

    private final ClientesService clientesService;

    @GetMapping
    @Operation(summary = "Obtener todos los clientes", description = "Lista todos los clientes registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ClientesResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "nombre": "Juan Pérez",
                                                    "rut": "12.345.678-9",
                                                    "direccion": "Av. Providencia 1234, Santiago",
                                                    "telefono": "+56912345678",
                                                    "email": "juan.perez@email.com"
                                                }
                                            ]
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<ClientesResponse>> obtenerTodos() {
        return ResponseEntity.ok(clientesService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", description = "Retorna el cliente correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientesResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "nombre": "Juan Pérez",
                                                "rut": "12.345.678-9",
                                                "direccion": "Av. Providencia 1234, Santiago",
                                                "telefono": "+56912345678",
                                                "email": "juan.perez@email.com"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe el cliente con id: 1",
                                                "path": "/api/v1/clientes/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ClientesResponse> obtenerPorId(
            @Parameter(description = "ID del cliente a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(clientesService.obtenerPorId(id));
    }

    @GetMapping("/{clienteId}/pedidos")
    @Operation(summary = "Obtener pedidos de un cliente", description = "Lista todos los pedidos realizados por el cliente correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PedidoResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                    "id": 1,
                                                    "fechaPedido": "2026-05-18",
                                                    "estado": "COMPLETADO",
                                                    "proveedorId": 1,
                                                    "clienteId": 1
                                                }
                                            ]
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe el cliente con id: 1",
                                                "path": "/api/v1/clientes/1/pedidos"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<PedidoResponse>> obtenerPedidosDelCliente(
            @Parameter(description = "ID del cliente a consultar", required = true, example = "1")
            @PathVariable Long clienteId) {
        return ResponseEntity.ok(clientesService.obtenerPedidosDelCliente(clienteId));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientesResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 11,
                                                "nombre": "María González",
                                                "rut": "15.678.901-2",
                                                "direccion": "Calle Los Aromos 456, Valparaíso",
                                                "telefono": "+56987654321",
                                                "email": "maria.gonzalez@email.com"
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
                                                    "rut": "El RUT es obligatorio"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ClientesResponse> crear(@Valid @RequestBody ClientesRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientesService.guardar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente por ID", description = "Actualiza los datos de un cliente existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientesResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "nombre": "Juan Pérez Actualizado",
                                                "rut": "12.345.678-9",
                                                "direccion": "Nueva dirección 789, Santiago",
                                                "telefono": "+56911111111",
                                                "email": "juan.nuevo@email.com"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el request"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<ClientesResponse> actualizar(
            @Parameter(description = "ID del cliente a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ClientesRequest request) {
        return ResponseEntity.ok(clientesService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente por ID", description = "Elimina del sistema el cliente correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cliente a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        clientesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}