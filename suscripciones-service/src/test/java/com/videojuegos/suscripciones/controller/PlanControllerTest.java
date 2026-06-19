package com.videojuegos.suscripciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.suscripciones.dto.PlanRequestDTO;
import com.videojuegos.suscripciones.dto.PlanResponseDTO;
import com.videojuegos.suscripciones.service.PlanService;
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

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanService service;

    private PlanResponseDTO resp(Long id, String nombre) {
        PlanResponseDTO p = new PlanResponseDTO();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecioMensual(new BigDecimal("5990"));
        p.setDuracionMeses(1);
        p.setActivo(true);
        return p;
    }

    @Test
    void deberiaListar() throws Exception {
        when(service.listarTodos()).thenReturn(List.of(resp(1L, "Pase Mensual")));
        mockMvc.perform(get("/api/planes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Pase Mensual"));
    }

    @Test
    void deberiaCrearYRetornar201() throws Exception {
        PlanRequestDTO dto = new PlanRequestDTO();
        dto.setNombre("Pase Semestral");
        dto.setPrecioMensual(new BigDecimal("4990"));
        dto.setDuracionMeses(6);
        when(service.crear(any(PlanRequestDTO.class))).thenReturn(resp(4L, "Pase Semestral"));

        mockMvc.perform(post("/api/planes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));
    }

    @Test
    void deberiaEliminarYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/planes/1"))
                .andExpect(status().isNoContent());
    }
}
