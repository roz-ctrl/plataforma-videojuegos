package com.videojuegos.logros.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.logros.dto.DesbloquearRequestDTO;
import com.videojuegos.logros.dto.LogroDesbloqueadoResponseDTO;
import com.videojuegos.logros.dto.LogroRequestDTO;
import com.videojuegos.logros.dto.LogroResponseDTO;
import com.videojuegos.logros.exception.RecursoNoEncontradoException;
import com.videojuegos.logros.exception.ReglaNegocioException;
import com.videojuegos.logros.service.LogroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogroController.class)
class LogroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LogroService service;

    private LogroResponseDTO logro(Long id) {
        LogroResponseDTO l = new LogroResponseDTO();
        l.setId(id);
        l.setJuegoId(2L);
        l.setNombre("Maestro Brujo");
        l.setDescripcion("desc");
        l.setPuntos(100);
        return l;
    }

    private LogroDesbloqueadoResponseDTO desbloqueado() {
        LogroDesbloqueadoResponseDTO d = new LogroDesbloqueadoResponseDTO();
        d.setId(10L);
        d.setUsuarioId(1L);
        d.setLogroId(1L);
        d.setLogroNombre("Maestro Brujo");
        d.setPuntos(100);
        d.setFechaDesbloqueo(LocalDateTime.now());
        return d;
    }

    @Test
    void deberiaListarLogrosPorJuego() throws Exception {
        when(service.listarPorJuego(2L)).thenReturn(List.of(logro(1L)));
        mockMvc.perform(get("/api/logros/juego/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Maestro Brujo"));
    }

    @Test
    void deberiaCrearLogroYRetornar201() throws Exception {
        LogroRequestDTO dto = new LogroRequestDTO();
        dto.setJuegoId(2L);
        dto.setNombre("Maestro Brujo");
        dto.setPuntos(100);
        when(service.crearLogro(any(LogroRequestDTO.class))).thenReturn(logro(5L));

        mockMvc.perform(post("/api/logros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void deberiaDesbloquearYRetornar201() throws Exception {
        DesbloquearRequestDTO dto = new DesbloquearRequestDTO();
        dto.setUsuarioId(1L);
        dto.setLogroId(1L);
        when(service.desbloquear(any(DesbloquearRequestDTO.class))).thenReturn(desbloqueado());

        mockMvc.perform(post("/api/logros/desbloquear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.logroNombre").value("Maestro Brujo"));
    }

    @Test
    void deberiaListarDesbloqueadosPorUsuario() throws Exception {
        when(service.listarDesbloqueadosPorUsuario(1L)).thenReturn(List.of(desbloqueado()));
        mockMvc.perform(get("/api/logros/usuario/1/desbloqueados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void deberiaEliminarLogroYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/logros/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar409CuandoYaDesbloqueado() throws Exception {
        DesbloquearRequestDTO dto = new DesbloquearRequestDTO();
        dto.setUsuarioId(1L);
        dto.setLogroId(1L);
        when(service.desbloquear(any(DesbloquearRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("El usuario ya desbloqueo el logro"));

        mockMvc.perform(post("/api/logros/desbloquear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }

    @Test
    void deberiaRetornar404AlEliminarLogroInexistente() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe el logro con id 99"))
                .when(service).eliminarLogro(99L);

        mockMvc.perform(delete("/api/logros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar400CuandoFaltanDatosDelLogro() throws Exception {
        LogroRequestDTO dto = new LogroRequestDTO(); // sin juegoId, nombre ni puntos

        mockMvc.perform(post("/api/logros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
