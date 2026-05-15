package com.almacen.clientes.services;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.exceptions.NotFoundException;
import com.almacen.clientes.models.ClientesModel;
import com.almacen.clientes.repositories.ClientesRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;

    public ClientesService(ClientesRepository clientesRepository) {
        this.clientesRepository = clientesRepository;
    }

    // Obtener todos
    public List<ClientesResponse> obtenerTodos() {

        log.info("Obteniendo todos los clientes");

        return clientesRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Obtener por ID
    public ClientesResponse obtenerPorId(Long id) {

        log.info("Buscando cliente con ID: {}", id);

        return clientesRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {

                    log.error("No existe el cliente con ID: {}", id);

                    return new NotFoundException(
                            "No existe el cliente con id: " + id
                    );
                });
    }

    // Crear cliente
    public ClientesResponse guardar(ClientesRequest request) {

        log.info("Guardando nuevo cliente con rut: {}", request.getRut());

        ClientesModel cliente = new ClientesModel();

        cliente.setNombre(request.getNombre());
        cliente.setRut(request.getRut());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());

        ClientesModel guardado = clientesRepository.save(cliente);

        log.info("Cliente guardado correctamente con ID: {}", guardado.getId());

        return toResponse(guardado);
    }

    // Actualizar cliente
    public ClientesResponse actualizar(Long id, ClientesRequest request) {

        log.info("Actualizando cliente con ID: {}", id);

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() -> {

                    log.error("No existe el cliente con ID: {}", id);

                    return new NotFoundException(
                            "No existe el cliente con id: " + id
                    );
                });

        cliente.setNombre(request.getNombre());
        cliente.setRut(request.getRut());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());

        ClientesModel actualizado = clientesRepository.save(cliente);

        log.info("Cliente actualizado correctamente con ID: {}", actualizado.getId());

        return toResponse(actualizado);
    }

    // Eliminar cliente
    public void eliminar(Long id) {

        log.info("Eliminando cliente con ID: {}", id);

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() -> {

                    log.error("No existe el cliente con ID: {}", id);

                    return new NotFoundException(
                            "No existe el cliente con id: " + id
                    );
                });

        clientesRepository.delete(cliente);

        log.info("Cliente eliminado correctamente con ID: {}", id);
    }

    // Mapper
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