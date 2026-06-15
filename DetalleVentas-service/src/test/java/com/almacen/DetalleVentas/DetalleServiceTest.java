package com.almacen.DetalleVentas;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.DetalleVentas.dtos.request.DetalleRequest;
import com.almacen.DetalleVentas.dtos.response.DetalleResponse;
import com.almacen.DetalleVentas.dtos.response.ProductoResponse;
import com.almacen.DetalleVentas.models.DetalleModel;
import com.almacen.DetalleVentas.repositories.DetalleRepository;
import com.almacen.DetalleVentas.services.DetalleService;
import com.almacen.DetalleVentas.webclient.InventarioClient;
import com.almacen.DetalleVentas.webclient.ProductoClient;
import com.almacen.DetalleVentas.webclient.VentasClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class DetalleServiceTest {

    @Autowired
    private DetalleService detalleService;

    @MockBean
    private DetalleRepository detalleRepository;

    @MockBean
    private ProductoClient productoClient;

    @MockBean
    private VentasClient ventasClient;

    @MockBean
    private InventarioClient inventarioClient;

    @Test
    public void testObtenerTodos() {
        DetalleModel detalle = DetalleModel.builder()
                .idDetalleVenta(1L)
                .ventaId(1L)
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(20000))
                .build();

        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setStock(20);
        producto.setCategoriaId(1L);

        when(detalleRepository.findAll()).thenReturn(List.of(detalle));
        when(productoClient.obtenerProducto(1L)).thenReturn(producto);

        List<DetalleResponse> detalles = detalleService.obtenerTodos();

        assertNotNull(detalles);
        assertEquals(1, detalles.size());
        assertEquals("Arroz", detalles.get(0).getNombreProducto());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        DetalleModel detalle = DetalleModel.builder()
                .idDetalleVenta(id)
                .ventaId(1L)
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(20000))
                .build();

        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setStock(20);
        producto.setCategoriaId(1L);

        when(detalleRepository.findById(id)).thenReturn(Optional.of(detalle));
        when(productoClient.obtenerProducto(1L)).thenReturn(producto);

        DetalleResponse encontrado = detalleService.obtenerPorId(id);

        assertNotNull(encontrado);
        assertEquals(id, encontrado.getIdDetalle());
        assertEquals("Arroz", encontrado.getNombreProducto());
    }

    @Test
    public void testObtenerPorVentaId() {
        Long ventaId = 1L;

        DetalleModel detalle = DetalleModel.builder()
                .idDetalleVenta(1L)
                .ventaId(ventaId)
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(20000))
                .build();

        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setStock(20);
        producto.setCategoriaId(1L);

        when(detalleRepository.findByVentaId(ventaId)).thenReturn(List.of(detalle));
        when(productoClient.obtenerProducto(1L)).thenReturn(producto);

        List<DetalleResponse> detalles = detalleService.obtenerPorVentaId(ventaId);

        assertNotNull(detalles);
        assertEquals(1, detalles.size());
        assertEquals(ventaId, detalles.get(0).getVentaId());
    }

    @Test
    public void testGuardar() {
        DetalleRequest request = new DetalleRequest();
        request.setVentaId(1L);
        request.setProductoId(1L);
        request.setCantidad(2);

        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setStock(20);
        producto.setCategoriaId(1L);

        DetalleModel detalleGuardado = DetalleModel.builder()
                .idDetalleVenta(1L)
                .ventaId(1L)
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(20000))
                .build();

        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        doNothing().when(inventarioClient).descontarStock(1L, 2);
        when(detalleRepository.save(any(DetalleModel.class))).thenReturn(detalleGuardado);

        DetalleResponse guardado = detalleService.guardar(request);

        assertNotNull(guardado);
        assertEquals(1L, guardado.getIdDetalle());
        assertEquals("Arroz", guardado.getNombreProducto());
        assertEquals(BigDecimal.valueOf(20000), guardado.getSubTotal());

        verify(inventarioClient, times(1)).descontarStock(1L, 2);
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        DetalleRequest request = new DetalleRequest();
        request.setVentaId(1L);
        request.setProductoId(1L);
        request.setCantidad(3);

        DetalleModel detalleExistente = DetalleModel.builder()
                .idDetalleVenta(id)
                .ventaId(1L)
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(20000))
                .build();

        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setStock(20);
        producto.setCategoriaId(1L);

        DetalleModel detalleActualizado = DetalleModel.builder()
                .idDetalleVenta(id)
                .ventaId(1L)
                .productoId(1L)
                .cantidad(3)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(30000))
                .build();

        when(detalleRepository.findById(id)).thenReturn(Optional.of(detalleExistente));
        doNothing().when(ventasClient).validarVenta(1L);
        when(productoClient.obtenerProducto(1L)).thenReturn(producto);
        when(detalleRepository.save(any(DetalleModel.class))).thenReturn(detalleActualizado);

        DetalleResponse actualizado = detalleService.actualizar(id, request);

        assertNotNull(actualizado);
        assertEquals(id, actualizado.getIdDetalle());
        assertEquals(3, actualizado.getCantidad());
        assertEquals(BigDecimal.valueOf(30000), actualizado.getSubTotal());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        DetalleModel detalle = DetalleModel.builder()
                .idDetalleVenta(id)
                .ventaId(1L)
                .productoId(1L)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(10000))
                .subTotal(BigDecimal.valueOf(20000))
                .build();

        when(detalleRepository.findById(id)).thenReturn(Optional.of(detalle));
        doNothing().when(detalleRepository).delete(detalle);

        detalleService.eliminar(id);

        verify(detalleRepository, times(1)).delete(detalle);
    }
}