package com.almacen.inventario_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.inventario_service.dtos.request.InventarioRequest;
import com.almacen.inventario_service.dtos.response.InventarioResponse;
import com.almacen.inventario_service.dtos.response.ProductoResponse;
import com.almacen.inventario_service.models.InventarioModel;
import com.almacen.inventario_service.repositories.InventarioRepository;
import com.almacen.inventario_service.services.InventarioService;
import com.almacen.inventario_service.webclient.ProductoClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class InventarioServiceTest {

    @Autowired
    private InventarioService inventarioService;

    @MockBean
    private InventarioRepository inventarioRepository;

    @MockBean
    private ProductoClient productoClient;

    @Test
    public void testObtenerTodos() {
        InventarioModel inventario = crearInventarioModel();
        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);

        List<InventarioResponse> inventarios = inventarioService.obtenerTodos();

        assertNotNull(inventarios);
        assertEquals(1, inventarios.size());
        assertEquals(20, inventarios.get(0).getStockActual());
        assertEquals("Arroz", inventarios.get(0).getProducto().getNombre());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        InventarioModel inventario = crearInventarioModel();
        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findById(id)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);

        InventarioResponse response = inventarioService.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(1L, response.getProductoId());
    }

    @Test
    public void testObtenerPorProductoId() {
        Long productoId = 1L;

        InventarioModel inventario = crearInventarioModel();
        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
        when(productoClient.obtenerProductoPorId(productoId)).thenReturn(producto);

        InventarioResponse response = inventarioService.obtenerPorProductoId(productoId);

        assertNotNull(response);
        assertEquals(productoId, response.getProductoId());
        assertEquals("Arroz", response.getProducto().getNombre());
    }

    @Test
    public void testGuardar() {
        InventarioRequest request = crearInventarioRequest();

        InventarioModel guardado = crearInventarioModel();
        ProductoResponse producto = crearProductoResponse();

        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);
        when(inventarioRepository.save(any(InventarioModel.class))).thenReturn(guardado);

        InventarioResponse response = inventarioService.guardar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(20, response.getStockActual());
        assertEquals("Arroz", response.getProducto().getNombre());
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        InventarioRequest request = crearInventarioRequest();
        request.setStockActual(30);

        InventarioModel existente = crearInventarioModel();

        InventarioModel actualizado = crearInventarioModel();
        actualizado.setStockActual(30);

        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findById(id)).thenReturn(Optional.of(existente));
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);
        when(inventarioRepository.save(any(InventarioModel.class))).thenReturn(actualizado);

        InventarioResponse response = inventarioService.actualizar(id, request);

        assertNotNull(response);
        assertEquals(30, response.getStockActual());
    }

    @Test
    public void testDescontarStock() {
        Long productoId = 1L;
        Integer cantidad = 5;

        InventarioModel inventario = crearInventarioModel();

        InventarioModel actualizado = crearInventarioModel();
        actualizado.setStockActual(15);

        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioModel.class))).thenReturn(actualizado);
        when(productoClient.obtenerProductoPorId(productoId)).thenReturn(producto);

        InventarioResponse response = inventarioService.descontarStock(productoId, cantidad);

        assertNotNull(response);
        assertEquals(15, response.getStockActual());
        assertEquals(productoId, response.getProductoId());
    }

    @Test
    public void testAumentarStock() {
        Long productoId = 1L;
        Integer cantidad = 10;

        InventarioModel inventario = crearInventarioModel();

        InventarioModel actualizado = crearInventarioModel();
        actualizado.setStockActual(30);

        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(InventarioModel.class))).thenReturn(actualizado);
        when(productoClient.obtenerProductoPorId(productoId)).thenReturn(producto);

        InventarioResponse response = inventarioService.aumentarStock(productoId, cantidad);

        assertNotNull(response);
        assertEquals(30, response.getStockActual());
    }

    @Test
    public void testObtenerPorCategoria() {
        Long categoriaId = 1L;

        ProductoResponse producto = crearProductoResponse();
        InventarioModel inventario = crearInventarioModel();

        when(productoClient.obtenerProductosPorCategoria(categoriaId)).thenReturn(List.of(producto));
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inventario));

        List<InventarioResponse> response = inventarioService.obtenerPorCategoria(categoriaId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(categoriaId, response.get(0).getProducto().getCategoriaId());
    }

    @Test
    public void testObtenerBajoStock() {
        InventarioModel inventario = crearInventarioModel();
        inventario.setStockActual(3);
        inventario.setStockMinimo(5);

        ProductoResponse producto = crearProductoResponse();

        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(productoClient.obtenerProductoPorId(1L)).thenReturn(producto);

        List<InventarioResponse> response = inventarioService.obtenerBajoStock();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(3, response.get(0).getStockActual());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        InventarioModel inventario = crearInventarioModel();

        when(inventarioRepository.findById(id)).thenReturn(Optional.of(inventario));
        doNothing().when(inventarioRepository).delete(inventario);

        inventarioService.eliminar(id);

        verify(inventarioRepository, times(1)).delete(inventario);
    }

    private InventarioModel crearInventarioModel() {
        return InventarioModel.builder()
                .id(1L)
                .stockActual(20)
                .stockMinimo(5)
                .productoId(1L)
                .build();
    }

    private InventarioRequest crearInventarioRequest() {
        InventarioRequest request = new InventarioRequest();
        request.setStockActual(20);
        request.setStockMinimo(5);
        request.setProductoId(1L);
        return request;
    }

    private ProductoResponse crearProductoResponse() {
        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000L);
        producto.setCategoriaId(1L);
        return producto;
    }
}