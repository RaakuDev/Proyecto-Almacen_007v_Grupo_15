package com.almacen.ventas;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.ventas.Services.VentasServices;
import com.almacen.ventas.dtos.request.ItemVentaRequest;
import com.almacen.ventas.dtos.request.VentasRequest;
import com.almacen.ventas.dtos.response.ClienteResponse;
import com.almacen.ventas.dtos.response.DetalleVentaResponse;
import com.almacen.ventas.dtos.response.EmpleadoResponse;
import com.almacen.ventas.dtos.response.VentasResponse;
import com.almacen.ventas.enums.EstadoVentas;
import com.almacen.ventas.enums.MetodoDePago;
import com.almacen.ventas.enums.TipoComprobante;
import com.almacen.ventas.models.VentasModel;
import com.almacen.ventas.repositories.VentasRepository;
import com.almacen.ventas.webclient.ClienteClient;
import com.almacen.ventas.webclient.DetalleVentaClient;
import com.almacen.ventas.webclient.EmpleadoClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class VentasServiceTest {

    @Autowired
    private VentasServices ventasServices;

    @MockBean
    private VentasRepository ventasRepository;

    @MockBean
    private ClienteClient clienteClient;

    @MockBean
    private DetalleVentaClient detalleVentaClient;

    @MockBean
    private EmpleadoClient empleadoClient;

    @Test
    public void testObtenerTodas() {
        VentasModel venta = crearVentaModel();

        when(ventasRepository.findAll()).thenReturn(List.of(venta));
        when(detalleVentaClient.obtenerDetallesPorVenta(1L)).thenReturn(List.of(crearDetalleVentaResponse()));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(empleadoClient.obtenerEmpleadoPorId(1L)).thenReturn(crearEmpleadoResponse());

        List<VentasResponse> ventas = ventasServices.obtenerTodas();

        assertNotNull(ventas);
        assertEquals(1, ventas.size());
        assertEquals(1L, ventas.get(0).getIdVenta());
        assertEquals(EstadoVentas.COMPLETADA, ventas.get(0).getEstadoVenta());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        when(ventasRepository.findById(id)).thenReturn(Optional.of(crearVentaModel()));
        when(detalleVentaClient.obtenerDetallesPorVenta(id)).thenReturn(List.of(crearDetalleVentaResponse()));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(empleadoClient.obtenerEmpleadoPorId(1L)).thenReturn(crearEmpleadoResponse());

        VentasResponse response = ventasServices.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getIdVenta());
        assertEquals(BigDecimal.valueOf(11900), response.getTotal());
    }

    @Test
    public void testObtenerPorNumeroComprobante() {
        String numero = "B001";

        when(ventasRepository.findByNumeroComprobante(numero)).thenReturn(Optional.of(crearVentaModel()));
        when(detalleVentaClient.obtenerDetallesPorVenta(1L)).thenReturn(List.of(crearDetalleVentaResponse()));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(empleadoClient.obtenerEmpleadoPorId(1L)).thenReturn(crearEmpleadoResponse());

        VentasResponse response = ventasServices.obtenerPorNumeroComprobante(numero);

        assertNotNull(response);
        assertEquals(numero, response.getNumeroComprobante());
    }

    @Test
    public void testGuardar() {
        VentasRequest request = crearVentasRequest();

        VentasModel guardadaInicial = crearVentaModel();
        guardadaInicial.setSubTotal(BigDecimal.ZERO);
        guardadaInicial.setImpuestoTotal(BigDecimal.ZERO);
        guardadaInicial.setTotal(BigDecimal.ZERO);
        guardadaInicial.setVuelto(BigDecimal.ZERO);

        VentasModel recalculada = crearVentaModel();

        when(ventasRepository.save(any(VentasModel.class)))
                .thenReturn(guardadaInicial)
                .thenReturn(recalculada);

        doNothing().when(detalleVentaClient).crearDetalle(any());
        when(ventasRepository.findById(1L)).thenReturn(Optional.of(guardadaInicial));
        when(detalleVentaClient.obtenerDetallesPorVenta(1L)).thenReturn(List.of(crearDetalleVentaResponse()));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(empleadoClient.obtenerEmpleadoPorId(1L)).thenReturn(crearEmpleadoResponse());

        VentasResponse response = ventasServices.guardar(request);

        assertNotNull(response);
        assertEquals(1L, response.getIdVenta());
        assertEquals(BigDecimal.valueOf(10000), response.getSubTotal());

        verify(detalleVentaClient, times(1)).crearDetalle(any());
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        VentasRequest request = crearVentasRequest();
        request.setObservaciones("Venta actualizada");
        request.setSubTotal(BigDecimal.valueOf(10000));
        request.setDescuentoTotal(BigDecimal.ZERO);
        request.setImpuestoTotal(BigDecimal.valueOf(1900));
        request.setTotal(BigDecimal.valueOf(11900));
        request.setVuelto(BigDecimal.valueOf(8100));

        VentasModel existente = crearVentaModel();

        VentasModel actualizada = crearVentaModel();
        actualizada.setObservaciones("Venta actualizada");

        when(ventasRepository.findById(id)).thenReturn(Optional.of(existente));
        when(ventasRepository.save(any(VentasModel.class))).thenReturn(actualizada);
        when(detalleVentaClient.obtenerDetallesPorVenta(id)).thenReturn(List.of(crearDetalleVentaResponse()));
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(empleadoClient.obtenerEmpleadoPorId(1L)).thenReturn(crearEmpleadoResponse());

        VentasResponse response = ventasServices.actualizar(id, request);

        assertNotNull(response);
        assertEquals("Venta actualizada", response.getObservaciones());
    }

    @Test
    public void testRecalcularTotal() {
        Long id = 1L;

        VentasModel venta = crearVentaModel();
        venta.setSubTotal(BigDecimal.ZERO);
        venta.setImpuestoTotal(BigDecimal.ZERO);
        venta.setTotal(BigDecimal.ZERO);
        venta.setVuelto(BigDecimal.ZERO);

        VentasModel recalculada = crearVentaModel();

        when(ventasRepository.findById(id)).thenReturn(Optional.of(venta));
        when(detalleVentaClient.obtenerDetallesPorVenta(id)).thenReturn(List.of(crearDetalleVentaResponse()));
        when(ventasRepository.save(any(VentasModel.class))).thenReturn(recalculada);
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(empleadoClient.obtenerEmpleadoPorId(1L)).thenReturn(crearEmpleadoResponse());

        VentasResponse response = ventasServices.recalcularTotal(id);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(11900), response.getTotal());
        assertEquals(BigDecimal.valueOf(8100), response.getVuelto());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        VentasModel venta = crearVentaModel();

        when(ventasRepository.findById(id)).thenReturn(Optional.of(venta));
        doNothing().when(ventasRepository).delete(venta);

        ventasServices.eliminar(id);

        verify(ventasRepository, times(1)).delete(venta);
    }

    private VentasModel crearVentaModel() {
        return VentasModel.builder()
                .idVenta(1L)
                .fechaVenta(LocalDateTime.now())
                .subTotal(BigDecimal.valueOf(10000))
                .descuentoTotal(BigDecimal.ZERO)
                .impuestoTotal(BigDecimal.valueOf(1900))
                .total(BigDecimal.valueOf(11900))
                .metodoPago(MetodoDePago.EFECTIVO)
                .tipoComprobante(TipoComprobante.BOLETA)
                .montoPagado(BigDecimal.valueOf(20000))
                .vuelto(BigDecimal.valueOf(8100))
                .estadoVenta(EstadoVentas.COMPLETADA)
                .clienteId(1L)
                .empleadoId(1L)
                .numeroComprobante("B001")
                .observaciones("Venta de prueba")
                .build();
    }

    private VentasRequest crearVentasRequest() {
        ItemVentaRequest item = new ItemVentaRequest();
        item.setProductoId(1L);
        item.setCantidad(1);

        VentasRequest request = new VentasRequest();
        request.setClienteId(1L);
        request.setEmpleadoId(1L);
        request.setMetodoPago(MetodoDePago.EFECTIVO);
        request.setTipoComprobante(TipoComprobante.BOLETA);
        request.setMontoPagado(BigDecimal.valueOf(20000));
        request.setNumeroComprobante("B001");
        request.setObservaciones("Venta de prueba");
        request.setItems(List.of(item));
        return request;
    }

    private DetalleVentaResponse crearDetalleVentaResponse() {
        DetalleVentaResponse detalle = new DetalleVentaResponse();
        detalle.setIdDetalle(1L);
        detalle.setVentaId(1L);
        detalle.setProductoId(1L);
        detalle.setNombreProducto("Arroz");
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(BigDecimal.valueOf(10000));
        detalle.setSubTotal(BigDecimal.valueOf(10000));
        return detalle;
    }

    private ClienteResponse crearClienteResponse() {
        ClienteResponse cliente = new ClienteResponse();
        cliente.setId(1L);
        cliente.setNombre("Cliente Uno");
        cliente.setRut("11111111-1");
        cliente.setDireccion("Maipú");
        cliente.setTelefono("+56912345678");
        cliente.setEmail("cliente@gmail.com");
        return cliente;
    }

    private EmpleadoResponse crearEmpleadoResponse() {
        EmpleadoResponse empleado = new EmpleadoResponse();
        empleado.setId(1L);
        empleado.setNombre("Empleado Uno");
        empleado.setCargo("Vendedor");
        return empleado;
    }
}