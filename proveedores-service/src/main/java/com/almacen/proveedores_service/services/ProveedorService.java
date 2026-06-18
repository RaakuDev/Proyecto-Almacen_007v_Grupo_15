package com.almacen.proveedores_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.almacen.proveedores_service.dtos.request.ProveedorRequest;
import com.almacen.proveedores_service.dtos.response.ProductoResponse;
import com.almacen.proveedores_service.dtos.response.ProveedorResponse;
import com.almacen.proveedores_service.exceptions.NotFoundException;
import com.almacen.proveedores_service.models.ProveedorModel;
import com.almacen.proveedores_service.repositories.ProveedorRepository;
import com.almacen.proveedores_service.webclient.ProductoClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoClient productoClient;

    public List<ProveedorResponse> obtenerTodos() {

        List<ProveedorModel> proveedores = proveedorRepository.findAll();

        if (proveedores.isEmpty()) {
            throw new NotFoundException("No existen proveedores registrados");
        }

        return proveedores.stream()
                .map(this::toResponse)
                .toList();
    }

    public ProveedorResponse obtenerPorId(Long id) {

        if (id == null) {
            throw new NotFoundException("El id del proveedor no puede ser nulo");
        }

        if (id <= 0) {
            throw new NotFoundException("El id del proveedor debe ser mayor a cero");
        }

        return proveedorRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new NotFoundException("No existe el proveedor con id: " + id)
                );
    }

    public List<ProductoResponse> obtenerProductosDelProveedor(Long proveedorId) {

        if (proveedorId == null) {
            throw new NotFoundException("El id del proveedor no puede ser nulo");
        }

        if (proveedorId <= 0) {
            throw new NotFoundException("El id del proveedor debe ser mayor a cero");
        }

        proveedorRepository.findById(proveedorId)
                .orElseThrow(() ->
                        new NotFoundException("No existe el proveedor con id: " + proveedorId)
                );

        List<ProductoResponse> productos = productoClient.obtenerProductosPorProveedor(proveedorId);

        if (productos == null || productos.isEmpty()) {
            throw new NotFoundException("El proveedor no tiene productos registrados");
        }

        return productos;
    }

    public ProveedorResponse guardar(ProveedorRequest request) {

        if (request == null) {
            throw new NotFoundException("Los datos del proveedor no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new NotFoundException("El nombre del proveedor es obligatorio");
        }

        if (request.getContacto() == null || request.getContacto().trim().isEmpty()) {
            throw new NotFoundException("El contacto del proveedor es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            throw new NotFoundException("El rut del proveedor es obligatorio");
        }

        ProveedorModel proveedor = new ProveedorModel();
        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setRut(request.getRut());

        ProveedorModel guardado = proveedorRepository.save(proveedor);

        return toResponse(guardado);
    }

    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {

        if (id == null) {
            throw new NotFoundException("El id del proveedor no puede ser nulo");
        }

        if (id <= 0) {
            throw new NotFoundException("El id del proveedor debe ser mayor a cero");
        }

        if (request == null) {
            throw new NotFoundException("Los datos del proveedor no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new NotFoundException("El nombre del proveedor es obligatorio");
        }

        if (request.getContacto() == null || request.getContacto().trim().isEmpty()) {
            throw new NotFoundException("El contacto del proveedor es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            throw new NotFoundException("El rut del proveedor es obligatorio");
        }

        ProveedorModel proveedor = proveedorRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("No existe el proveedor con id: " + id)
                );

        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setRut(request.getRut());

        ProveedorModel actualizado = proveedorRepository.save(proveedor);

        return toResponse(actualizado);
    }

    public void eliminar(Long id) {

        if (id == null) {
            throw new NotFoundException("El id del proveedor no puede ser nulo");
        }

        if (id <= 0) {
            throw new NotFoundException("El id del proveedor debe ser mayor a cero");
        }

        if (!proveedorRepository.existsById(id)) {
            throw new NotFoundException("No existe el proveedor con id: " + id);
        }

        proveedorRepository.deleteById(id);
    }

    private ProveedorResponse toResponse(ProveedorModel model) {

        if (model == null) {
            throw new NotFoundException("El proveedor no puede ser nulo");
        }

        return ProveedorResponse.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .contacto(model.getContacto())
                .rut(model.getRut())
                .build();
    }
}