package com.almacen.clientes;

import com.almacen.clientes.controllers.ClientesController;
import com.almacen.clientes.dtos.response.ClientesResponse;
import com.almacen.clientes.security.JwtAuthenticationFilter;
import com.almacen.clientes.security.JwtService;
import com.almacen.clientes.services.ClientesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.http.MediaType;
import java.util.List;

@WebMvcTest(ClientesController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ClientesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientesService clientesService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testObtenerTodos_retorna200() throws Exception {
        ClientesResponse response = ClientesResponse.builder()
                .id(1L)
                .nombre("Alejandro")
                .rut("11111111-1")
                .build();

        when(clientesService.obtenerTodos()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Alejandro"));
    }

    @Test
    public void testObtenerPorId_retorna200() throws Exception {
        ClientesResponse response = ClientesResponse.builder()
                .id(1L)
                .nombre("Alejandro")
                .build();

        when(clientesService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Alejandro"));
    }

    @Test
    public void testCrear_retorna201() throws Exception {
        ClientesResponse response = ClientesResponse.builder()
                .id(1L)
                .nombre("Alejandro")
                .rut("11111111-1")
                .build();

        when(clientesService.guardar(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "nombre": "Alejandro",
                            "rut": "11111111-1",
                            "direccion": "Maipú",
                            "telefono": "+56912345678",
                            "email": "alejandro@gmail.com"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Alejandro"));
    }

    @Test
    public void testEliminar_retorna200() throws Exception {
        doNothing().when(clientesService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isNoContent());
    }
}