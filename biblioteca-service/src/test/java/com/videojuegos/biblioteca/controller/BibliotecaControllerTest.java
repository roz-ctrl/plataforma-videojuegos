package com.videojuegos.biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.biblioteca.dto.AdquirirJuegoRequestDTO;
import com.videojuegos.biblioteca.dto.EntradaBibliotecaResponseDTO;
import com.videojuegos.biblioteca.exception.RecursoNoEncontradoException;
import com.videojuegos.biblioteca.exception.ReglaNegocioException;
import com.videojuegos.biblioteca.service.BibliotecaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BibliotecaController.class)
class BibliotecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BibliotecaService service;

    private EntradaBibliotecaResponseDTO resp(Long id) {
        EntradaBibliotecaResponseDTO e = new EntradaBibliotecaResponseDTO();
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
    void deberiaListarPorUsuario() throws Exception {
        when(service.listarPorUsuario(1L)).thenReturn(List.of(resp(1L)));
        mockMvc.perform(get("/api/biblioteca/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].juegoId").value(2));
    }

    @Test
    void deberiaIndicarPosesion() throws Exception {
        when(service.usuarioPoseeJuego(1L, 2L)).thenReturn(true);
        mockMvc.perform(get("/api/biblioteca/usuario/1/posee/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void deberiaAdquirirYRetornar201() throws Exception {
        AdquirirJuegoRequestDTO dto = new AdquirirJuegoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        when(service.adquirir(any(AdquirirJuegoRequestDTO.class))).thenReturn(resp(10L));

        mockMvc.perform(post("/api/biblioteca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deberiaEliminarYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/biblioteca/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRegistrarHoras() throws Exception {
        when(service.registrarHoras(any(Long.class), anyInt())).thenReturn(resp(1L));
        mockMvc.perform(patch("/api/biblioteca/1/horas").param("horas", "3"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaCambiarInstalado() throws Exception {
        when(service.cambiarInstalado(any(Long.class), anyBoolean())).thenReturn(resp(1L));
        mockMvc.perform(patch("/api/biblioteca/1/instalado").param("valor", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar409CuandoYaPoseeElJuego() throws Exception {
        AdquirirJuegoRequestDTO dto = new AdquirirJuegoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        when(service.adquirir(any(AdquirirJuegoRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("El usuario ya posee el juego"));

        mockMvc.perform(post("/api/biblioteca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }

    @Test
    void deberiaRetornar404AlEliminarInexistente() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe la entrada"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/biblioteca/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar400CuandoFaltanDatos() throws Exception {
        AdquirirJuegoRequestDTO dto = new AdquirirJuegoRequestDTO(); // sin usuarioId ni juegoId

        mockMvc.perform(post("/api/biblioteca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
