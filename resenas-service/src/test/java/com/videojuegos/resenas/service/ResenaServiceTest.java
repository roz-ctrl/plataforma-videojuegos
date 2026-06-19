package com.videojuegos.resenas.service;

import com.videojuegos.resenas.client.BibliotecaClient;
import com.videojuegos.resenas.client.JuegoClient;
import com.videojuegos.resenas.client.UsuarioClient;
import com.videojuegos.resenas.client.dto.JuegoDTO;
import com.videojuegos.resenas.client.dto.UsuarioDTO;
import com.videojuegos.resenas.dto.ResenaRequestDTO;
import com.videojuegos.resenas.dto.ResenaResponseDTO;
import com.videojuegos.resenas.dto.ResumenJuegoDTO;
import com.videojuegos.resenas.exception.ComunicacionException;
import com.videojuegos.resenas.exception.RecursoNoEncontradoException;
import com.videojuegos.resenas.exception.ReglaNegocioException;
import com.videojuegos.resenas.model.Resena;
import com.videojuegos.resenas.repository.ResenaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de ResenaService.
 * Se mockean el repositorio y los 3 clientes Feign (Usuario, Juego, Biblioteca).
 * La regla central probada: solo se puede resenar un juego que se posee.
 */
@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository repository;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private JuegoClient juegoClient;
    @Mock
    private BibliotecaClient bibliotecaClient;

    @InjectMocks
    private ResenaService service;

    private UsuarioDTO usuario() {
        UsuarioDTO u = new UsuarioDTO();
        u.setId(1L);
        u.setNombreUsuario("gabe");
        u.setActivo(true);
        return u;
    }

    private JuegoDTO juego() {
        JuegoDTO j = new JuegoDTO();
        j.setId(2L);
        j.setTitulo("The Witcher 3");
        return j;
    }

    private ResenaRequestDTO requestValido() {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        dto.setCalificacion(5);
        dto.setComentario("Excelente");
        dto.setRecomendado(true);
        return dto;
    }

    private Resena resena(Long id, int calificacion, boolean recomendado) {
        Resena r = new Resena();
        r.setId(id);
        r.setUsuarioId(1L);
        r.setJuegoId(2L);
        r.setCalificacion(calificacion);
        r.setRecomendado(recomendado);
        return r;
    }

    @Test
    void deberiaCrearResenaCuandoElUsuarioPoseeElJuego() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario());
        when(juegoClient.obtenerPorId(2L)).thenReturn(juego());
        when(bibliotecaClient.usuarioPoseeJuego(1L, 2L)).thenReturn(true);
        when(repository.existsByUsuarioIdAndJuegoId(1L, 2L)).thenReturn(false);
        when(repository.save(any(Resena.class))).thenAnswer(inv -> {
            Resena r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });

        ResenaResponseDTO r = service.crear(requestValido());

        assertEquals(10L, r.getId());
        assertEquals("gabe", r.getNombreUsuario());
        assertEquals("The Witcher 3", r.getTituloJuego());
    }

    @Test
    void deberiaLanzarReglaNegocioSiNoPoseeElJuego() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario());
        when(juegoClient.obtenerPorId(2L)).thenReturn(juego());
        when(bibliotecaClient.usuarioPoseeJuego(1L, 2L)).thenReturn(false);

        assertThrows(ReglaNegocioException.class, () -> service.crear(requestValido()));
        verify(repository, never()).save(any(Resena.class));
    }

    @Test
    void deberiaLanzarReglaNegocioSiYaResenoElJuego() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario());
        when(juegoClient.obtenerPorId(2L)).thenReturn(juego());
        when(bibliotecaClient.usuarioPoseeJuego(1L, 2L)).thenReturn(true);
        when(repository.existsByUsuarioIdAndJuegoId(1L, 2L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.crear(requestValido()));
        verify(repository, never()).save(any(Resena.class));
    }

    @Test
    void deberiaCalcularElResumenDeUnJuego() {
        when(repository.findByJuegoId(2L)).thenReturn(List.of(
                resena(1L, 4, true),
                resena(2L, 5, false)
        ));

        ResumenJuegoDTO r = service.resumenPorJuego(2L);

        assertEquals(2, r.getTotalResenas());
        assertEquals(4.5, r.getPromedioCalificacion());
        assertEquals(1, r.getTotalRecomiendan());
    }

    @Test
    void deberiaListarResenasPorJuego() {
        when(repository.findByJuegoId(2L)).thenReturn(List.of(resena(1L, 5, true)));
        when(usuarioClient.obtenerPorId(anyLong())).thenReturn(usuario());
        when(juegoClient.obtenerPorId(anyLong())).thenReturn(juego());

        List<ResenaResponseDTO> r = service.listarPorJuego(2L);

        assertEquals(1, r.size());
    }

    @Test
    void deberiaActualizarResena() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(resena(1L, 5, true)));
        when(repository.save(any(Resena.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioClient.obtenerPorId(anyLong())).thenReturn(usuario());
        when(juegoClient.obtenerPorId(anyLong())).thenReturn(juego());

        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        dto.setCalificacion(3);
        dto.setComentario("Actualizado");
        dto.setRecomendado(false);

        ResenaResponseDTO r = service.actualizar(1L, dto);

        assertEquals(3, r.getCalificacion());
    }

    @Test
    void deberiaEliminarResena() {
        Resena resena = resena(1L, 5, true);
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(resena));

        service.eliminar(1L);

        verify(repository).delete(resena);
    }

    @Test
    void deberiaLanzarReglaNegocioSiElUsuarioEstaInactivo() {
        UsuarioDTO inactivo = new UsuarioDTO();
        inactivo.setId(1L);
        inactivo.setActivo(false);
        when(usuarioClient.obtenerPorId(1L)).thenReturn(inactivo);

        assertThrows(ReglaNegocioException.class, () -> service.crear(requestValido()));
        verify(repository, never()).save(any(Resena.class));
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoAlActualizarInexistente() {
        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> service.actualizar(99L, requestValido()));
    }

    @Test
    void deberiaLanzarRecursoNoEncontradoAlEliminarInexistente() {
        when(repository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.eliminar(99L));
    }

    @Test
    void deberiaLanzarComunicacionSiFallaElServicioDeBiblioteca() {
        when(usuarioClient.obtenerPorId(1L)).thenReturn(usuario());
        when(juegoClient.obtenerPorId(2L)).thenReturn(juego());
        // Simula que biblioteca-service responde con error (503)
        feign.Request req = feign.Request.create(
                feign.Request.HttpMethod.GET, "http://biblioteca",
                java.util.Collections.emptyMap(), null,
                java.nio.charset.StandardCharsets.UTF_8, null);
        feign.Response resp = feign.Response.builder()
                .status(503).reason("down").request(req)
                .headers(java.util.Collections.emptyMap()).build();
        when(bibliotecaClient.usuarioPoseeJuego(1L, 2L))
                .thenThrow(feign.FeignException.errorStatus("usuarioPoseeJuego", resp));

        assertThrows(ComunicacionException.class, () -> service.crear(requestValido()));
    }
}
