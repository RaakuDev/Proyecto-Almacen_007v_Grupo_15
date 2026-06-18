package com.almacen.categorias_service;

import com.almacen.categorias_service.controllers.CategoriaController;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.security.JwtAuthenticationFilter;
import com.almacen.categorias_service.security.JwtService;
import com.almacen.categorias_service.services.CategoriaService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CategoriasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void testObtenerTodos_retorna200() throws Exception {
        CategoriaResponse response = CategoriaResponse.builder()
                .id(1L)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogur")
                .build();

        when(categoriaService.obtenerTodos())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lácteos"))
                .andExpect(jsonPath("$[0].descripcion")
                        .value("Leche, queso y yogur"));
    }

    @Test
    public void testObtenerPorId_retorna200() throws Exception {
        CategoriaResponse response = CategoriaResponse.builder()
                .id(1L)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogur")
                .build();

        when(categoriaService.obtenerPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Lácteos"))
                .andExpect(jsonPath("$.descripcion")
                        .value("Leche, queso y yogur"));
    }

    @Test
    public void testCrear_retorna201() throws Exception {
        CategoriaResponse response = CategoriaResponse.builder()
                .id(1L)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogur")
                .build();

        when(categoriaService.guardar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Lácteos",
                                "descripcion": "Leche, queso y yogur"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Lácteos"))
                .andExpect(jsonPath("$.descripcion")
                        .value("Leche, queso y yogur"));
    }

    @Test
    public void testActualizar_retorna200() throws Exception {
        CategoriaResponse response = CategoriaResponse.builder()
                .id(1L)
                .nombre("Lácteos y Derivados")
                .descripcion("Leche, queso, yogur y mantequilla")
                .build();

        when(categoriaService.actualizar(any(Long.class), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombre": "Lácteos y Derivados",
                                "descripcion": "Leche, queso, yogur y mantequilla"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Lácteos y Derivados"));
    }

    @Test
    public void testEliminar_retorna200() throws Exception {
        doNothing().when(categoriaService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Categoría eliminada"));
    }
}