package com.almacen.pedidos_service;

import com.almacen.pedidos_service.controller.PedidoController;
import com.almacen.pedidos_service.dtos.request.PedidoItem;
import com.almacen.pedidos_service.dtos.request.PedidosRequest;
import com.almacen.pedidos_service.dtos.response.PedidosResponse;
import com.almacen.pedidos_service.security.JwtAuthenticationFilter;
import com.almacen.pedidos_service.security.JwtService;
import com.almacen.pedidos_service.service.PedidoService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class PedidosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private PedidosResponse response;
    private PedidosRequest request;

    @BeforeEach
    void setUp() {
        response = crearPedidosResponse();
        request = crearPedidosRequest();
    }

    @Test
    public void testObtenerTodos() throws Exception {
        when(pedidoService.obtenerTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].proveedorId").value(1))
                .andExpect(jsonPath("$[0].clienteId").value(1));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(pedidoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.proveedorId").value(1))
                .andExpect(jsonPath("$.clienteId").value(1));
    }

    @Test
    public void testObtenerPorCliente() throws Exception {
        when(pedidoService.obtenerPorCliente(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/pedidos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    public void testObtenerPorEstado() throws Exception {
        when(pedidoService.obtenerPorEstado("PENDIENTE")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/pedidos/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void testCambiarEstado() throws Exception {
        PedidosResponse actualizado = crearPedidosResponse();
        actualizado.setEstado("APROBADO");

        when(pedidoService.cambiarEstado(1L, "APROBADO")).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/pedidos/1/estado/APROBADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    public void testGuardar() throws Exception {
        when(pedidoService.guardar(any(PedidosRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.proveedorId").value(1))
                .andExpect(jsonPath("$.clienteId").value(1));
    }

    @Test
    public void testActualizar() throws Exception {
        when(pedidoService.actualizar(eq(1L), any(PedidosRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    public void testEliminar() throws Exception {
        doNothing().when(pedidoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pedido eliminado"));

        verify(pedidoService, times(1)).eliminar(1L);
    }

    private PedidosResponse crearPedidosResponse() {
        return PedidosResponse.builder()
                .id(1L)
                .fechaPedido(LocalDate.of(2026, 1, 1))
                .estado("PENDIENTE")
                .proveedorId(1L)
                .clienteId(1L)
                .productos(List.of())
                .build();
    }

    private PedidosRequest crearPedidosRequest() {
        PedidoItem item = new PedidoItem();
        item.setProductoId(1L);
        item.setCantidad(2);

        PedidosRequest request = new PedidosRequest();
        request.setFechaPedido(LocalDate.of(2026, 1, 1));
        request.setEstado("PENDIENTE");
        request.setProveedorId(1L);
        request.setClienteId(1L);
        request.setItems(List.of(item));

        return request;
    }
}