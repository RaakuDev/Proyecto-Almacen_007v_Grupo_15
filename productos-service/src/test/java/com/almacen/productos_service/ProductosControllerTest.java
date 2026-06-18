package com.almacen.productos_service;

import com.almacen.productos_service.controllers.ProductoController;
import com.almacen.productos_service.dtos.request.ProductoRequest;
import com.almacen.productos_service.dtos.response.CategoriaResponse;
import com.almacen.productos_service.dtos.response.ProductoResponse;
import com.almacen.productos_service.dtos.response.ProveedorResponse;
import com.almacen.productos_service.security.JwtAuthenticationFilter;
import com.almacen.productos_service.security.JwtService;
import com.almacen.productos_service.services.ProductoService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ProductosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoResponse response;
    private ProductoRequest request;

    @BeforeEach
    void setUp() {
        response = crearProductoResponse();
        request = crearProductoRequest();
    }

    @Test
    public void testObtenerTodos() throws Exception {
        when(productoService.obtenerTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Arroz 1kg"))
                .andExpect(jsonPath("$[0].precio").value(1500))
                .andExpect(jsonPath("$[0].categoriaId").value(1))
                .andExpect(jsonPath("$[0].proveedorId").value(1));
    }

    @Test
    public void testObtenerPorId() throws Exception {
        when(productoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Arroz 1kg"))
                .andExpect(jsonPath("$.precio").value(1500))
                .andExpect(jsonPath("$.categoria.nombre").value("Abarrotes"))
                .andExpect(jsonPath("$.proveedor.nombre").value("Distribuidora Central"));
    }

    @Test
    public void testObtenerPorCategoria() throws Exception {
        when(productoService.obtenerPorCategoria(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/productos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoriaId").value(1))
                .andExpect(jsonPath("$[0].categoria.nombre").value("Abarrotes"));
    }

    @Test
    public void testObtenerPorProveedor() throws Exception {
        when(productoService.obtenerPorProveedor(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/productos/proveedor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].proveedorId").value(1))
                .andExpect(jsonPath("$[0].proveedor.nombre").value("Distribuidora Central"));
    }

    @Test
    public void testCrearProducto() throws Exception {
        when(productoService.guardar(any(ProductoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Arroz 1kg"))
                .andExpect(jsonPath("$.precio").value(1500));
    }

    @Test
    public void testActualizarProducto() throws Exception {
        when(productoService.actualizar(eq(1L), any(ProductoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Arroz 1kg"));
    }

    @Test
    public void testEliminarProducto() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Producto eliminado"));

        verify(productoService, times(1)).eliminar(1L);
    }

    private ProductoResponse crearProductoResponse() {
        CategoriaResponse categoria = new CategoriaResponse();
        categoria.setId(1L);
        categoria.setNombre("Abarrotes");
        categoria.setDescripcion("Productos secos");

        ProveedorResponse proveedor = new ProveedorResponse();
        proveedor.setId(1L);
        proveedor.setNombre("Distribuidora Central");
        proveedor.setContacto("contacto@central.cl");
        proveedor.setRut("76.123.456-1");

        return ProductoResponse.builder()
                .id(1L)
                .nombre("Arroz 1kg")
                .precio(1500L)
                .categoriaId(1L)
                .proveedorId(1L)
                .categoria(categoria)
                .proveedor(proveedor)
                .build();
    }

    private ProductoRequest crearProductoRequest() {
        ProductoRequest request = new ProductoRequest();
        request.setNombre("Arroz 1kg");
        request.setPrecio(1500L);
        request.setCategoriaId(1L);
        request.setProveedorId(1L);
        request.setStockInicial(20);
        request.setStockMinimo(5);

        return request;
    }
}