package com.almacen.productos_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.dtos.request.InventarioRequest;
import com.almacen.productos_service.dtos.response.CategoriaResponse;
import com.almacen.productos_service.dtos.response.ProductoResponse;
import com.almacen.productos_service.dtos.response.ProveedorResponse;
import com.almacen.productos_service.models.ProductoModel;
import com.almacen.productos_service.repositories.ProductoRepository;
import com.almacen.productos_service.services.ProductoService;
import com.almacen.productos_service.webclient.CategoriaClient;
import com.almacen.productos_service.webclient.InventarioClient;
import com.almacen.productos_service.webclient.ProveedorClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class ProductosServiceTest {

    @Autowired
    private ProductoService productoService;

    @MockBean
    private ProductoRepository productoRepository;

    @MockBean
    private CategoriaClient categoriaClient;

    @MockBean
    private InventarioClient inventarioClient;

    @MockBean
    private ProveedorClient proveedorClient;

    @Test
    public void testObtenerTodos() {
        ProductoModel producto = crearProductoModel();

        when(productoRepository.findAll()).thenReturn(List.of(producto));
        when(categoriaClient.obtenerCatPorId(1L)).thenReturn(crearCategoriaResponse());
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());

        List<ProductoResponse> productos = productoService.obtenerTodos();

        assertNotNull(productos);
        assertEquals(1, productos.size());
        assertEquals("Arroz", productos.get(0).getNombre());
        assertEquals("Alimentos", productos.get(0).getCategoria().getNombre());
        assertEquals("Proveedor Uno", productos.get(0).getProveedor().getNombre());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        when(productoRepository.findById(id)).thenReturn(Optional.of(crearProductoModel()));
        when(categoriaClient.obtenerCatPorId(1L)).thenReturn(crearCategoriaResponse());
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());

        ProductoResponse response = productoService.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Arroz", response.getNombre());
    }

    @Test
    public void testObtenerPorCategoria() {
        Long categoriaId = 1L;

        when(categoriaClient.obtenerCatPorId(categoriaId)).thenReturn(crearCategoriaResponse());
        when(productoRepository.findByCategoriaId(categoriaId)).thenReturn(List.of(crearProductoModel()));
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());

        List<ProductoResponse> response = productoService.obtenerPorCategoria(categoriaId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(categoriaId, response.get(0).getCategoriaId());
    }

    @Test
    public void testObtenerPorProveedor() {
        Long proveedorId = 1L;

        when(productoRepository.findByProveedorId(proveedorId)).thenReturn(List.of(crearProductoModel()));
        when(categoriaClient.obtenerCatPorId(1L)).thenReturn(crearCategoriaResponse());
        when(proveedorClient.obtenerProveedorPorId(proveedorId)).thenReturn(crearProveedorResponse());

        List<ProductoResponse> response = productoService.obtenerPorProveedor(proveedorId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(proveedorId, response.get(0).getProveedorId());
    }

    @Test
    public void testGuardar() {
        ProductoRequest request = crearProductoRequest();

        ProductoModel guardado = crearProductoModel();

        when(categoriaClient.obtenerCatPorId(1L)).thenReturn(crearCategoriaResponse());
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoRepository.save(any(ProductoModel.class))).thenReturn(guardado);
        doNothing().when(inventarioClient).crearInventario(any(InventarioRequest.class));

        ProductoResponse response = productoService.guardar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Arroz", response.getNombre());

        verify(inventarioClient, times(1)).crearInventario(any(InventarioRequest.class));
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        ProductoRequest request = crearProductoRequest();
        request.setNombre("Arroz Actualizado");

        ProductoModel existente = crearProductoModel();

        ProductoModel actualizado = crearProductoModel();
        actualizado.setNombre("Arroz Actualizado");

        when(productoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(categoriaClient.obtenerCatPorId(1L)).thenReturn(crearCategoriaResponse());
        when(proveedorClient.obtenerProveedorPorId(1L)).thenReturn(crearProveedorResponse());
        when(productoRepository.save(any(ProductoModel.class))).thenReturn(actualizado);

        ProductoResponse response = productoService.actualizar(id, request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Arroz Actualizado", response.getNombre());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        ProductoModel producto = crearProductoModel();

        when(productoRepository.findById(id)).thenReturn(Optional.of(producto));
        doNothing().when(productoRepository).delete(producto);

        productoService.eliminar(id);

        verify(productoRepository, times(1)).delete(producto);
    }

    private ProductoModel crearProductoModel() {
        return ProductoModel.builder()
                .id(1L)
                .nombre("Arroz")
                .precio(10000L)
                .categoriaId(1L)
                .proveedorId(1L)
                .build();
    }

    private ProductoRequest crearProductoRequest() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Arroz");
        request.setPrecio(10000L);
        request.setCategoriaId(1L);
        request.setProveedorId(1L);
        request.setStockInicial(20);
        request.setStockMinimo(5);
        return request;
    }

    private CategoriaResponse crearCategoriaResponse() {
        CategoriaResponse categoria = new CategoriaResponse();
        categoria.setId(1L);
        categoria.setNombre("Alimentos");
        categoria.setDescripcion("Productos alimenticios");
        return categoria;
    }

    private ProveedorResponse crearProveedorResponse() {
        ProveedorResponse proveedor = new ProveedorResponse();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Uno");
        proveedor.setContacto("contacto@proveedor.cl");
        proveedor.setRut("22222222-2");
        return proveedor;
    }
}