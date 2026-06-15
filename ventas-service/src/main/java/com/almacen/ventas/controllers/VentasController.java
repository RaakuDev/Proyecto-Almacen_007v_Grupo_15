package com.almacen.ventas.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.almacen.ventas.Services.VentasServices;
import com.almacen.ventas.dtos.request.VentasRequest;
import com.almacen.ventas.dtos.response.VentasResponse;

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

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Operaciones relacionadas con las ventas del almacén")
public class VentasController {

    private final VentasServices ventasServices;

    public VentasController(VentasServices ventasServices) {
        this.ventasServices = ventasServices;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las ventas", description = "Lista todas las ventas registradas con sus detalles, cliente y empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VentasResponse.class)),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                                {
                                                                "idVenta": 1,
                                                                "fechaVenta": "2026-05-18T10:15:00",
                                                                "subTotal": 5000.00,
                                                                "descuentoTotal": 0.00,
                                                                "impuestoTotal": 950.00,
                                                                "total": 5950.00,
                                                                "metodoPago": "EFECTIVO",
                                                                "tipoComprobante": "BOLETA",
                                                                "montoPagado": 6000.00,
                                                                "vuelto": 50.00,
                                                                "estadoVenta": "COMPLETADA",
                                                                "clienteId": 1,
                                                                "empleadoId": 1,
                                                                "numeroComprobante": "BOL-0001",
                                                                "observaciones": "Venta normal",
                                                                "cliente": {
                                                                    "id": 1,
                                                                    "nombre": "Juan Pérez",
                                                                    "rut": "12.345.678-9"
                                                                },
                                                                "empleado": {
                                                                    "id": 1,
                                                                    "nombre": "Pedro González",
                                                                    "cargo": "Cajero"
                                                                }
                                                            }
                                            ]
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<List<VentasResponse>> obtenerTodas() {
        return ResponseEntity.ok(ventasServices.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Retorna la venta correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VentasResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                            "idVenta": 1,
                                                            "fechaVenta": "2026-05-18T10:15:00",
                                                            "subTotal": 5000.00,
                                                            "descuentoTotal": 0.00,
                                                            "impuestoTotal": 950.00,
                                                            "total": 5950.00,
                                                            "metodoPago": "EFECTIVO",
                                                            "tipoComprobante": "BOLETA",
                                                            "montoPagado": 6000.00,
                                                            "vuelto": 50.00,
                                                            "estadoVenta": "COMPLETADA",
                                                            "clienteId": 1,
                                                            "empleadoId": 1,
                                                            "numeroComprobante": "BOL-0001",
                                                            "observaciones": "Venta normal",
                                                            "cliente": {
                                                                "id": 1,
                                                                "nombre": "Juan Pérez",
                                                                "rut": "12.345.678-9"
                                                            },
                                                            "empleado": {
                                                                "id": 1,
                                                                "nombre": "Pedro González",
                                                                "cargo": "Cajero"
                                                            }
                                                        }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe la venta con ID: 1",
                                                "path": "/api/v1/ventas/1"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<VentasResponse> obtenerPorId(
            @Parameter(description = "ID de la venta a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ventasServices.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear nueva venta", description = "Registra una nueva venta con sus items, cliente y empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Venta creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VentasResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "idVenta": 11,
                                                "fechaVenta": "2026-05-18T10:15:00",
                                                "subTotal": 3000.00,
                                                "descuentoTotal": 0.00,
                                                "impuestoTotal": 570.00,
                                                "total": 3570.00,
                                                "metodoPago": "EFECTIVO",
                                                "tipoComprobante": "BOLETA",
                                                "montoPagado": 4000.00,
                                                "vuelto": 430.00,
                                                "estadoVenta": "COMPLETADA",
                                                "numeroComprobante": "B-0011",
                                                "observaciones": "Venta normal"
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
                                                    "metodoPago": "El método de pago es obligatorio",
                                                    "items": "Los items son obligatorios"
                                                }
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "404", description = "Cliente o empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<VentasResponse> guardar(@Valid @RequestBody VentasRequest request) {
        VentasResponse nuevaVenta = ventasServices.guardar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar venta por ID", description = "Actualiza los datos de una venta existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VentasResponse.class))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el request"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<VentasResponse> actualizar(
            @Parameter(description = "ID de la venta a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody VentasRequest request) {
        return ResponseEntity.ok(ventasServices.actualizar(id, request));
    }

    @GetMapping("/comprobante/{numeroComprobante}")
    @Operation(summary = "Obtener venta por número de comprobante", description = "Retorna la venta correspondiente al número de comprobante ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VentasResponse.class))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-18T10:15:00",
                                                "status": 404,
                                                "error": "Not Found",
                                                "message": "No existe una venta con comprobante: B-0001",
                                                "path": "/api/v1/ventas/comprobante/B-0001"
                                            }
                                            """
                            ))),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<VentasResponse> obtenerPorNumeroComprobante(
            @Parameter(description = "Número de comprobante a buscar", required = true, example = "B-0001")
            @PathVariable String numeroComprobante) {
        return ResponseEntity.ok(ventasServices.obtenerPorNumeroComprobante(numeroComprobante));
    }

    @PutMapping("/{id}/recalcular-total")
    @Operation(summary = "Recalcular total de una venta", description = "Recalcula el total de la venta basándose en sus detalles actuales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total recalculado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VentasResponse.class))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<VentasResponse> recalcularTotal(
            @Parameter(description = "ID de la venta a recalcular", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ventasServices.recalcularTotal(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar venta por ID", description = "Elimina del sistema la venta correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Venta eliminada exitosamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "\"Venta eliminada correctamente\""))),
            @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID de la venta a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        ventasServices.eliminar(id);
        return ResponseEntity.ok("Venta eliminada correctamente");
    }
}