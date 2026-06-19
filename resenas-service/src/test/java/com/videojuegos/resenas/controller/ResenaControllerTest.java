package com.videojuegos.resenas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.resenas.dto.ResenaRequestDTO;
import com.videojuegos.resenas.dto.ResenaResponseDTO;
import com.videojuegos.resenas.dto.ResumenJuegoDTO;
import com.videojuegos.resenas.exception.RecursoNoEncontradoException;
import com.videojuegos.resenas.exception.ReglaNegocioException;
import com.videojuegos.resenas.service.ResenaService;
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

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResenaService service;

    private ResenaResponseDTO resp(Long id) {
        ResenaResponseDTO r = new ResenaResponseDTO();
        r.setId(id);
        r.setUsuarioId(1L);
        r.setJuegoId(2L);
        r.setCalificacion(5);
        r.setComentario("Excelente");
        r.setRecomendado(true);
        r.setFecha(LocalDateTime.now());
        r.setNombreUsuario("gabe");
        r.setTituloJuego("The Witcher 3");
        return r;
    }

    @Test
    void deberiaListarPorJuego() throws Exception {
        when(service.listarPorJuego(2L)).thenReturn(List.of(resp(1L)));
        mockMvc.perform(get("/api/resenas/juego/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calificacion").value(5));
    }

    @Test
    void deberiaObtenerResumen() throws Exception {
        when(service.resumenPorJuego(2L)).thenReturn(new ResumenJuegoDTO(2L, 3, 4.7, 2));
        mockMvc.perform(get("/api/resenas/juego/2/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResenas").value(3))
                .andExpect(jsonPath("$.promedioCalificacion").value(4.7));
    }

    @Test
    void deberiaCrearResenaYRetornar201() throws Exception {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        dto.setCalificacion(5);
        dto.setComentario("Excelente");
        dto.setRecomendado(true);
        when(service.crear(any(ResenaRequestDTO.class))).thenReturn(resp(10L));

        mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void deberiaRetornar400CuandoLaCalificacionEsInvalida() throws Exception {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        dto.setCalificacion(9); // fuera de rango 1-5
        dto.setRecomendado(true);

        mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaActualizarYRetornar200() throws Exception {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        dto.setCalificacion(4);
        dto.setComentario("Actualizado");
        dto.setRecomendado(true);
        when(service.actualizar(any(Long.class), any(ResenaRequestDTO.class))).thenReturn(resp(1L));

        mockMvc.perform(put("/api/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaEliminarYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/resenas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar409CuandoNoPoseeElJuego() throws Exception {
        ResenaRequestDTO dto = new ResenaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(2L);
        dto.setCalificacion(5);
        dto.setRecomendado(true);
        when(service.crear(any(ResenaRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("No puede resenar un juego que no posee"));

        mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }

    @Test
    void deberiaRetornar404AlEliminarInexistente() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe la resena con id 99"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/resenas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }
}
