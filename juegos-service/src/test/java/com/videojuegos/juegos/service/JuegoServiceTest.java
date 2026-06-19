package com.videojuegos.juegos.service;

import com.videojuegos.juegos.client.CategoriaClient;
import com.videojuegos.juegos.client.DesarrolladoraClient;
import com.videojuegos.juegos.client.dto.CategoriaDTO;
import com.videojuegos.juegos.client.dto.DesarrolladoraDTO;
import com.videojuegos.juegos.dto.JuegoRequestDTO;
import com.videojuegos.juegos.dto.JuegoResponseDTO;
import com.videojuegos.juegos.exception.RecursoNoEncontradoException;
import com.videojuegos.juegos.exception.ReglaNegocioException;
import com.videojuegos.juegos.model.Juego;
import com.videojuegos.juegos.repository.JuegoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de JuegoService.
 * Ademas del repositorio, se mockean los clientes Feign (DesarrolladoraClient y
 * CategoriaClient), por lo que NO se levantan los servicios remotos.
 */
@ExtendWith(MockitoExtension.class)
class JuegoServiceTest {

    @Mock
    private JuegoRepository repository;

    @Mock
    private DesarrolladoraClient desarrolladoraClient;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private JuegoService service;

    private Juego nuevoJuego(Long id) {
        Juego j = new Juego();
        j.setId(id);
        j.setTitulo("Elden Ring");
        j.setPrecio(new BigDecimal("40000"));
        j.setDesarrolladoraId(2L);
        j.setCategoriaId(1L);
        j.setDescuentoPorcentaje(0);
        j.setActivo(true);
        return j;
    }

    private DesarrolladoraDTO desarrolladoraActiva() {
        DesarrolladoraDTO d = new DesarrolladoraDTO();
        d.setId(2L);
        d.setNombre("CD Projekt Red");
        d.setActiva(true);
        return d;
    }

    private CategoriaDTO categoria() {
        CategoriaDTO c = new CategoriaDTO();
        c.setId(1L);
        c.setNombre("RPG");
        return c;
    }

    @Test
    void deberiaCrearJuegoCuandoDesarrolladoraYCategoriaExisten() {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setTitulo("Elden Ring");
        dto.setPrecio(new BigDecimal("40000"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);
        dto.setDescuentoPorcentaje(0);

        when(desarrolladoraClient.obtenerPorId(2L)).thenReturn(desarrolladoraActiva());
        when(categoriaClient.obtenerPorId(1L)).thenReturn(categoria());
        when(repository.save(any(Juego.class))).thenAnswer(inv -> {
            Juego j = inv.getArgument(0);
            j.setId(5L);
            return j;
        });

        JuegoResponseDTO r = service.crear(dto);

        assertEquals(5L, r.getId());
        assertEquals("CD Projekt Red", r.getDesarrolladoraNombre());
        verify(repository).save(any(Juego.class));
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoDesarrolladoraEstaInactiva() {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setTitulo("Juego X");
        dto.setPrecio(new BigDecimal("10000"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);

        DesarrolladoraDTO inactiva = desarrolladoraActiva();
        inactiva.setActiva(false);
        when(desarrolladoraClient.obtenerPorId(2L)).thenReturn(inactiva);

        assertThrows(ReglaNegocioException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Juego.class));
    }

    @Test
    void deberiaObtenerJuegoPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(nuevoJuego(1L)));
        when(desarrolladoraClient.obtenerPorId(2L)).thenReturn(desarrolladoraActiva());
        when(categoriaClient.obtenerPorId(1L)).thenReturn(categoria());

        JuegoResponseDTO r = service.obtenerPorId(1L);

        assertEquals("Elden Ring", r.getTitulo());
        assertEquals("RPG", r.getCategoriaNombre());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoCuandoJuegoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void deberiaEliminarJuego() {
        Juego j = nuevoJuego(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(j));
        service.eliminar(1L);
        verify(repository).delete(j);
    }

    @Test
    void deberiaListarJuegos() {
        when(repository.findAll()).thenReturn(java.util.List.of(nuevoJuego(1L)));
        when(desarrolladoraClient.obtenerPorId(2L)).thenReturn(desarrolladoraActiva());
        when(categoriaClient.obtenerPorId(1L)).thenReturn(categoria());

        assertEquals(1, service.listarTodos().size());
    }

    @Test
    void deberiaActualizarJuego() {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setTitulo("The Witcher 3 GOTY");
        dto.setPrecio(new BigDecimal("15000"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);
        dto.setDescuentoPorcentaje(10);
        when(repository.findById(1L)).thenReturn(Optional.of(nuevoJuego(1L)));
        when(desarrolladoraClient.obtenerPorId(2L)).thenReturn(desarrolladoraActiva());
        when(categoriaClient.obtenerPorId(1L)).thenReturn(categoria());
        when(repository.save(any(Juego.class))).thenAnswer(inv -> inv.getArgument(0));

        JuegoResponseDTO r = service.actualizar(1L, dto);

        assertEquals("The Witcher 3 GOTY", r.getTitulo());
    }
}
