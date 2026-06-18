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

        log.info("Obteniendo todos los empleados");

        List<EmpleadoModel> empleados = empleadoRepository.findAll();

        if (empleados.isEmpty()) {
            log.error("No existen empleados registrados");
            throw new ResourceNotFoundException("No existen empleados registrados");
        }

        return empleados.stream()
                .map(this::toResponse)
                .toList();
    }

    public EmpleadoResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id del empleado no puede ser nulo");
            throw new ResourceNotFoundException("El id del empleado no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del empleado debe ser mayor a cero");
            throw new ResourceNotFoundException("El id del empleado debe ser mayor a cero");
        }

        log.info("Buscando empleado con id: {}", id);

        EmpleadoModel empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Empleado no encontrado con id: {}", id);
                    return new ResourceNotFoundException("Empleado no encontrado con id: " + id);
                });

        return toResponse(empleado);
    }

    public EmpleadoResponse obtenerPorUsuarioId(Long usuarioId) {

        if (usuarioId == null) {
            log.error("El usuarioId del empleado no puede ser nulo");
            throw new ResourceNotFoundException("El usuarioId del empleado no puede ser nulo");
        }

        if (usuarioId <= 0) {
            log.error("El usuarioId del empleado debe ser mayor a cero");
            throw new ResourceNotFoundException("El usuarioId del empleado debe ser mayor a cero");
        }

        log.info("Buscando empleado con usuarioId: {}", usuarioId);

        EmpleadoModel empleado = empleadoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> {
                    log.error("Empleado no encontrado con usuarioId: {}", usuarioId);
                    return new ResourceNotFoundException("Empleado no encontrado con usuarioId: " + usuarioId);
                });

        return toResponse(empleado);
    }

    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {

        if (request == null) {
            log.error("Los datos del empleado no pueden ser nulos");
            throw new ResourceNotFoundException("Los datos del empleado no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre del empleado es obligatorio");
            throw new ResourceNotFoundException("El nombre del empleado es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            log.error("El rut del empleado es obligatorio");
            throw new ResourceNotFoundException("El rut del empleado es obligatorio");
        }

        if (request.getCargo() == null || request.getCargo().trim().isEmpty()) {
            log.error("El cargo del empleado es obligatorio");
            throw new ResourceNotFoundException("El cargo del empleado es obligatorio");
        }

        if (request.getTurno() == null || request.getTurno().trim().isEmpty()) {
            log.error("El turno del empleado es obligatorio");
            throw new ResourceNotFoundException("El turno del empleado es obligatorio");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            log.error("El teléfono del empleado es obligatorio");
            throw new ResourceNotFoundException("El teléfono del empleado es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("El email del empleado es obligatorio");
            throw new ResourceNotFoundException("El email del empleado es obligatorio");
        }

        if (request.getFechaInicioContrato() == null) {
            log.error("La fecha de inicio de contrato es obligatoria");
            throw new ResourceNotFoundException("La fecha de inicio de contrato es obligatoria");
        }

        if (request.getActivo() == null) {
            log.error("El estado activo del empleado es obligatorio");
            throw new ResourceNotFoundException("El estado activo del empleado es obligatorio");
        }

        if (request.getUsuarioId() != null && request.getUsuarioId() <= 0) {
            log.error("El usuarioId del empleado debe ser mayor a cero");
            throw new ResourceNotFoundException("El usuarioId del empleado debe ser mayor a cero");
        }

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

        if (id == null) {
            log.error("El id del empleado no puede ser nulo");
            throw new ResourceNotFoundException("El id del empleado no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del empleado debe ser mayor a cero");
            throw new ResourceNotFoundException("El id del empleado debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos del empleado no pueden ser nulos");
            throw new ResourceNotFoundException("Los datos del empleado no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre del empleado es obligatorio");
            throw new ResourceNotFoundException("El nombre del empleado es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            log.error("El rut del empleado es obligatorio");
            throw new ResourceNotFoundException("El rut del empleado es obligatorio");
        }

        if (request.getCargo() == null || request.getCargo().trim().isEmpty()) {
            log.error("El cargo del empleado es obligatorio");
            throw new ResourceNotFoundException("El cargo del empleado es obligatorio");
        }

        if (request.getTurno() == null || request.getTurno().trim().isEmpty()) {
            log.error("El turno del empleado es obligatorio");
            throw new ResourceNotFoundException("El turno del empleado es obligatorio");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            log.error("El teléfono del empleado es obligatorio");
            throw new ResourceNotFoundException("El teléfono del empleado es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("El email del empleado es obligatorio");
            throw new ResourceNotFoundException("El email del empleado es obligatorio");
        }

        if (request.getFechaInicioContrato() == null) {
            log.error("La fecha de inicio de contrato es obligatoria");
            throw new ResourceNotFoundException("La fecha de inicio de contrato es obligatoria");
        }

        if (request.getActivo() == null) {
            log.error("El estado activo del empleado es obligatorio");
            throw new ResourceNotFoundException("El estado activo del empleado es obligatorio");
        }

        if (request.getUsuarioId() != null && request.getUsuarioId() <= 0) {
            log.error("El usuarioId del empleado debe ser mayor a cero");
            throw new ResourceNotFoundException("El usuarioId del empleado debe ser mayor a cero");
        }

        EmpleadoModel empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Empleado no encontrado con id: {}", id);
                    return new ResourceNotFoundException("Empleado no encontrado con id: " + id);
                });

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

        if (id == null) {
            log.error("El id del empleado no puede ser nulo");
            throw new ResourceNotFoundException("El id del empleado no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del empleado debe ser mayor a cero");
            throw new ResourceNotFoundException("El id del empleado debe ser mayor a cero");
        }

        if (!empleadoRepository.existsById(id)) {
            log.error("Empleado no encontrado con id: {}", id);
            throw new ResourceNotFoundException("Empleado no encontrado con id: " + id);
        }

        empleadoRepository.deleteById(id);

        log.info("Empleado eliminado con id: {}", id);
    }

    private EmpleadoResponse toResponse(EmpleadoModel model) {

        if (model == null) {
            log.error("El empleado no puede ser nulo");
            throw new ResourceNotFoundException("El empleado no puede ser nulo");
        }

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