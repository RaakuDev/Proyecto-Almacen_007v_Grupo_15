package com.almacen.empleados_service.controller;

import com.almacen.empleados_service.dto.request.EmpleadoRequest;
import com.almacen.empleados_service.dto.response.EmpleadoResponse;
import com.almacen.empleados_service.service.EmpleadoService;
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
@RequestMapping("/api/v1/empleados")
@RequiredArgsConstructor
@Tag(name = "Empleados", description = "Operaciones relacionadas con los empleados del almacén")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    @Operation(summary = "Obtener todos los empleados", description = "Lista todos los empleados registrados en el sistema")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de empleados obtenida exitosamente",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EmpleadoResponse.class))
                    )
            ),
            @ApiResponse(responseCode = "403", description = "No autorizado - Token JWT inválido o ausente")
    })
    public ResponseEntity<List<EmpleadoResponse>> obtenerTodos() {
        return ResponseEntity.ok(empleadoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empleado por ID", description = "Retorna el empleado correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<EmpleadoResponse> obtenerPorId(
            @Parameter(description = "ID del empleado a buscar", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(empleadoService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener empleado por ID de usuario", description = "Retorna el empleado correspondiente al ID de usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<EmpleadoResponse> obtenerPorUsuarioId(
            @Parameter(description = "ID del usuario", required = true, example = "1")
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(empleadoService.obtenerPorUsuarioId(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo empleado", description = "Registra un nuevo empleado en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crearEmpleado(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar empleado por ID", description = "Actualiza los datos de un empleado existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmpleadoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<EmpleadoResponse> actualizarEmpleado(
            @Parameter(description = "ID del empleado a actualizar", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody EmpleadoRequest request
    ) {
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar empleado por ID", description = "Elimina del sistema el empleado correspondiente al ID ingresado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    public ResponseEntity<String> eliminarEmpleado(
            @Parameter(description = "ID del empleado a eliminar", required = true, example = "1")
            @PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return ResponseEntity.ok("Empleado eliminado");
    }
}