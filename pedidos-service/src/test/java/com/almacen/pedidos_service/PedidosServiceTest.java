package com.almacen.pedidos_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.pedidos_service.dtos.request.PedidoItem;
import com.almacen.pedidos_service.dtos.request.PedidosRequest;
import com.almacen.pedidos_service.dtos.response.ClienteResponse;
import com.almacen.pedidos_service.dtos.response.PedidosResponse;
import com.almacen.pedidos_service.dtos.response.ProductoResponse;
import com.almacen.pedidos_service.dtos.response.ProveedorResponse;
import com.almacen.pedidos_service.model.PedidoModel;
import com.almacen.pedidos_service.repository.PedidoRepository;
import com.almacen.pedidos_service.service.PedidoService;
import com.almacen.pedidos_service.webclient.ClienteClient;
import com.almacen.pedidos_service.webclient.ProductoClient;
import com.almacen.pedidos_service.webclient.ProveedorClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class PedidosServiceTest {

    @Autowired
    private PedidoService pedidoService;

    @MockBean
    private PedidoRepository pedidoRepository;

    @MockBean
    private ProveedorClient proveedorClient;

    @MockBean
    private ProductoClient productoClient;

    @MockBean
    private ClienteClient clienteClient;

    @Test
    public void testObtenerTodos() {
        PedidoModel pedido = crearPedidoModel();
        ProveedorResponse proveedor = crearProveedorResponse();
        ProductoResponse producto = crearProductoResponse();
        ClienteResponse cliente = crearClienteResponse();

        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(proveedor);
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(cliente);

        List<PedidosResponse> pedidos = pedidoService.obtenerTodos();

        assertNotNull(pedidos);
        assertEquals(1, pedidos.size());
        assertEquals("PENDIENTE", pedidos.get(0).getEstado());
        assertEquals("Proveedor Uno", pedidos.get(0).getProveedor().getNombre());
        assertEquals("Cliente Uno", pedidos.get(0).getCliente().getNombre());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        PedidoModel pedido = crearPedidoModel();

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(crearProductoResponse());
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());

        PedidosResponse response = pedidoService.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("PENDIENTE", response.getEstado());
    }

    @Test
    public void testObtenerPorCliente() {
        Long clienteId = 1L;

        PedidoModel pedido = crearPedidoModel();

        when(pedidoRepository.findByClienteId(clienteId)).thenReturn(List.of(pedido));
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(crearProductoResponse());
        when(clienteClient.obtenerClientePorId(clienteId)).thenReturn(crearClienteResponse());

        List<PedidosResponse> response = pedidoService.obtenerPorCliente(clienteId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(clienteId, response.get(0).getClienteId());
    }

    @Test
    public void testObtenerPorEstado() {
        String estado = "PENDIENTE";

        PedidoModel pedido = crearPedidoModel();

        when(pedidoRepository.findByEstado(estado)).thenReturn(List.of(pedido));
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(crearProductoResponse());
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());

        List<PedidosResponse> response = pedidoService.obtenerPorEstado(estado);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(estado, response.get(0).getEstado());
    }

    @Test
    public void testGuardar() {
        PedidosRequest request = crearPedidosRequest();

        PedidoModel guardado = crearPedidoModel();

        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(crearProductoResponse());
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(guardado);

        PedidosResponse response = pedidoService.guardar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals(1, response.getProductos().size());
        assertEquals(2, response.getProductos().get(0).getCantidad());
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        PedidosRequest request = crearPedidosRequest();
        request.setEstado("APROBADO");

        PedidoModel existente = crearPedidoModel();

        PedidoModel actualizado = crearPedidoModel();
        actualizado.setEstado("APROBADO");

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(crearProductoResponse());
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());
        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(actualizado);

        PedidosResponse response = pedidoService.actualizar(id, request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("APROBADO", response.getEstado());
    }

    @Test
    public void testCambiarEstado() {
        Long id = 1L;

        PedidoModel existente = crearPedidoModel();

        PedidoModel actualizado = crearPedidoModel();
        actualizado.setEstado("FINALIZADO");

        when(pedidoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(actualizado);
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(crearProductoResponse());
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(crearClienteResponse());

        PedidosResponse response = pedidoService.cambiarEstado(id, "FINALIZADO");

        assertNotNull(response);
        assertEquals("FINALIZADO", response.getEstado());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        when(pedidoRepository.existsById(id)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(id);

        pedidoService.eliminar(id);

        verify(pedidoRepository, times(1)).deleteById(id);
    }

    private PedidoModel crearPedidoModel() {
        return PedidoModel.builder()
                .id(1L)
                .fechaPedido(LocalDate.of(2026, 1, 1))
                .estado("PENDIENTE")
                .proveedorId(1L)
                .clienteId(1L)
                .productosIds("1:2")
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

    private ProveedorResponse crearProveedorResponse() {
        ProveedorResponse proveedor = new ProveedorResponse();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Uno");
        proveedor.setContacto("contacto@proveedor.cl");
        proveedor.setRut("22222222-2");
        return proveedor;
    }

    private ProductoResponse crearProductoResponse() {
        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setCategoriaId(1L);
        return producto;
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
}