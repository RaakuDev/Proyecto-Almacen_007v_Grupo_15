package com.almacen.proveedores_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.proveedores_service.dtos.request.ProveedorRequest;
import com.almacen.proveedores_service.dtos.response.ProductoResponse;
import com.almacen.proveedores_service.dtos.response.ProveedorResponse;
import com.almacen.proveedores_service.models.ProveedorModel;
import com.almacen.proveedores_service.repositories.ProveedorRepository;
import com.almacen.proveedores_service.services.ProveedorService;
import com.almacen.proveedores_service.webclient.ProductoClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class ProveedoresServiceTest {

    @Autowired
    private ProveedorService proveedorService;

    @MockBean
    private ProveedorRepository proveedorRepository;

    @MockBean
    private ProductoClient productoClient;

    @Test
    public void testObtenerTodos() {
        ProveedorModel proveedor = crearProveedorModel();

        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));

        List<ProveedorResponse> proveedores = proveedorService.obtenerTodos();

        assertNotNull(proveedores);
        assertEquals(1, proveedores.size());
        assertEquals("Proveedor Uno", proveedores.get(0).getNombre());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        when(proveedorRepository.findById(id)).thenReturn(Optional.of(crearProveedorModel()));

        ProveedorResponse response = proveedorService.obtenerPorId(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Proveedor Uno", response.getNombre());
    }

    @Test
    public void testObtenerProductosDelProveedor() {
        Long proveedorId = 1L;

        ProductoResponse producto = crearProductoResponse();

        when(proveedorRepository.findById(proveedorId)).thenReturn(Optional.of(crearProveedorModel()));
        when(productoClient.obtenerProductosPorProveedor(proveedorId)).thenReturn(List.of(producto));

        List<ProductoResponse> productos = proveedorService.obtenerProductosDelProveedor(proveedorId);

        assertNotNull(productos);
        assertEquals(1, productos.size());
        assertEquals("Arroz", productos.get(0).getNombre());
        assertEquals(proveedorId, productos.get(0).getProveedorId());
    }

    @Test
    public void testGuardar() {
        ProveedorRequest request = crearProveedorRequest();

        when(proveedorRepository.save(any(ProveedorModel.class))).thenReturn(crearProveedorModel());

        ProveedorResponse response = proveedorService.guardar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Proveedor Uno", response.getNombre());
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        ProveedorRequest request = crearProveedorRequest();
        request.setNombre("Proveedor Actualizado");

        ProveedorModel existente = crearProveedorModel();

        ProveedorModel actualizado = crearProveedorModel();
        actualizado.setNombre("Proveedor Actualizado");

        when(proveedorRepository.findById(id)).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(any(ProveedorModel.class))).thenReturn(actualizado);

        ProveedorResponse response = proveedorService.actualizar(id, request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Proveedor Actualizado", response.getNombre());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        when(proveedorRepository.existsById(id)).thenReturn(true);
        doNothing().when(proveedorRepository).deleteById(id);

        proveedorService.eliminar(id);

        verify(proveedorRepository, times(1)).deleteById(id);
    }

    private ProveedorModel crearProveedorModel() {
        return ProveedorModel.builder()
                .id(1L)
                .nombre("Proveedor Uno")
                .contacto("contacto@proveedor.cl")
                .rut("22222222-2")
                .build();
    }

    private ProveedorRequest crearProveedorRequest() {
        ProveedorRequest request = new ProveedorRequest();
        request.setNombre("Proveedor Uno");
        request.setContacto("contacto@proveedor.cl");
        request.setRut("22222222-2");
        return request;
    }

    private ProductoResponse crearProductoResponse() {
        ProductoResponse producto = new ProductoResponse();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(10000.0);
        producto.setStock(20);
        producto.setCategoriaId(1L);
        producto.setProveedorId(1L);
        return producto;
    }
}