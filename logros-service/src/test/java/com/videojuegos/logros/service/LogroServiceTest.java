package com.videojuegos.logros.service;

import com.videojuegos.logros.client.JuegoClient;
import com.videojuegos.logros.client.UsuarioClient;
import com.videojuegos.logros.dto.DesbloquearRequestDTO;
import com.videojuegos.logros.dto.LogroDesbloqueadoResponseDTO;
import com.videojuegos.logros.dto.LogroRequestDTO;
import com.videojuegos.logros.dto.LogroResponseDTO;
import com.videojuegos.logros.exception.RecursoNoEncontradoException;
import com.videojuegos.logros.exception.ReglaNegocioException;
import com.videojuegos.logros.model.Logro;
import com.videojuegos.logros.model.LogroDesbloqueado;
import com.videojuegos.logros.repository.LogroDesbloqueadoRepository;
import com.videojuegos.logros.repository.LogroRepository;
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
class LogroServiceTest {

    @Mock
    private LogroRepository logroRepository;
    @Mock
    private LogroDesbloqueadoRepository desbloqueadoRepository;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private JuegoClient juegoClient;

    @InjectMocks
    private LogroService service;

    private Logro logro(Long id) {
        Logro l = new Logro();
        l.setId(id);
        l.setJuegoId(2L);
        l.setNombre("Maestro Brujo");
        l.setDescripcion("Alcanza el nivel 50");
        l.setPuntos(100);
        return l;
    }

    @Test
    void deberiaCrearLogro() {
        LogroRequestDTO dto = new LogroRequestDTO();
        dto.setJuegoId(2L);
        dto.setNombre("Maestro Brujo");
        dto.setDescripcion("Alcanza el nivel 50");
        dto.setPuntos(100);
        when(logroRepository.save(any(Logro.class))).thenAnswer(inv -> {
            Logro l = inv.getArgument(0);
            l.setId(5L);
            return l;
        });

        LogroResponseDTO r = service.crearLogro(dto);

        assertEquals(5L, r.getId());
        assertEquals("Maestro Brujo", r.getNombre());
    }

    @Test
    void deberiaDesbloquearLogroParaUnUsuario() {
        DesbloquearRequestDTO dto = new DesbloquearRequestDTO();
        dto.setUsuarioId(1L);
        dto.setLogroId(1L);

        when(logroRepository.findById(1L)).thenReturn(Optional.of(logro(1L)));
        when(desbloqueadoRepository.existsByUsuarioIdAndLogroId(1L, 1L)).thenReturn(false);
        when(desbloqueadoRepository.save(any(LogroDesbloqueado.class))).thenAnswer(inv -> {
            LogroDesbloqueado d = inv.getArgument(0);
            d.setId(10L);
            return d;
        });

        LogroDesbloqueadoResponseDTO r = service.desbloquear(dto);

        assertEquals(10L, r.getId());
        assertEquals("Maestro Brujo", r.getLogroNombre());
    }

    @Test
    void deberiaLanzarReglaNegocioSiYaDesbloqueoElLogro() {
        DesbloquearRequestDTO dto = new DesbloquearRequestDTO();
        dto.setUsuarioId(1L);
        dto.setLogroId(1L);

        when(logroRepository.findById(1L)).thenReturn(Optional.of(logro(1L)));
        when(desbloqueadoRepository.existsByUsuarioIdAndLogroId(1L, 1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.desbloquear(dto));
        verify(desbloqueadoRepository, never()).save(any(LogroDesbloqueado.class));
    }

    @Test
    void deberiaListarLogrosPorJuego() {
        when(logroRepository.findByJuegoId(2L)).thenReturn(List.of(logro(1L), logro(2L)));
        assertEquals(2, service.listarPorJuego(2L).size());
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoAlEliminarLogroInexistente() {
        when(logroRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminarLogro(99L));
    }

    @Test
    void deberiaListarDesbloqueadosPorUsuario() {
        LogroDesbloqueado d = new LogroDesbloqueado();
        d.setId(1L);
        d.setUsuarioId(1L);
        d.setLogro(logro(1L));
        d.setFechaDesbloqueo(java.time.LocalDateTime.now());
        when(desbloqueadoRepository.findByUsuarioId(1L)).thenReturn(java.util.List.of(d));

        assertEquals(1, service.listarDesbloqueadosPorUsuario(1L).size());
    }

    @Test
    void deberiaEliminarLogroExistente() {
        when(logroRepository.findById(1L)).thenReturn(Optional.of(logro(1L)));

        service.eliminarLogro(1L);

        verify(logroRepository).delete(any(Logro.class));
    }
}
