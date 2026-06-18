package com.almacen.empleados_service;

import com.almacen.empleados_service.controller.EmpleadoController;
import com.almacen.empleados_service.dto.response.EmpleadoResponse;
import com.almacen.empleados_service.security.JwtAuthenticationFilter;
import com.almacen.empleados_service.security.JwtService;
import com.almacen.empleados_service.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmpleadoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class EmpleadosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmpleadoService empleadoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testObtenerTodos_retorna200() throws Exception {
        EmpleadoResponse response = EmpleadoResponse.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .rut("12345678-9")
                .cargo("Vendedor")
                .turno("Mañana")
                .telefono("+56912345678")
                .email("juan.perez@almacen.cl")
                .fechaInicioContrato(LocalDate.of(2024, 1, 15))
                .activo(true)
                .usuarioId(1L)
                .build();

        when(empleadoService.obtenerTodos())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/empleados")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].cargo").value("Vendedor"));
    }

    @Test
    public void testObtenerPorId_retorna200() throws Exception {
        EmpleadoResponse response = EmpleadoResponse.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .rut("12345678-9")
                .cargo("Vendedor")
                .turno("Mañana")
                .telefono("+56912345678")
                .email("juan.perez@almacen.cl")
                .fechaInicioContrato(LocalDate.of(2024, 1, 15))
                .activo(true)
                .usuarioId(1L)
                .build();

        when(empleadoService.obtenerPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/empleados/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.cargo").value("Vendedor"));
    }

    @Test
    public void testObtenerPorUsuarioId_retorna200() throws Exception {
        EmpleadoResponse response = EmpleadoResponse.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .rut("12345678-9")
                .cargo("Vendedor")
                .turno("Mañana")
                .telefono("+56912345678")
                .email("juan.perez@almacen.cl")
                .fechaInicioContrato(LocalDate.of(2024, 1, 15))
                .activo(true)
                .usuarioId(1L)
                .build();

        when(empleadoService.obtenerPorUsuarioId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/empleados/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    @Test
    public void testCrearEmpleado_retorna201() throws Exception {
        EmpleadoResponse response = EmpleadoResponse.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .rut("12345678-9")
                .cargo("Vendedor")
                .turno("Mañana")
                .telefono("+56912345678")
                .email("juan.perez@almacen.cl")
                .fechaInicioContrato(LocalDate.of(2024, 1, 15))
                .activo(true)
                .usuarioId(1L)
                .build();

        when(empleadoService.crearEmpleado(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Juan Pérez",
                                "rut": "12345678-9",
                                "cargo": "Vendedor",
                                "turno": "Mañana",
                                "telefono": "+56912345678",
                                "email": "juan.perez@almacen.cl",
                                "fechaInicioContrato": "2024-01-15",
                                "activo": true,
                                "usuarioId": 1
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.usuarioId").value(1));
    }

    @Test
    public void testActualizarEmpleado_retorna200() throws Exception {
        EmpleadoResponse response = EmpleadoResponse.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .rut("12345678-9")
                .cargo("Supervisor")
                .turno("Tarde")
                .telefono("+56912345678")
                .email("juan.perez@almacen.cl")
                .fechaInicioContrato(LocalDate.of(2024, 1, 15))
                .activo(true)
                .usuarioId(1L)
                .build();

        when(empleadoService.actualizarEmpleado(anyLong(), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/empleados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Juan Pérez",
                                "rut": "12345678-9",
                                "cargo": "Supervisor",
                                "turno": "Tarde",
                                "telefono": "+56912345678",
                                "email": "juan.perez@almacen.cl",
                                "fechaInicioContrato": "2024-01-15",
                                "activo": true,
                                "usuarioId": 1
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("Supervisor"))
                .andExpect(jsonPath("$.turno").value("Tarde"));
    }

    @Test
    public void testEliminarEmpleado_retorna200() throws Exception {
        doNothing().when(empleadoService).eliminarEmpleado(1L);

        mockMvc.perform(delete("/api/v1/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Empleado eliminado"));
    }
}