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
        return proveedorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProveedorResponse obtenerPorId(Long id) {
        return proveedorRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new NotFoundException("No existe el proveedor con id: " + id)
                );
    }

    public List<ProductoResponse> obtenerProductosDelProveedor(Long proveedorId) {

        proveedorRepository.findById(proveedorId)
                .orElseThrow(() ->
                        new NotFoundException("No existe el proveedor con id: " + proveedorId)
                );

        return productoClient.obtenerProductosPorProveedor(proveedorId);
    }

    public ProveedorResponse guardar(ProveedorRequest request) {
        ProveedorModel proveedor = new ProveedorModel();
        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setRut(request.getRut());

        ProveedorModel guardado = proveedorRepository.save(proveedor);
        return toResponse(guardado);
    }

    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
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
        if (!proveedorRepository.existsById(id)) {
            throw new NotFoundException("No existe el proveedor con id: " + id);
        }
        proveedorRepository.deleteById(id);
    }

    private ProveedorResponse toResponse(ProveedorModel model) {
        return ProveedorResponse.builder()
                .id(model.getId())
                .nombre(model.getNombre())
                .contacto(model.getContacto())
                .rut(model.getRut())
                .build();
    }
}