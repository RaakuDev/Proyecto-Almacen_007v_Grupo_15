package com.almacen.usuarios.services;

import com.almacen.usuarios.dtos.request.UsuarioRequest;
import com.almacen.usuarios.dtos.response.UsuarioResponse;
import com.almacen.usuarios.enums.Rol;
import com.almacen.usuarios.exceptions.NotFoundException;
import com.almacen.usuarios.models.UsuarioModel;
import com.almacen.usuarios.repositories.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todos los usuarios
    public List<UsuarioResponse> obtenerTodos() {
        log.info("Obteniendo todos los usuarios");

        List<UsuarioModel> usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            log.error("No existen usuarios registrados");
            throw new NotFoundException("No existen usuarios registrados");
        }

        return usuarios.stream()
                .map(this::toResponse)
                .toList();
    }

    // Obtener usuario por ID
    public UsuarioResponse obtenerPorId(Long id) {

        if (id == null) {
            log.error("El id del usuario no puede ser nulo");
            throw new NotFoundException("El id del usuario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del usuario debe ser mayor a cero");
            throw new NotFoundException("El id del usuario debe ser mayor a cero");
        }

        log.info("Buscando usuario con id: {}", id);

        return usuarioRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("No existe el usuario con id: {}", id);
                    return new NotFoundException("No existe el usuario con id: " + id);
                });
    }

    // Obtener usuario por username
    public UsuarioResponse obtenerPorUsername(String username) {

        if (username == null || username.trim().isEmpty()) {
            log.error("El username no puede ser nulo o vacío");
            throw new NotFoundException("El username no puede ser nulo o vacío");
        }

        log.info("Buscando usuario con username: {}", username);

        return usuarioRepository.findByUsername(username)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.error("No existe el usuario con username: {}", username);
                    return new NotFoundException("No existe el usuario con username: " + username);
                });
    }

    // Crear usuario
    public UsuarioResponse guardar(UsuarioRequest request) {

        if (request == null) {
            log.error("Los datos del usuario no pueden ser nulos");
            throw new NotFoundException("Los datos del usuario no pueden ser nulos");
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.error("El username es obligatorio");
            throw new NotFoundException("El username es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("El password es obligatorio");
            throw new NotFoundException("El password es obligatorio");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre es obligatorio");
            throw new NotFoundException("El nombre es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            log.error("El rut es obligatorio");
            throw new NotFoundException("El rut es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("El email es obligatorio");
            throw new NotFoundException("El email es obligatorio");
        }

        if (request.getRol() == null || request.getRol().trim().isEmpty()) {
            log.error("El rol es obligatorio");
            throw new NotFoundException("El rol es obligatorio");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            log.error("El teléfono es obligatorio");
            throw new NotFoundException("El teléfono es obligatorio");
        }

        if (request.getDireccion() == null || request.getDireccion().trim().isEmpty()) {
            log.error("La dirección es obligatoria");
            throw new NotFoundException("La dirección es obligatoria");
        }

        log.info("Guardando nuevo usuario: {}", request.getUsername());

        Rol rolConvertido = convertirRol(request.getRol());

        UsuarioModel usuario = UsuarioModel.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .nombre(request.getNombre())
                .rut(request.getRut())
                .email(request.getEmail())
                .rol(rolConvertido)
                .estado(true)
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .build();

        UsuarioModel guardado = usuarioRepository.save(usuario);

        log.info("Usuario guardado con id: {}", guardado.getId());

        return toResponse(guardado);
    }

    // Actualizar usuario
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {

        if (id == null) {
            log.error("El id del usuario no puede ser nulo");
            throw new NotFoundException("El id del usuario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del usuario debe ser mayor a cero");
            throw new NotFoundException("El id del usuario debe ser mayor a cero");
        }

        if (request == null) {
            log.error("Los datos del usuario no pueden ser nulos");
            throw new NotFoundException("Los datos del usuario no pueden ser nulos");
        }

        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.error("El username es obligatorio");
            throw new NotFoundException("El username es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.error("El password es obligatorio");
            throw new NotFoundException("El password es obligatorio");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            log.error("El nombre es obligatorio");
            throw new NotFoundException("El nombre es obligatorio");
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            log.error("El rut es obligatorio");
            throw new NotFoundException("El rut es obligatorio");
        }

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.error("El email es obligatorio");
            throw new NotFoundException("El email es obligatorio");
        }

        if (request.getRol() == null || request.getRol().trim().isEmpty()) {
            log.error("El rol es obligatorio");
            throw new NotFoundException("El rol es obligatorio");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            log.error("El teléfono es obligatorio");
            throw new NotFoundException("El teléfono es obligatorio");
        }

        if (request.getDireccion() == null || request.getDireccion().trim().isEmpty()) {
            log.error("La dirección es obligatoria");
            throw new NotFoundException("La dirección es obligatoria");
        }

        log.info("Actualizando usuario con id: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el usuario con id: {}", id);
                    return new NotFoundException("No existe el usuario con id: " + id);
                });

        Rol rolConvertido = convertirRol(request.getRol());

        usuario.setUsername(request.getUsername());
        usuario.setPassword(request.getPassword());
        usuario.setNombre(request.getNombre());
        usuario.setRut(request.getRut());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rolConvertido);
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());

        UsuarioModel actualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado con id: {}", actualizado.getId());

        return toResponse(actualizado);
    }

    // Cambiar estado del usuario
    public UsuarioResponse cambiarEstado(Long id, boolean estado) {

        if (id == null) {
            log.error("El id del usuario no puede ser nulo");
            throw new NotFoundException("El id del usuario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del usuario debe ser mayor a cero");
            throw new NotFoundException("El id del usuario debe ser mayor a cero");
        }

        log.info("Cambiando estado del usuario con id: {} a {}", id, estado);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el usuario con id: {}", id);
                    return new NotFoundException("No existe el usuario con id: " + id);
                });

        usuario.setEstado(estado);

        UsuarioModel actualizado = usuarioRepository.save(usuario);

        log.info("Estado del usuario actualizado con id: {}", actualizado.getId());

        return toResponse(actualizado);
    }

    // Eliminar usuario
    public void eliminar(Long id) {

        if (id == null) {
            log.error("El id del usuario no puede ser nulo");
            throw new NotFoundException("El id del usuario no puede ser nulo");
        }

        if (id <= 0) {
            log.error("El id del usuario debe ser mayor a cero");
            throw new NotFoundException("El id del usuario debe ser mayor a cero");
        }

        log.info("Eliminando usuario con id: {}", id);

        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No existe el usuario con id: {}", id);
                    return new NotFoundException("No existe el usuario con id: " + id);
                });

        usuarioRepository.delete(usuario);

        log.info("Usuario eliminado con id: {}", id);
    }

    // Convertir texto a Enum Rol
    private Rol convertirRol(String rol) {

        if (rol == null || rol.trim().isEmpty()) {
            log.error("El rol no puede ser nulo o vacío");
            throw new NotFoundException("El rol no puede ser nulo o vacío");
        }

        try {
            return Rol.valueOf(rol.toUpperCase());
        } catch (Exception e) {
            log.error("Rol inválido recibido: {}", rol);
            throw new NotFoundException("Rol inválido. Debe ser ADMIN, CAJERO o SUPERVISOR");
        }
    }

    // Mapper privado
    private UsuarioResponse toResponse(UsuarioModel usuario) {

        if (usuario == null) {
            log.error("El usuario no puede ser nulo");
            throw new NotFoundException("El usuario no puede ser nulo");
        }

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .rut(usuario.getRut())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .estado(usuario.isEstado())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .build();
    }
}
