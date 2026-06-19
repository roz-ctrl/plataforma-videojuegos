package com.videojuegos.carrito.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.carrito.dto.AgregarItemRequestDTO;
import com.videojuegos.carrito.dto.CarritoResponseDTO;
import com.videojuegos.carrito.exception.RecursoNoEncontradoException;
import com.videojuegos.carrito.exception.ReglaNegocioException;
import com.videojuegos.carrito.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarritoService service;

    private CarritoResponseDTO resp() {
        CarritoResponseDTO c = new CarritoResponseDTO();
        c.setId(1L);
        c.setUsuarioId(1L);
        c.setEstado("ABIERTO");
        c.setItems(List.of());
        c.setTotal(new BigDecimal("19990"));
        c.setCantidadItems(1);
        return c;
    }

    @Test
    void deberiaVerCarrito() throws Exception {
        when(service.verCarrito(1L)).thenReturn(resp());
        mockMvc.perform(get("/api/carrito/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioId").value(1));
    }

    @Test
    void deberiaAgregarItemYRetornar201() throws Exception {
        AgregarItemRequestDTO dto = new AgregarItemRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);
        when(service.agregarItem(any(AgregarItemRequestDTO.class))).thenReturn(resp());

        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidadItems").value(1));
    }

    @Test
    void deberiaVaciarCarrito() throws Exception {
        when(service.vaciar(1L)).thenReturn(resp());
        mockMvc.perform(delete("/api/carrito/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaMarcarComoPagado() throws Exception {
        when(service.marcarComoPagado(anyLong())).thenReturn(resp());
        mockMvc.perform(put("/api/carrito/usuario/1/pagar"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaEliminarItem() throws Exception {
        when(service.eliminarItem(1L, 1L)).thenReturn(resp());
        mockMvc.perform(delete("/api/carrito/usuario/1/juego/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRetornar404CuandoNoHayCarrito() throws Exception {
        when(service.verCarrito(1L))
                .thenThrow(new RecursoNoEncontradoException("El usuario 1 no tiene un carrito abierto"));

        mockMvc.perform(get("/api/carrito/usuario/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoJuegoDuplicado() throws Exception {
        AgregarItemRequestDTO dto = new AgregarItemRequestDTO();
        dto.setUsuarioId(1L);
        dto.setJuegoId(1L);
        when(service.agregarItem(any(AgregarItemRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("El juego ya se encuentra en el carrito"));

        mockMvc.perform(post("/api/carrito/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }
}
