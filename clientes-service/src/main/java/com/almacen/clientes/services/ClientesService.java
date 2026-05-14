package com.almacen.clientes.services;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.exceptions.NotFoundException;
import com.almacen.clientes.models.ClientesModel;
import com.almacen.clientes.repositories.ClientesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;

    public ClientesService(ClientesRepository clientesRepository) {
        this.clientesRepository = clientesRepository;
    }

    // 🔹 Obtener todos
    public List<ClientesResponse> obtenerTodos() {
        return clientesRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 🔹 Obtener por ID
    public ClientesResponse obtenerPorId(Long id) {
        return clientesRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() ->
                        new NotFoundException("No existe el cliente con id: " + id)
                );
    }

    // 🔹 Crear cliente
    public ClientesResponse guardar(ClientesRequest request) {

        ClientesModel cliente = new ClientesModel();
        cliente.setNombre(request.getNombre());
        cliente.setRut(request.getRut());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());

        ClientesModel guardado = clientesRepository.save(cliente);

        return toResponse(guardado);
    }

    // 🔹 Actualizar cliente
    public ClientesResponse actualizar(Long id, ClientesRequest request) {

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("No existe el cliente con id: " + id)
                );

        cliente.setNombre(request.getNombre());
        cliente.setRut(request.getRut());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());

        ClientesModel actualizado = clientesRepository.save(cliente);

        return toResponse(actualizado);
    }

    // 🔹 Eliminar cliente
    public void eliminar(Long id) {
        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("No existe el cliente con id: " + id)
                );

        clientesRepository.delete(cliente);
    }

    // 🔁 Mapper
    private ClientesResponse toResponse(ClientesModel cliente) {
        return ClientesResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .rut(cliente.getRut())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .build();
    }
}