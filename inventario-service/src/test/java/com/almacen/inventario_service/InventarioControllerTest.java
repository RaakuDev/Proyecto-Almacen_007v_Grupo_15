package com.almacen.inventario_service;

import com.almacen.inventario_service.controller.InventarioController;
import com.almacen.inventario_service.dtos.response.InventarioResponse;
import com.almacen.inventario_service.dtos.response.ProductoResponse;
import com.almacen.inventario_service.security.JwtAuthenticationFilter;
import com.almacen.inventario_service.security.JwtService;
import com.almacen.inventario_service.services.InventarioService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService inventarioService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private InventarioResponse crearResponse() {
        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz 1kg");
        producto.setPrecio(1500L);
        producto.setCategoriaId(1L);

        return InventarioResponse.builder()
                .id(1L)
                .stockActual(50)
                .stockMinimo(10)
                .productoId(1L)
                .producto(producto)
                .build();
    }

    @Test
    void testObtenerTodos_retorna200() throws Exception {
        when(inventarioService.obtenerTodos())
                .thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stockActual").value(50))
                .andExpect(jsonPath("$[0].producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testObtenerPorId_retorna200() throws Exception {
        when(inventarioService.obtenerPorId(1L))
                .thenReturn(crearResponse());

        mockMvc.perform(get("/api/v1/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockActual").value(50))
                .andExpect(jsonPath("$.producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testObtenerPorProductoId_retorna200() throws Exception {
        when(inventarioService.obtenerPorProductoId(1L))
                .thenReturn(crearResponse());

        mockMvc.perform(get("/api/v1/inventario/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testObtenerPorCategoria_retorna200() throws Exception {
        when(inventarioService.obtenerPorCategoria(1L))
                .thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/inventario/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].producto.categoriaId")
                        .value(1));
    }

    @Test
    void testObtenerBajoStock_retorna200() throws Exception {
        when(inventarioService.obtenerBajoStock())
                .thenReturn(List.of(crearResponse()));

        mockMvc.perform(get("/api/v1/inventario/bajo-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testCrear_retorna201() throws Exception {
        when(inventarioService.guardar(any()))
                .thenReturn(crearResponse());

        mockMvc.perform(post("/api/v1/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "stockActual": 50,
                            "stockMinimo": 10,
                            "productoId": 1
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockActual").value(50))
                .andExpect(jsonPath("$.producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testActualizar_retorna200() throws Exception {
        when(inventarioService.actualizar(anyLong(), any()))
                .thenReturn(crearResponse());

        mockMvc.perform(put("/api/v1/inventario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "stockActual": 50,
                            "stockMinimo": 10,
                            "productoId": 1
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockActual").value(50));
    }

    @Test
    void testAumentarStock_retorna200() throws Exception {
        when(inventarioService.aumentarStock(anyLong(), anyInt()))
                .thenReturn(crearResponse());

        mockMvc.perform(
                        put("/api/v1/inventario/producto/1/aumentar/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testDescontarStock_retorna200() throws Exception {
        when(inventarioService.descontarStock(anyLong(), anyInt()))
                .thenReturn(crearResponse());

        mockMvc.perform(
                        put("/api/v1/inventario/producto/1/descontar/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto.nombre")
                        .value("Arroz 1kg"));
    }

    @Test
    void testEliminar_retorna200() throws Exception {
        doNothing().when(inventarioService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/inventario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value("Inventario eliminado"));
    }
}