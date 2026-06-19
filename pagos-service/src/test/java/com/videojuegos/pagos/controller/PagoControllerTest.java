package com.videojuegos.pagos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.pagos.dto.PagoResponseDTO;
import com.videojuegos.pagos.dto.ProcesarPagoRequestDTO;
import com.videojuegos.pagos.exception.RecursoNoEncontradoException;
import com.videojuegos.pagos.exception.ReglaNegocioException;
import com.videojuegos.pagos.service.PagoService;
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

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagoService service;

    private PagoResponseDTO resp() {
        PagoResponseDTO p = new PagoResponseDTO();
        p.setId(100L);
        p.setUsuarioId(1L);
        p.setMonto(new BigDecimal("19990"));
        p.setMetodoPago("SALDO");
        p.setEstado("COMPLETADO");
        p.setReferencia("TX-ABC123");
        p.setDetalles(List.of());
        return p;
    }

    @Test
    void deberiaProcesarPagoYRetornar201() throws Exception {
        ProcesarPagoRequestDTO dto = new ProcesarPagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMetodoPago("SALDO");
        when(service.procesarPago(any(ProcesarPagoRequestDTO.class))).thenReturn(resp());

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    void deberiaRetornar400CuandoMetodoPagoEsInvalido() throws Exception {
        ProcesarPagoRequestDTO dto = new ProcesarPagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMetodoPago("PAYPAL"); // no permitido (solo SALDO o TARJETA)

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaObtenerPagoPorId() throws Exception {
        when(service.obtenerPorId(100L)).thenReturn(resp());
        mockMvc.perform(get("/api/pagos/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void deberiaListarHistorialDelUsuario() throws Exception {
        when(service.listarPorUsuario(1L)).thenReturn(List.of(resp()));
        mockMvc.perform(get("/api/pagos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100));
    }

    @Test
    void deberiaRetornar404CuandoPagoNoExiste() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe el pago con id 99"));

        mockMvc.perform(get("/api/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoSaldoInsuficiente() throws Exception {
        ProcesarPagoRequestDTO dto = new ProcesarPagoRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMetodoPago("SALDO");
        when(service.procesarPago(any(ProcesarPagoRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("Saldo insuficiente para completar la compra"));

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }
}
