package com.almacen.DetalleVentas;

import com.almacen.DetalleVentas.controllers.DetalleController;
import com.almacen.DetalleVentas.dtos.response.DetalleResponse;
import com.almacen.DetalleVentas.security.JwtAuthenticationFilter;
import com.almacen.DetalleVentas.security.JwtService;
import com.almacen.DetalleVentas.services.DetalleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DetalleController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class DetalleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetalleService detalleService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testObtenerTodos_retorna200() throws Exception {
        DetalleResponse response = DetalleResponse.builder()
                .idDetalle(1L)
                .ventaId(1L)
                .productoId(1L)
                .nombreProducto("Coca Cola")
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5000))
                .subTotal(BigDecimal.valueOf(10000))
                .build();

        when(detalleService.obtenerTodos())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/detalles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProducto")
                        .value("Coca Cola"))
                .andExpect(jsonPath("$[0].cantidad")
                        .value(2))
                .andExpect(jsonPath("$[0].subTotal")
                        .value(10000));
    }

    @Test
    public void testObtenerPorId_retorna200() throws Exception {
        DetalleResponse response = DetalleResponse.builder()
                .idDetalle(1L)
                .ventaId(1L)
                .productoId(1L)
                .nombreProducto("Coca Cola")
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5000))
                .subTotal(BigDecimal.valueOf(10000))
                .build();

        when(detalleService.obtenerPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/detalles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreProducto")
                        .value("Coca Cola"))
                .andExpect(jsonPath("$.cantidad")
                        .value(2));
    }

    @Test
    public void testObtenerPorVentaId_retorna200() throws Exception {
        DetalleResponse response = DetalleResponse.builder()
                .idDetalle(1L)
                .ventaId(1L)
                .productoId(1L)
                .nombreProducto("Coca Cola")
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5000))
                .subTotal(BigDecimal.valueOf(10000))
                .build();

        when(detalleService.obtenerPorVentaId(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/detalles/venta/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ventaId")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreProducto")
                        .value("Coca Cola"));
    }

    @Test
    public void testCrear_retorna201() throws Exception {
        DetalleResponse response = DetalleResponse.builder()
                .idDetalle(1L)
                .ventaId(1L)
                .productoId(1L)
                .nombreProducto("Coca Cola")
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(5000))
                .subTotal(BigDecimal.valueOf(10000))
                .build();

        when(detalleService.guardar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/detalles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "ventaId": 1,
                            "productoId": 1,
                            "cantidad": 2
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDetalle")
                        .value(1))
                .andExpect(jsonPath("$.nombreProducto")
                        .value("Coca Cola"))
                .andExpect(jsonPath("$.subTotal")
                        .value(10000));
    }

    @Test
    public void testActualizar_retorna200() throws Exception {
        DetalleResponse response = DetalleResponse.builder()
                .idDetalle(1L)
                .ventaId(1L)
                .productoId(1L)
                .nombreProducto("Coca Cola")
                .cantidad(3)
                .precioUnitario(BigDecimal.valueOf(5000))
                .subTotal(BigDecimal.valueOf(15000))
                .build();

        when(detalleService.actualizar(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/detalles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "ventaId": 1,
                            "productoId": 1,
                            "cantidad": 3
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad")
                        .value(3))
                .andExpect(jsonPath("$.subTotal")
                        .value(15000));
    }

    @Test
    public void testEliminar_retorna204() throws Exception {
        doNothing().when(detalleService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/detalles/1"))
                .andExpect(status().isNoContent());
    }
}
