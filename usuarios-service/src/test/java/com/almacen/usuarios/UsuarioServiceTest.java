package com.almacen.usuarios;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.usuarios.dtos.request.UsuarioRequest;
import com.almacen.usuarios.dtos.response.UsuarioResponse;
import com.almacen.usuarios.enums.Rol;
import com.almacen.usuarios.models.UsuarioModel;
import com.almacen.usuarios.repositories.UsuarioRepository;
import com.almacen.usuarios.services.UsuarioService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testObtenerTodos() {
        UsuarioModel usuario = crearUsuarioModel();

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponse> usuarios = usuarioService.obtenerTodos();

        assertNotNull(usuarios);
        assertEquals(1, usuarios.size());
        assertEquals("alejandro", usuarios.get(0).getUsername());
        assertEquals(Rol.ADMIN, usuarios.get(0).getRol());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(crearUsuarioModel()));

        UsuarioResponse response = usuarioService.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("alejandro", response.getUsername());
    }

    @Test
    public void testObtenerPorUsername() {
        String username = "alejandro";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(crearUsuarioModel()));

        UsuarioResponse response = usuarioService.obtenerPorUsername(username);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals("Alejandro Meza", response.getNombre());
    }

    @Test
    public void testGuardar() {
        UsuarioRequest request = crearUsuarioRequest();

        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(crearUsuarioModel());

        UsuarioResponse response = usuarioService.guardar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("alejandro", response.getUsername());
        assertEquals(Rol.ADMIN, response.getRol());
        assertTrue(response.isEstado());
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        UsuarioRequest request = crearUsuarioRequest();
        request.setNombre("Alejandro Actualizado");

        UsuarioModel existente = crearUsuarioModel();

        UsuarioModel actualizado = crearUsuarioModel();
        actualizado.setNombre("Alejandro Actualizado");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(actualizado);

        UsuarioResponse response = usuarioService.actualizar(id, request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Alejandro Actualizado", response.getNombre());
    }

    @Test
    public void testCambiarEstado() {
        Long id = 1L;

        UsuarioModel existente = crearUsuarioModel();

        UsuarioModel actualizado = crearUsuarioModel();
        actualizado.setEstado(false);

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(actualizado);

        UsuarioResponse response = usuarioService.cambiarEstado(id, false);

        assertNotNull(response);
        assertFalse(response.isEstado());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        UsuarioModel usuario = crearUsuarioModel();

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).delete(usuario);

        usuarioService.eliminar(id);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    private UsuarioModel crearUsuarioModel() {
        return UsuarioModel.builder()
                .id(1L)
                .username("alejandro")
                .password("123456")
                .nombre("Alejandro Meza")
                .rut("11111111-1")
                .email("alejandro@gmail.com")
                .rol(Rol.ADMIN)
                .estado(true)
                .telefono("+56912345678")
                .direccion("Maipú")
                .build();
    }

    private UsuarioRequest crearUsuarioRequest() {
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("alejandro");
        request.setPassword("123456");
        request.setNombre("Alejandro Meza");
        request.setRut("11111111-1");
        request.setEmail("alejandro@gmail.com");
        request.setRol("ADMIN");
        request.setTelefono("+56912345678");
        request.setDireccion("Maipú");
        return request;
    }
}