package com.videojuegos.juegos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.juegos.dto.JuegoRequestDTO;
import com.videojuegos.juegos.dto.JuegoResponseDTO;
import com.videojuegos.juegos.exception.RecursoNoEncontradoException;
import com.videojuegos.juegos.exception.ReglaNegocioException;
import com.videojuegos.juegos.service.JuegoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JuegoController.class)
class JuegoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JuegoService service;

    private JuegoResponseDTO resp(Long id, String titulo) {
        JuegoResponseDTO j = new JuegoResponseDTO();
        j.setId(id);
        j.setTitulo(titulo);
        j.setPrecio(new BigDecimal("19990"));
        j.setPrecioFinal(new BigDecimal("19990"));
        j.setActivo(true);
        return j;
    }

    @Test
    void deberiaListar() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(resp(1L, "The Witcher 3")));
        mockMvc.perform(get("/api/juegos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("The Witcher 3"));
    }

    @Test
    void deberiaObtenerPorId() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(resp(1L, "The Witcher 3"));
        mockMvc.perform(get("/api/juegos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaCrearYRetornar201() throws Exception {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setTitulo("Elden Ring");
        dto.setPrecio(new BigDecimal("40000"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);
        when(service.crear(any(JuegoRequestDTO.class))).thenReturn(resp(5L, "Elden Ring"));

        mockMvc.perform(post("/api/juegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void deberiaRetornar400CuandoFaltaTitulo() throws Exception {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setPrecio(new BigDecimal("40000"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);
        // sin titulo -> @NotBlank falla

        mockMvc.perform(post("/api/juegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaEliminarYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/juegos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaActualizarYRetornar200() throws Exception {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setTitulo("The Witcher 3");
        dto.setPrecio(new BigDecimal("19990"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);
        when(service.actualizar(any(Long.class), any(JuegoRequestDTO.class))).thenReturn(resp(2L, "The Witcher 3"));

        mockMvc.perform(put("/api/juegos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void deberiaRetornar404CuandoNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe el juego con id 99"));

        mockMvc.perform(get("/api/juegos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoReglaNegocio() throws Exception {
        JuegoRequestDTO dto = new JuegoRequestDTO();
        dto.setTitulo("Juego X");
        dto.setPrecio(new BigDecimal("10000"));
        dto.setDesarrolladoraId(2L);
        dto.setCategoriaId(1L);
        when(service.crear(any(JuegoRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("La desarrolladora esta inactiva"));

        mockMvc.perform(post("/api/juegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }
}
