package com.videojuegos.biblioteca.service;

import com.videojuegos.biblioteca.client.JuegoClient;
import com.videojuegos.biblioteca.client.UsuarioClient;
import com.videojuegos.biblioteca.client.dto.JuegoDTO;
import com.videojuegos.biblioteca.client.dto.UsuarioDTO;
import com.videojuegos.biblioteca.dto.AdquirirJuegoRequestDTO;
import com.videojuegos.biblioteca.dto.EntradaBibliotecaResponseDTO;
import com.videojuegos.biblioteca.exception.ReglaNegocioException;
import com.videojuegos.biblioteca.model.EntradaBiblioteca;
import com.videojuegos.biblioteca.repository.BibliotecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de BibliotecaService.
 * Se mockean el repositorio y los clientes Feign (UsuarioClient, JuegoClient).
 */
@ExtendWith(MockitoExtension.class)
class BibliotecaServiceTest {

    @Mock
    private BibliotecaRepository repository;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private JuegoClient juegoClient;

    @InjectMocks
    private BibliotecaService service;

    private UsuarioDTO usuarioActivo() {
        UsuarioDTO u = new UsuarioDTO();
        u.setId(1L);
        u.setActivo(true);
        return u;
    }

    private JuegoDTO juego() {
        JuegoDTO j = new JuegoDTO();
        j.setId(2L);
        j.setTitulo("Hades");
        return j;
    }

    private EntradaBiblioteca entrada(Long id) {
        EntradaBiblioteca e = new EntradaBiblioteca();
        e.setId(id);
        e.setUsuarioId(1L);
        e.setJuegoId(2L);
        e.setTituloJuego("Hades");
        e.setFechaAdquisicion(LocalDateTime.now());
        e.setHorasJugadas(0);
        e.setInstalado(false);
        return e;
    }

    @Test
    void deberiaAdquirirJuegoCuandoNoLoPosee() {
        AdquirirJuegoRequestDTO dto = new AdquirirJuegoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);

        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuarioActivo());
        when(juegoClient.obtenerPorId(2L)).thenReturn(juego());
        when(repository.existsByUsuarioIdAndJuegoId(1L, 2L)).thenReturn(false);
        when(repository.save(any(EntradaBiblioteca.class))).thenAnswer(inv -> {
            EntradaBiblioteca e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        EntradaBibliotecaResponseDTO r = service.adquirir(dto);

        assertEquals(10L, r.getId());
        assertEquals("Hades", r.getTituloJuego());
    }

    @Test
    void deberiaLanzarReglaNegocioSiYaPoseeElJuego() {
        AdquirirJuegoRequestDTO dto = new AdquirirJuegoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);

        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuarioActivo());
        when(juegoClient.obtenerPorId(2L)).thenReturn(juego());
        when(repository.existsByUsuarioIdAndJuegoId(1L, 2L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.adquirir(dto));
        verify(repository, never()).save(any(EntradaBiblioteca.class));
    }

    @Test
    void deberiaIndicarSiElUsuarioPoseeElJuego() {
        when(repository.existsByUsuarioIdAndJuegoId(1L, 2L)).thenReturn(true);
        assertTrue(service.usuarioPoseeJuego(1L, 2L));
    }

    @Test
    void deberiaLanzarReglaNegocioSiLasHorasSonNegativas() {
        assertThrows(ReglaNegocioException.class, () -> service.registrarHoras(1L, -5));
    }

    @Test
    void deberiaListarLaBibliotecaDelUsuario() {
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(entrada(1L)));
        List<EntradaBibliotecaResponseDTO> r = service.listarPorUsuario(1L);
        assertEquals(1, r.size());
    }

    @Test
    void deberiaRegistrarHorasJugadas() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(entrada(1L)));
        when(repository.save(any(EntradaBiblioteca.class))).thenAnswer(inv -> inv.getArgument(0));

        EntradaBibliotecaResponseDTO r = service.registrarHoras(1L, 5);

        assertEquals(5, r.getHorasJugadas());
    }

    @Test
    void deberiaCambiarEstadoInstalado() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(entrada(1L)));
        when(repository.save(any(EntradaBiblioteca.class))).thenAnswer(inv -> inv.getArgument(0));

        EntradaBibliotecaResponseDTO r = service.cambiarInstalado(1L, true);

        assertTrue(r.isInstalado());
    }

    @Test
    void deberiaEliminarEntrada() {
        EntradaBiblioteca e = entrada(1L);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(e));

        service.eliminar(1L);

        verify(repository).delete(e);
    }

    @Test
    void deberiaLanzarReglaNegocioSiElUsuarioEstaInactivo() {
        AdquirirJuegoRequestDTO dto = new AdquirirJuegoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        UsuarioDTO inactivo = new UsuarioDTO();
        inactivo.setId(1L);
        inactivo.setActivo(false);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(inactivo);

        assertThrows(ReglaNegocioException.class, () -> service.adquirir(dto));
        verify(repository, never()).save(any(EntradaBiblioteca.class));
    }
}
