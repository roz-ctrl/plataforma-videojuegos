package com.videojuegos.categorias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.categorias.dto.CategoriaRequestDTO;
import com.videojuegos.categorias.dto.CategoriaResponseDTO;
import com.videojuegos.categorias.exception.RecursoNoEncontradoException;
import com.videojuegos.categorias.exception.ReglaNegocioException;
import com.videojuegos.categorias.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService service;

    private CategoriaResponseDTO resp(Long id, String nombre) {
        CategoriaResponseDTO c = new CategoriaResponseDTO();
        c.setId(id);
        c.setNombre(nombre);
        c.setDescripcion("desc");
        return c;
    }

    @Test
    void deberiaListar() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(resp(1L, "RPG")));
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("RPG"));
    }

    @Test
    void deberiaObtenerPorId() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(resp(1L, "RPG"));
        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaCrearYRetornar201() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("Carreras");
        when(service.crear(any(CategoriaRequestDTO.class))).thenReturn(resp(6L, "Carreras"));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(6));
    }

    @Test
    void deberiaEliminarYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaActualizarYRetornar200() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("RPG");
        when(service.actualizar(any(Long.class), any(CategoriaRequestDTO.class)))
                .thenReturn(resp(1L, "RPG"));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRetornar404CuandoNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe la categoria con id 99"));

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoNombreDuplicado() throws Exception {
        CategoriaRequestDTO dto = new CategoriaRequestDTO();
        dto.setNombre("RPG");
        when(service.crear(any(CategoriaRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("Ya existe una categoria con el nombre RPG"));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }
}
