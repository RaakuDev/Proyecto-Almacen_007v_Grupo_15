package com.almacen.ventas;

import com.almacen.ventas.Services.VentasServices;
import com.almacen.ventas.Security.JwtAuthenticationFilter;
import com.almacen.ventas.Security.JwtService;
import com.almacen.ventas.controllers.VentasController;
import com.almacen.ventas.dtos.request.ItemVentaRequest;
import com.almacen.ventas.dtos.request.VentasRequest;
import com.almacen.ventas.dtos.response.ClienteResponse;
import com.almacen.ventas.dtos.response.DetalleVentaResponse;
import com.almacen.ventas.dtos.response.EmpleadoResponse;
import com.almacen.ventas.dtos.response.VentasResponse;
import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentasController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class VentasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentasServices ventasServices;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private VentasResponse ventasResponse;
    private VentasRequest ventasRequest;

    @BeforeEach
    void setUp() {
        ventasResponse = crearVentasResponse();
        ventasRequest = crearVentasRequest();
    }

    @Test
    public void testObtenerTodas() throws Exception {
        when(ventasServices.obtenerTodas()).thenReturn(List.of(ventasResponse));

        mockMvc.perform(get("/api/v1/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVenta").value(1))
                .andExpect(jsonPath("$[0].total").value(5950))
                .andExpect(jsonPath("$[0].metodoPago").value("EFECTIVO"))
                .andExpect(jsonPath("$[0].tipoComprobante").value("BOLETA"))
                .andExpect(jsonPath("$[0].estadoVenta").value("COMPLETADA"));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(ventasServices.obtenerPorId(1L)).thenReturn(ventasResponse);

        mockMvc.perform(get("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(1))
                .andExpect(jsonPath("$.total").value(5950))
                .andExpect(jsonPath("$.cliente.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.empleado.nombre").value("Pedro González"));
    }

    @Test
    public void testGuardar() throws Exception {
        when(ventasServices.guardar(any(VentasRequest.class))).thenReturn(ventasResponse);

        mockMvc.perform(post("/api/v1/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventasRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta").value(1))
                .andExpect(jsonPath("$.total").value(5950))
                .andExpect(jsonPath("$.numeroComprobante").value("BOL-0001"));
    }

    @Test
    public void testActualizar() throws Exception {
        when(ventasServices.actualizar(eq(1L), any(VentasRequest.class))).thenReturn(ventasResponse);

        mockMvc.perform(put("/api/v1/ventas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventasRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(1))
                .andExpect(jsonPath("$.observaciones").value("Venta normal"));
    }

    @Test
    public void testObtenerPorNumeroComprobante() throws Exception {
        when(ventasServices.obtenerPorNumeroComprobante("BOL-0001")).thenReturn(ventasResponse);

        mockMvc.perform(get("/api/v1/ventas/comprobante/BOL-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroComprobante").value("BOL-0001"))
                .andExpect(jsonPath("$.idVenta").value(1));
    }

    @Test
    public void testRecalcularTotal() throws Exception {
        when(ventasServices.recalcularTotal(1L)).thenReturn(ventasResponse);

        mockMvc.perform(put("/api/v1/ventas/1/recalcular-total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(1))
                .andExpect(jsonPath("$.total").value(5950));
    }

    @Test
    public void testEliminar() throws Exception {
        doNothing().when(ventasServices).eliminar(1L);

        mockMvc.perform(delete("/api/v1/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Venta eliminada correctamente"));

        verify(ventasServices, times(1)).eliminar(1L);
    }

    private VentasResponse crearVentasResponse() {
        ClienteResponse cliente = new ClienteResponse();
        cliente.setId(1L);
        cliente.setNombre("Juan Pérez");
        cliente.setRut("12.345.678-9");
        cliente.setDireccion("Av. Siempre Viva 123");
        cliente.setTelefono("+56912345678");
        cliente.setEmail("juan.perez@ejemplo.com");

        EmpleadoResponse empleado = new EmpleadoResponse();
        empleado.setId(1L);
        empleado.setNombre("Pedro González");
        empleado.setCargo("Cajero");

        DetalleVentaResponse detalle = new DetalleVentaResponse();
        detalle.setIdDetalle(1L);
        detalle.setVentaId(1L);
        detalle.setProductoId(1L);
        detalle.setNombreProducto("Arroz 1kg");
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(BigDecimal.valueOf(2500));
        detalle.setSubTotal(BigDecimal.valueOf(5000));

        return VentasResponse.builder()
                .idVenta(1L)
                .fechaVenta(LocalDateTime.of(2026, 5, 18, 10, 15))
                .subTotal(BigDecimal.valueOf(5000))
                .descuentoTotal(BigDecimal.ZERO)
                .impuestoTotal(BigDecimal.valueOf(950))
                .total(BigDecimal.valueOf(5950))
                .metodoPago(MetodoDePago.EFECTIVO)
                .tipoComprobante(TipoComprobante.BOLETA)
                .montoPagado(BigDecimal.valueOf(6000))
                .vuelto(BigDecimal.valueOf(50))
                .estadoVenta(EstadoVentas.COMPLETADA)
                .clienteId(1L)
                .cliente(cliente)
                .empleadoId(1L)
                .empleado(empleado)
                .numeroComprobante("BOL-0001")
                .observaciones("Venta normal")
                .detalles(List.of(detalle))
                .build();
    }

    private VentasRequest crearVentasRequest() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setProductoId(1L);
        item.setCantidad(2);

        VentasRequest request = new VentasRequest();
        request.setClienteId(1L);
        request.setEmpleadoId(1L);
        request.setMetodoPago(MetodoDePago.EFECTIVO);
        request.setTipoComprobante(TipoComprobante.BOLETA);
        request.setMontoPagado(BigDecimal.valueOf(6000));
        request.setNumeroComprobante("BOL-0001");
        request.setObservaciones("Venta normal");
        request.setItems(List.of(item));
        request.setSubTotal(BigDecimal.valueOf(5000));
        request.setDescuentoTotal(BigDecimal.ZERO);
        request.setImpuestoTotal(BigDecimal.valueOf(950));
        request.setTotal(BigDecimal.valueOf(5950));
        request.setVuelto(BigDecimal.valueOf(50));

        return request;
    }
}