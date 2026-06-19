package com.videojuegos.categorias.service;

import com.videojuegos.categorias.dto.CategoriaRequestDTO;
import com.videojuegos.categorias.dto.CategoriaResponseDTO;
import com.videojuegos.categorias.exception.RecursoNoEncontradoException;
import com.videojuegos.categorias.exception.ReglaNegocioException;
import com.videojuegos.categorias.model.Categoria;
import com.videojuegos.categorias.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    private Categoria nueva(Long id, String nombre) {
        Categoria c = new Categoria();
        c.setId(id);
        c.setNombre(nombre);
        c.setDescripcion("desc");
        return c;
    }

    @Test
    void deberiaCrearCategoriaCuandoNombreEsUnico() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Carreras");
        when(repository.existsByNombre("Carreras")).thenReturn(false);
        when(repository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setId(6L);
            return c;
        });

        CategoriaResponseDTO r = service.crear(dto);

        assertEquals(6L, r.getId());
        assertEquals("Carreras", r.getNombre());
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoNombreYaExiste() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("RPG");
        when(repository.existsByNombre("RPG")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    void deberiaObtenerCategoriaPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(nueva(1L, "RPG")));
        assertEquals("RPG", service.obtenerPorId(1L).getNombre());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void deberiaListarCategorias() {
        when(repository.findAll()).thenReturn(List.of(nueva(1L, "RPG"), nueva(2L, "Shooter")));
        assertEquals(2, service.listarTodas().size());
    }

    @Test
    void deberiaEliminarCategoria() {
        Categoria c = nueva(1L, "RPG");
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        service.eliminar(1L);
        verify(repository).delete(c);
    }

    @Test
    void deberiaActualizarCategoria() {
        Categoria c = nueva(1L, "RPG");
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("RPG"); // mismo nombre: no revisa unicidad
        dto.setDescripcion("Juegos de rol actualizados");
        when(repository.findById(1L)).thenReturn(Optional.of(c));
        when(repository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponseDTO r = service.actualizar(1L, dto);

        assertEquals("Juegos de rol actualizados", r.getDescripcion());
    }
}
