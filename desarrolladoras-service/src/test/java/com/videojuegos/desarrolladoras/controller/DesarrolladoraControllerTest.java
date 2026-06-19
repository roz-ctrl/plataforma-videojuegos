package com.videojuegos.desarrolladoras.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.desarrolladoras.dto.DesarrolladoraRequestDTO;
import com.videojuegos.desarrolladoras.dto.DesarrolladoraResponseDTO;
import com.videojuegos.desarrolladoras.exception.RecursoNoEncontradoException;
import com.videojuegos.desarrolladoras.exception.ReglaNegocioException;
import com.videojuegos.desarrolladoras.service.DesarrolladoraService;
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

@WebMvcTest(DesarrolladoraController.class)
class DesarrolladoraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DesarrolladoraService service;

    private DesarrolladoraResponseDTO resp(Long id, String nombre) {
        DesarrolladoraResponseDTO d = new DesarrolladoraResponseDTO();
        d.setId(id);
        d.setNombre(nombre);
        d.setPaisOrigen("Chile");
        d.setActiva(true);
        return d;
    }

    @Test
    void deberiaListar() throws Exception {
        when(service.listarTodas()).thenReturn(List.of(resp(1L, "Valve")));

        mockMvc.perform(get("/api/desarrolladoras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Valve"));
    }

    @Test
    void deberiaObtenerPorId() throws Exception {
        when(service.obtenerPorId(1L)).thenReturn(resp(1L, "Valve"));

        mockMvc.perform(get("/api/desarrolladoras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaCrearYRetornar201() throws Exception {
        DesarrolladoraRequestDTO dto = new DesarrolladoraRequestDTO();
        dto.setNombre("FromSoftware");
        when(service.crear(any(DesarrolladoraRequestDTO.class))).thenReturn(resp(5L, "FromSoftware"));

        mockMvc.perform(post("/api/desarrolladoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void deberiaEliminarYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/desarrolladoras/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaActualizarYRetornar200() throws Exception {
        DesarrolladoraRequestDTO dto = new DesarrolladoraRequestDTO();
        dto.setNombre("Valve");
        when(service.actualizar(any(Long.class), any(DesarrolladoraRequestDTO.class)))
                .thenReturn(resp(1L, "Valve"));

        mockMvc.perform(put("/api/desarrolladoras/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRetornar404CuandoNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe la desarrolladora con id 99"));

        mockMvc.perform(get("/api/desarrolladoras/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoNombreDuplicado() throws Exception {
        DesarrolladoraRequestDTO dto = new DesarrolladoraRequestDTO();
        dto.setNombre("Valve");
        when(service.crear(any(DesarrolladoraRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("Ya existe una desarrolladora con el nombre Valve"));

        mockMvc.perform(post("/api/desarrolladoras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }
}
