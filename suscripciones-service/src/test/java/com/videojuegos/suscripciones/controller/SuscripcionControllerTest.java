package com.videojuegos.suscripciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.suscripciones.dto.SuscripcionRequestDTO;
import com.videojuegos.suscripciones.dto.SuscripcionResponseDTO;
import com.videojuegos.suscripciones.exception.RecursoNoEncontradoException;
import com.videojuegos.suscripciones.exception.ReglaNegocioException;
import com.videojuegos.suscripciones.service.SuscripcionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SuscripcionController.class)
class SuscripcionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SuscripcionService service;

    private SuscripcionResponseDTO resp(String estado) {
        SuscripcionResponseDTO s = new SuscripcionResponseDTO();
        s.setId(1L);
        s.setUsuarioId(1L);
        s.setPlanId(1L);
        s.setPlanNombre("Pase Mensual");
        s.setFechaInicio(LocalDate.now());
        s.setFechaFin(LocalDate.now().plusMonths(1));
        s.setEstado(estado);
        s.setRenovacionAutomatica(true);
        return s;
    }

    @Test
    void deberiaSuscribirYRetornar201() throws Exception {
        SuscripcionRequestDTO dto = new SuscripcionRequestDTO();
        dto.setUsuarioId(1L);
        dto.setPlanId(1L);
        dto.setRenovacionAutomatica(true);
        when(service.suscribir(any(SuscripcionRequestDTO.class))).thenReturn(resp("ACTIVA"));

        mockMvc.perform(post("/api/suscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void deberiaListarPorUsuario() throws Exception {
        when(service.listarPorUsuario(1L)).thenReturn(List.of(resp("ACTIVA")));
        mockMvc.perform(get("/api/suscripciones/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planNombre").value("Pase Mensual"));
    }

    @Test
    void deberiaCancelar() throws Exception {
        when(service.cancelar(anyLong())).thenReturn(resp("CANCELADA"));
        mockMvc.perform(put("/api/suscripciones/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    void deberiaRetornar404CuandoNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe la suscripcion con id 99"));

        mockMvc.perform(get("/api/suscripciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoYaTieneSuscripcionActiva() throws Exception {
        SuscripcionRequestDTO dto = new SuscripcionRequestDTO();
        dto.setUsuarioId(1L);
        dto.setPlanId(1L);
        when(service.suscribir(any(SuscripcionRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("El usuario ya tiene una suscripcion activa"));

        mockMvc.perform(post("/api/suscripciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }
}
