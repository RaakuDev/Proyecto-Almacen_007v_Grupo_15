package com.almacen.usuarios;

import com.almacen.usuarios.controllers.UsuarioController;
import com.almacen.usuarios.dtos.request.UsuarioRequest;
import com.almacen.usuarios.dtos.response.UsuarioResponse;
import com.almacen.usuarios.enums.Rol;
import com.almacen.usuarios.security.JwtAuthenticationFilter;
import com.almacen.usuarios.security.JwtService;
import com.almacen.usuarios.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UsuariosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UsuarioResponse usuarioResponse;
    private UsuarioRequest usuarioRequest;

    @BeforeEach
    void setUp() {
        usuarioResponse = crearUsuarioResponse();
        usuarioRequest = crearUsuarioRequest();
    }

    @Test
    public void testObtenerTodos() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(List.of(usuarioResponse));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].rol").value("ADMIN"))
                .andExpect(jsonPath("$[0].estado").value(true));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(usuarioResponse);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("juan.perez@ejemplo.com"));
    }

    @Test
    public void testObtenerPorUsername() throws Exception {
        when(usuarioService.obtenerPorUsername("admin")).thenReturn(usuarioResponse);

        mockMvc.perform(get("/api/v1/usuarios/username/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    public void testGuardar() throws Exception {
        when(usuarioService.guardar(any(UsuarioRequest.class))).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    public void testActualizar() throws Exception {
        when(usuarioService.actualizar(eq(1L), any(UsuarioRequest.class))).thenReturn(usuarioResponse);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    @Test
    public void testCambiarEstado() throws Exception {
        UsuarioResponse actualizado = crearUsuarioResponse();

        when(usuarioService.cambiarEstado(1L, false)).thenReturn(actualizado);

        mockMvc.perform(patch("/api/v1/usuarios/1/estado")
                        .param("estado", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void testEliminar() throws Exception {
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).eliminar(1L);
    }

    private UsuarioResponse crearUsuarioResponse() {
        return UsuarioResponse.builder()
                .id(1L)
                .username("admin")
                .nombre("Juan Pérez")
                .rut("12345678-9")
                .email("juan.perez@ejemplo.com")
                .rol(Rol.ADMIN)
                .estado(true)
                .telefono("+56912345678")
                .direccion("Av. Siempre Viva 123")
                .build();
    }

    private UsuarioRequest crearUsuarioRequest() {
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("admin");
        request.setPassword("Passw0rd!23");
        request.setNombre("Juan Pérez");
        request.setRut("12345678-9");
        request.setEmail("juan.perez@ejemplo.com");
        request.setRol("ADMIN");
        request.setTelefono("+56912345678");
        request.setDireccion("Av. Siempre Viva 123");

        return request;
    }
}