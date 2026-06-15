package com.almacen.categorias_service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.almacen.categorias_service.dtos.request.CategoriaRequest;
import com.almacen.categorias_service.dtos.response.CategoriaResponse;
import com.almacen.categorias_service.models.CategoriaModel;
import com.almacen.categorias_service.repositories.CategoriaRepository;
import com.almacen.categorias_service.services.CategoriaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class CategoriasServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @MockBean
    private CategoriaRepository categoriaRepository;

    @Test
    public void testObtenerTodos() {
        CategoriaModel categoria = CategoriaModel.builder()
                .id(1L)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogurt")
                .build();

        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<CategoriaResponse> categorias = categoriaService.obtenerTodos();

        assertNotNull(categorias);
        assertEquals(1, categorias.size());
        assertEquals("Lácteos", categorias.get(0).getNombre());
    }

    @Test
    public void testObtenerPorId() {
        Long id = 1L;

        CategoriaModel categoria = CategoriaModel.builder()
                .id(id)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogurt")
                .build();

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));

        CategoriaResponse encontrada = categoriaService.obtenerPorId(id);

        assertNotNull(encontrada);
        assertEquals(id, encontrada.getId());
        assertEquals("Lácteos", encontrada.getNombre());
    }

    @Test
    public void testGuardar() {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre("Lácteos");
        request.setDescripcion("Leche, queso y yogurt");

        CategoriaModel categoriaGuardada = CategoriaModel.builder()
                .id(1L)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogurt")
                .build();

        when(categoriaRepository.save(any(CategoriaModel.class))).thenReturn(categoriaGuardada);

        CategoriaResponse guardada = categoriaService.guardar(request);

        assertNotNull(guardada);
        assertEquals(1L, guardada.getId());
        assertEquals("Lácteos", guardada.getNombre());
    }

    @Test
    public void testEliminar() {
        Long id = 1L;

        CategoriaModel categoria = CategoriaModel.builder()
                .id(id)
                .nombre("Lácteos")
                .descripcion("Leche, queso y yogurt")
                .build();

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).delete(categoria);

        categoriaService.eliminar(id);

        verify(categoriaRepository, times(1)).delete(categoria);
    }
}