package com.almacen.clientes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.clientes.dtos.request.ClientesRequest;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.dtos.response.PedidoResponse;
import com.almacen.clientes.models.ClientesModel;
import com.almacen.clientes.repositories.ClientesRepository;
import com.almacen.clientes.services.ClientesService;
import com.almacen.clientes.webclient.PedidoClient;

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
public class ClientesServiceTest {

    @Autowired
    private ClientesService clientesService;

    @MockBean
    private ClientesRepository clientesRepository;

    @MockBean
    private PedidoClient pedidoClient;

    @Test
    public void testObtenerTodos() {
        ClientesModel cliente = ClientesModel.builder()
                .id(1L)
                .nombre("Alejandro")
                .rut("11111111-1")
                .direccion("Maipú")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .build();

        when(clientesRepository.findAll()).thenReturn(List.of(cliente));

        List<ClientesResponse> clientes = clientesService.obtenerTodos();

        assertNotNull(clientes);
        assertEquals(1, clientes.size());
        assertEquals("Alejandro", clientes.get(0).getNombre());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        ClientesModel cliente = ClientesModel.builder()
                .id(id)
                .nombre("Alejandro")
                .rut("11111111-1")
                .direccion("Maipú")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .build();

        when(clientesRepository.findById(id)).thenReturn(Optional.of(cliente));

        ClientesResponse encontrado = clientesService.obtenerPorId(id);

        assertNotNull(encontrado);
        assertEquals(id, encontrado.getId());
        assertEquals("Alejandro", encontrado.getNombre());
    }

    @Test
    public void testGuardar() {
        ClientesRequest request = new ClientesRequest();
        request.setNombre("Alejandro");
        request.setRut("11111111-1");
        request.setDireccion("Maipú");
        request.setTelefono("+56912345678");
        request.setEmail("alejandro@gmail.com");

        ClientesModel clienteGuardado = ClientesModel.builder()
                .id(1L)
                .nombre("Alejandro")
                .rut("11111111-1")
                .direccion("Maipú")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .build();

        when(clientesRepository.save(any(ClientesModel.class))).thenReturn(clienteGuardado);

        ClientesResponse guardado = clientesService.guardar(request);

        assertNotNull(guardado);
        assertEquals(1L, guardado.getId());
        assertEquals("Alejandro", guardado.getNombre());
        assertEquals("11111111-1", guardado.getRut());
    }

    @Test
    public void testActualizar() {
        Long id = 1L;

        ClientesRequest request = new ClientesRequest();
        request.setNombre("Alejandro Actualizado");
        request.setRut("11111111-1");
        request.setDireccion("Santiago");
        request.setTelefono("+56987654321");
        request.setEmail("alejandro2@gmail.com");

        ClientesModel clienteExistente = ClientesModel.builder()
                .id(id)
                .nombre("Alejandro")
                .rut("11111111-1")
                .direccion("Maipú")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .build();

        ClientesModel clienteActualizado = ClientesModel.builder()
                .id(id)
                .nombre("Alejandro Actualizado")
                .rut("11111111-1")
                .direccion("Santiago")
                .telefono("+56987654321")
                .email("alejandro2@gmail.com")
                .build();

        when(clientesRepository.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clientesRepository.save(any(ClientesModel.class))).thenReturn(clienteActualizado);

        ClientesResponse actualizado = clientesService.actualizar(id, request);

        assertNotNull(actualizado);
        assertEquals(id, actualizado.getId());
        assertEquals("Alejandro Actualizado", actualizado.getNombre());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        ClientesModel cliente = ClientesModel.builder()
                .id(id)
                .nombre("Alejandro")
                .rut("11111111-1")
                .direccion("Maipú")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .build();

        when(clientesRepository.findById(id)).thenReturn(Optional.of(cliente));
        doNothing().when(clientesRepository).delete(cliente);

        clientesService.eliminar(id);

        verify(clientesRepository, times(1)).delete(cliente);
    }

    @Test
    public void testObtenerPedidosDelCliente() {
        Long clienteId = 1L;

        ClientesModel cliente = ClientesModel.builder()
                .id(clienteId)
                .nombre("Alejandro")
                .rut("11111111-1")
                .direccion("Maipú")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .build();

        PedidoResponse pedido = new PedidoResponse();
        pedido.setId(1L);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(50000L);
        pedido.setClienteId(clienteId);

        when(clientesRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(pedidoClient.obtenerPedidosPorCliente(clienteId)).thenReturn(List.of(pedido));

        List<PedidoResponse> pedidos = clientesService.obtenerPedidosDelCliente(clienteId);

        assertNotNull(pedidos);
        assertEquals(1, pedidos.size());
        assertEquals(clienteId, pedidos.get(0).getClienteId());
    }
}