package com.almacen.empleados_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.empleados_service.dto.request.EmpleadoRequest;
import com.almacen.empleados_service.dto.response.EmpleadoResponse;
import com.almacen.empleados_service.dto.response.VentaResponse;
import com.almacen.empleados_service.model.EmpleadoModel;
import com.almacen.empleados_service.repository.EmpleadoRepository;
import com.almacen.empleados_service.service.EmpleadoService;
import com.almacen.empleados_service.webclient.VentasClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class EmpleadosServiceTest {

    @Autowired
    private EmpleadoService empleadoService;

    @MockBean
    private EmpleadoRepository empleadoRepository;

    @MockBean
    private VentasClient ventasClient;

    @Test
    public void testObtenerTodos() {
        EmpleadoModel empleado = crearEmpleadoModel();

        VentaResponse venta = crearVentaResponse();

        when(empleadoRepository.findAll()).thenReturn(List.of(empleado));
        when(ventasClient.obtenerVentasPorEmpleado(1L)).thenReturn(List.of(venta));

        List<EmpleadoResponse> empleados = empleadoService.obtenerTodos();

        assertNotNull(empleados);
        assertEquals(1, empleados.size());
        assertEquals("Alejandro", empleados.get(0).getNombre());
        assertEquals(1, empleados.get(0).getVentas().size());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        EmpleadoModel empleado = crearEmpleadoModel();

        when(empleadoRepository.findById(id)).thenReturn(Optional.of(empleado));
        when(ventasClient.obtenerVentasPorEmpleado(id)).thenReturn(List.of(crearVentaResponse()));

        EmpleadoResponse encontrado = empleadoService.obtenerPorId(id);

        assertNotNull(encontrado);
        assertEquals(id, encontrado.getId());
        assertEquals("Alejandro", encontrado.getNombre());
    }

    @Test
    public void testObtenerPorUsuarioId() {
        Long usuarioId = 10L;

        EmpleadoModel empleado = crearEmpleadoModel();

        when(empleadoRepository.findByUsuarioId(usuarioId)).thenReturn(Optional.of(empleado));
        when(ventasClient.obtenerVentasPorEmpleado(1L)).thenReturn(List.of(crearVentaResponse()));

        EmpleadoResponse encontrado = empleadoService.obtenerPorUsuarioId(usuarioId);

        assertNotNull(encontrado);
        assertEquals(usuarioId, encontrado.getUsuarioId());
    }

    @Test
    public void testCrearEmpleado() {
        EmpleadoRequest request = crearEmpleadoRequest();

        EmpleadoModel guardado = crearEmpleadoModel();

        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(guardado);
        when(ventasClient.obtenerVentasPorEmpleado(1L)).thenReturn(List.of(crearVentaResponse()));

        EmpleadoResponse response = empleadoService.crearEmpleado(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Alejandro", response.getNombre());
    }

    @Test
    public void testActualizarEmpleado() {
        Long id = 1L;

        EmpleadoRequest request = crearEmpleadoRequest();
        request.setNombre("Alejandro Actualizado");

        EmpleadoModel existente = crearEmpleadoModel();

        EmpleadoModel actualizado = crearEmpleadoModel();
        actualizado.setNombre("Alejandro Actualizado");

        when(empleadoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(actualizado);
        when(ventasClient.obtenerVentasPorEmpleado(id)).thenReturn(List.of(crearVentaResponse()));

        EmpleadoResponse response = empleadoService.actualizarEmpleado(id, request);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Alejandro Actualizado", response.getNombre());
    }

    @Test
    public void testEliminarEmpleado() {
        Long id = 1L;

        when(empleadoRepository.existsById(id)).thenReturn(true);
        doNothing().when(empleadoRepository).deleteById(id);

        empleadoService.eliminarEmpleado(id);

        verify(empleadoRepository, times(1)).deleteById(id);
    }

    private EmpleadoModel crearEmpleadoModel() {
        return EmpleadoModel.builder()
                .id(1L)
                .nombre("Alejandro")
                .rut("11111111-1")
                .cargo("Vendedor")
                .turno("Mañana")
                .telefono("+56912345678")
                .email("alejandro@gmail.com")
                .fechaInicioContrato(LocalDate.of(2026, 1, 1))
                .activo(true)
                .usuarioId(10L)
                .build();
    }

    private EmpleadoRequest crearEmpleadoRequest() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Alejandro");
        request.setRut("11111111-1");
        request.setCargo("Vendedor");
        request.setTurno("Mañana");
        request.setTelefono("+56912345678");
        request.setEmail("alejandro@gmail.com");
        request.setFechaInicioContrato(LocalDate.of(2026, 1, 1));
        request.setActivo(true);
        request.setUsuarioId(10L);
        return request;
    }

    private VentaResponse crearVentaResponse() {
        VentaResponse venta = new VentaResponse();
        venta.setIdVenta(1L);
        venta.setFechaVenta(LocalDateTime.now());
        venta.setTotal(BigDecimal.valueOf(50000));
        venta.setClienteID(1L);
        return venta;
    }
}