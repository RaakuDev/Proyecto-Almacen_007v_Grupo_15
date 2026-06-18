package com.almacen.proveedores_service;

import com.almacen.proveedores_service.controllers.ProveedorController;
import com.almacen.proveedores_service.dtos.request.ProveedorRequest;
import com.almacen.proveedores_service.dtos.response.ProductoResponse;
import com.almacen.proveedores_service.dtos.response.ProveedorResponse;
import com.almacen.proveedores_service.security.JwtAuthenticationFilter;
import com.almacen.proveedores_service.security.JwtService;
import com.almacen.proveedores_service.services.ProveedorService;
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

@WebMvcTest(ProveedorController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ProveedoresControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService proveedorService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ProveedorResponse proveedorResponse;
    private ProveedorRequest proveedorRequest;
    private ProductoResponse productoResponse;

    @BeforeEach
    void setUp() {
        proveedorResponse = crearProveedorResponse();
        proveedorRequest = crearProveedorRequest();
        productoResponse = crearProductoResponse();
    }

    @Test
    public void testObtenerTodos() throws Exception {
        when(proveedorService.obtenerTodos()).thenReturn(List.of(proveedorResponse));

        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Distribuidora Central"))
                .andExpect(jsonPath("$[0].contacto").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].rut").value("76543210-9"));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(proveedorService.obtenerPorId(1L)).thenReturn(proveedorResponse);

        mockMvc.perform(get("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Distribuidora Central"))
                .andExpect(jsonPath("$.contacto").value("Juan Pérez"))
                .andExpect(jsonPath("$.rut").value("76543210-9"));
    }

    @Test
    public void testObtenerProductosDelProveedor() throws Exception {
        when(proveedorService.obtenerProductosDelProveedor(1L)).thenReturn(List.of(productoResponse));

        mockMvc.perform(get("/api/v1/proveedores/1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Arroz 1kg"))
                .andExpect(jsonPath("$[0].proveedorId").value(1));
    }

    @Test
    public void testGuardar() throws Exception {
        when(proveedorService.guardar(any(ProveedorRequest.class))).thenReturn(proveedorResponse);

        mockMvc.perform(post("/api/v1/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Distribuidora Central"))
                .andExpect(jsonPath("$.contacto").value("Juan Pérez"))
                .andExpect(jsonPath("$.rut").value("76543210-9"));
    }

    @Test
    public void testActualizar() throws Exception {
        when(proveedorService.actualizar(eq(1L), any(ProveedorRequest.class))).thenReturn(proveedorResponse);

        mockMvc.perform(put("/api/v1/proveedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Distribuidora Central"));
    }

    @Test
    public void testEliminar() throws Exception {
        doNothing().when(proveedorService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Proveedor eliminado"));

        verify(proveedorService, times(1)).eliminar(1L);
    }

    private ProveedorResponse crearProveedorResponse() {
        return ProveedorResponse.builder()
                .id(1L)
                .nombre("Distribuidora Central")
                .contacto("Juan Pérez")
                .rut("76543210-9")
                .build();
    }

    private ProveedorRequest crearProveedorRequest() {
        ProveedorRequest request = new ProveedorRequest();
        request.setNombre("Distribuidora Central");
        request.setContacto("Juan Pérez");
        request.setRut("76543210-9");

        return request;
    }

    private ProductoResponse crearProductoResponse() {
        ProductoResponse response = new ProductoResponse();
        response.setId(1L);
        response.setNombre("Arroz 1kg");
        response.setPrecio(1500.0);
        response.setStock(20);
        response.setCategoriaId(1L);
        response.setProveedorId(1L);

        return response;
    }
}