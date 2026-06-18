package com.almacen.clientes.services;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.dtos.response.PedidoResponse;
import com.almacen.clientes.exceptions.NotFoundException;
import com.almacen.clientes.models.ClientesModel;
import com.almacen.clientes.repositories.ClientesRepository;
import com.almacen.clientes.webclient.PedidoClient;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;
    private final PedidoClient pedidoClient;

    public ClientesService(ClientesRepository clientesRepository, PedidoClient pedidoClient) {
        this.clientesRepository = clientesRepository;
        this.pedidoClient = pedidoClient;
    }

    public List<ClientesResponse> obtenerTodos() {
        log.info("Obteniendo todos los clientes");

        List<ClientesModel> clientes = clientesRepository.findAll();

        if (clientes.isEmpty()) {
            log.error("No existen clientes registrados");
            throw new NotFoundException("No existen clientes registrados");
        }

        return clientes.stream()
                .map(this::toResponse)
                .toList();
    }

    public ClientesResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id del cliente no puede ser nulo");
            throw new NotFoundException("El id del cliente no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        log.info("Buscando cliente con ID: {}", id);

        return clientesRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("No existe el cliente con ID: {}", id);
                    return new NotFoundException("No existe el cliente con id: " + id);
                });
    }

    public List<PedidoResponse> obtenerPedidosDelCliente(Long clienteId) {

        if (clienteId == null) {
            log.error("El id del cliente no puede ser nulo");
            throw new NotFoundException("El id del cliente no puede ser nulo");
        }

        if (clienteId <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        log.info("Buscando pedidos del cliente con ID: {}", clienteId);

        clientesRepository.findById(clienteId)
                .orElseThrow(() -> {
                    log.error("No existe el cliente con ID: {}", clienteId);
                    return new NotFoundException("No existe el cliente con id: " + clienteId);
                });

        List<PedidoResponse> pedidos = pedidoClient.obtenerPedidosPorCliente(clienteId);

        if (pedidos == null || pedidos.isEmpty()) {
            log.error("El cliente con ID {} no tiene pedidos registrados", clienteId);
            throw new NotFoundException("El cliente no tiene pedidos registrados");
        }

        return pedidos;
    }

    public ClientesResponse guardar(ClientesRequest request) {

        if (request == null) {
            log.error("Los datos del cliente no pueden ser nulos");
            throw new NotFoundException("Los datos del cliente no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre del cliente es obligatorio");
            throw new NotFoundException("El nombre del cliente es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            log.error("El rut del cliente es obligatorio");
            throw new NotFoundException("El rut del cliente es obligatorio");
        }

        if (request.getDireccion() == null || request.getDireccion().trim().isEmpty()) {
            log.error("La dirección del cliente es obligatoria");
            throw new NotFoundException("La dirección del cliente es obligatoria");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            log.error("El teléfono del cliente es obligatorio");
            throw new NotFoundException("El teléfono del cliente es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("El email del cliente es obligatorio");
            throw new NotFoundException("El email del cliente es obligatorio");
        }

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

    public ClientesResponse actualizar(Long id, ClientesRequest request) {

        if (id == null) {
            log.error("El id del cliente no puede ser nulo");
            throw new NotFoundException("El id del cliente no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos del cliente no pueden ser nulos");
            throw new NotFoundException("Los datos del cliente no pueden ser nulos");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre del cliente es obligatorio");
            throw new NotFoundException("El nombre del cliente es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            log.error("El rut del cliente es obligatorio");
            throw new NotFoundException("El rut del cliente es obligatorio");
        }

        if (request.getDireccion() == null || request.getDireccion().trim().isEmpty()) {
            log.error("La dirección del cliente es obligatoria");
            throw new NotFoundException("La dirección del cliente es obligatoria");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            log.error("El teléfono del cliente es obligatorio");
            throw new NotFoundException("El teléfono del cliente es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("El email del cliente es obligatorio");
            throw new NotFoundException("El email del cliente es obligatorio");
        }

        log.info("Actualizando cliente con ID: {}", id);

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el cliente con ID: {}", id);
                    return new NotFoundException("No existe el cliente con id: " + id);
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

    public void eliminar(Long id) {

        if (id == null) {
            log.error("El id del cliente no puede ser nulo");
            throw new NotFoundException("El id del cliente no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del cliente debe ser mayor a cero");
            throw new NotFoundException("El id del cliente debe ser mayor a cero");
        }

        log.info("Eliminando cliente con ID: {}", id);

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el cliente con ID: {}", id);
                    return new NotFoundException("No existe el cliente con id: " + id);
                });

        clientesRepository.delete(cliente);

        log.info("Cliente eliminado correctamente con ID: {}", id);
    }

    private ClientesResponse toResponse(ClientesModel cliente) {

        if (cliente == null) {
            log.error("El cliente no puede ser nulo");
            throw new NotFoundException("El cliente no puede ser nulo");
        }

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