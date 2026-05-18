package com.almacen.empleados_service.service;

import java.util.List;

import com.almacen.empleados_service.dto.response.VentaResponse;
import com.almacen.empleados_service.webclient.VentasClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.almacen.empleados_service.dto.request.EmpleadoRequest;
import com.almacen.empleados_service.dto.response.EmpleadoResponse;
import com.almacen.empleados_service.exception.ResourceNotFoundException;
import com.almacen.empleados_service.model.EmpleadoModel;
import com.almacen.empleados_service.repository.EmpleadoRepository;

@Slf4j
@Service
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final VentasClient ventasClient;

    public EmpleadoService(EmpleadoRepository empleadoRepository, VentasClient ventasClient) {
        this.empleadoRepository = empleadoRepository;
        this.ventasClient = ventasClient;
    }

    public List<EmpleadoResponse> obtenerTodos() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EmpleadoResponse obtenerPorId(Long id) {
        EmpleadoModel empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));

        return toResponse(empleado);
    }

    public EmpleadoResponse obtenerPorUsuarioId(Long usuarioId) {
        EmpleadoModel empleado = empleadoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con usuarioId: " + usuarioId));

        return toResponse(empleado);
    }

    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        EmpleadoModel empleado = EmpleadoModel.builder()
                .nombre(request.getNombre())
                .rut(request.getRut())
                .cargo(request.getCargo())
                .turno(request.getTurno())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .fechaInicioContrato(request.getFechaInicioContrato())
                .activo(request.getActivo())
                .usuarioId(request.getUsuarioId())
                .build();

        EmpleadoModel guardado = empleadoRepository.save(empleado);

        log.info("Empleado creado con id: {}", guardado.getId());

        return toResponse(guardado);
    }

    public EmpleadoResponse actualizarEmpleado(Long id, EmpleadoRequest request) {
        EmpleadoModel empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con id: " + id));

        empleado.setNombre(request.getNombre());
        empleado.setRut(request.getRut());
        empleado.setCargo(request.getCargo());
        empleado.setTurno(request.getTurno());
        empleado.setTelefono(request.getTelefono());
        empleado.setEmail(request.getEmail());
        empleado.setFechaInicioContrato(request.getFechaInicioContrato());
        empleado.setActivo(request.getActivo());
        empleado.setUsuarioId(request.getUsuarioId());

        EmpleadoModel actualizado = empleadoRepository.save(empleado);

        log.info("Empleado actualizado con id: {}", actualizado.getId());

        return toResponse(actualizado);
    }

    public void eliminarEmpleado(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado no encontrado con id: " + id);
        }

        empleadoRepository.deleteById(id);

        log.info("Empleado eliminado con id: {}", id);
    }

    private EmpleadoResponse toResponse(EmpleadoModel model) {
        List<VentaResponse> ventas = obtenerVentasDesdeServicio(model.getId());

        return EmpleadoResponse.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .rut(model.getRut())
                .cargo(model.getCargo())
                .turno(model.getTurno())
                .telefono(model.getTelefono())
                .email(model.getEmail())
                .fechaInicioContrato(model.getFechaInicioContrato())
                .activo(model.getActivo())
                .usuarioId(model.getUsuarioId())
                .ventas(ventas)
                .build();
    }

    private List<VentaResponse> obtenerVentasDesdeServicio(Long empleadoId) {
        try {
            return ventasClient.obtenerVentasPorEmpleado(empleadoId);
        } catch (Exception e) {
            log.error("Error al obtener ventas del empleado con id: {}", empleadoId);
            return List.of();
        }
    }
}