package com.videojuegos.desarrolladoras.service;

import com.videojuegos.desarrolladoras.dto.DesarrolladoraRequestDTO;
import com.videojuegos.desarrolladoras.dto.DesarrolladoraResponseDTO;
import com.videojuegos.desarrolladoras.exception.RecursoNoEncontradoException;
import com.videojuegos.desarrolladoras.exception.ReglaNegocioException;
import com.videojuegos.desarrolladoras.model.Desarrolladora;
import com.videojuegos.desarrolladoras.repository.DesarrolladoraRepository;
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
class DesarrolladoraServiceTest {

    @Mock
    private DesarrolladoraRepository repository;

    @InjectMocks
    private DesarrolladoraService service;

    private Desarrolladora nueva(Long id, String nombre) {
        Desarrolladora d = new Desarrolladora();
        d.setId(id);
        d.setNombre(nombre);
        d.setPaisOrigen("Chile");
        d.setActiva(true);
        return d;
    }

    @Test
    void deberiaCrearDesarrolladoraCuandoNombreEsUnico() {
        DesarrolladoraRequestDTO dto = new DesarrolladoraRequestDTO();
        dto.setNombre("FromSoftware");
        when(repository.existsByNombre("FromSoftware")).thenReturn(false);
        when(repository.save(any(Desarrolladora.class))).thenAnswer(inv -> {
            Desarrolladora d = inv.getArgument(0);
            d.setId(5L);
            return d;
        });

        DesarrolladoraResponseDTO r = service.crear(dto);

        assertEquals(5L, r.getId());
        assertEquals("FromSoftware", r.getNombre());
        verify(repository).save(any(Desarrolladora.class));
    }

    @Test
    void deberiaLanzarReglaNegocioCuandoNombreYaExiste() {
        DesarrolladoraRequestDTO dto = new DesarrolladoraRequestDTO();
        dto.setNombre("Valve");
        when(repository.existsByNombre("Valve")).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(dto));
        verify(repository, never()).save(any(Desarrolladora.class));
    }

    @Test
    void deberiaObtenerDesarrolladoraPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(nueva(1L, "Valve")));

        DesarrolladoraResponseDTO r = service.obtenerPorId(1L);

        assertEquals("Valve", r.getNombre());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void deberiaListarDesarrolladoras() {
        when(repository.findAll()).thenReturn(List.of(nueva(1L, "Valve"), nueva(2L, "Nintendo")));

        assertEquals(2, service.listarTodas().size());
    }

    @Test
    void deberiaEliminarDesarrolladora() {
        Desarrolladora d = nueva(1L, "Valve");
        when(repository.findById(1L)).thenReturn(Optional.of(d));

        service.eliminar(1L);

        verify(repository).delete(d);
    }

    @Test
    void deberiaActualizarDesarrolladora() {
        Desarrolladora d = nueva(1L, "Valve");
        DesarrolladoraRequestDTO dto = new DesarrolladoraRequestDTO();
        dto.setNombre("Valve"); // mismo nombre: no revisa unicidad
        dto.setPaisOrigen("Estados Unidos");
        when(repository.findById(1L)).thenReturn(Optional.of(d));
        when(repository.save(any(Desarrolladora.class))).thenAnswer(inv -> inv.getArgument(0));

        DesarrolladoraResponseDTO r = service.actualizar(1L, dto);

        assertEquals("Estados Unidos", r.getPaisOrigen());
    }
}
