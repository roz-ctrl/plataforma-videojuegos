package com.videojuegos.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videojuegos.usuarios.dto.UsuarioRequestDTO;
import com.videojuegos.usuarios.dto.UsuarioResponseDTO;
import com.videojuegos.usuarios.exception.RecursoNoEncontradoException;
import com.videojuegos.usuarios.exception.ReglaNegocioException;
import com.videojuegos.usuarios.service.UsuarioService;
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

/**
 * Pruebas del controlador UsuarioController.
 * @WebMvcTest levanta solo la capa web (sin base de datos).
 * El UsuarioService se reemplaza por un mock con @MockBean.
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private UsuarioResponseDTO respuesta(Long id, String nombre, String email) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(id);
        dto.setNombreUsuario(nombre);
        dto.setEmail(email);
        dto.setPais("Chile");
        dto.setSaldo(BigDecimal.ZERO);
        dto.setActivo(true);
        return dto;
    }

    @Test
    void deberiaListarUsuarios() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(
                respuesta(1L, "gabe", "gabe@steamclone.com")));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombreUsuario").value("gabe"));
    }

    @Test
    void deberiaObtenerUsuarioPorId() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(respuesta(1L, "gabe", "gabe@steamclone.com"));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("gabe@steamclone.com"));
    }

    @Test
    void deberiaCrearUsuarioYRetornar201() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("nuevo_jugador");
        dto.setEmail("nuevo@steamclone.com");
        dto.setPassword("clave123");
        dto.setPais("Chile");

        when(usuarioService.crear(any(UsuarioRequestDTO.class)))
                .thenReturn(respuesta(5L, "nuevo_jugador", "nuevo@steamclone.com"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombreUsuario").value("nuevo_jugador"));
    }

    @Test
    void deberiaRetornar400CuandoLosDatosSonInvalidos() throws Exception {
        // email invalido y password corta -> Bean Validation debe rechazar (400)
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("x");
        dto.setEmail("correo-malo");
        dto.setPassword("123");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaEliminarUsuarioYRetornar204() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deberiaRetornar404CuandoUsuarioNoExiste() throws Exception {
        when(usuarioService.obtenerPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No existe el usuario con id 99"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404));
    }

    @Test
    void deberiaRetornar409CuandoEmailDuplicado() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("gabe_player");
        dto.setEmail("gabe@steamclone.com");
        dto.setPassword("clave123");
        when(usuarioService.crear(any(UsuarioRequestDTO.class)))
                .thenThrow(new ReglaNegocioException("Ya existe un usuario con el email"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.estado").value(409));
    }

    @Test
    void deberiaActualizarUsuarioYRetornar200() throws Exception {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNombreUsuario("gabe_player");
        dto.setEmail("gabe@steamclone.com");
        dto.setPassword("clave123");
        when(usuarioService.actualizar(any(Long.class), any(UsuarioRequestDTO.class)))
                .thenReturn(respuesta(1L, "gabe_player", "gabe@steamclone.com"));

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deberiaRecargarSaldoYRetornar200() throws Exception {
        when(usuarioService.recargarSaldo(any(Long.class), any(BigDecimal.class)))
                .thenReturn(respuesta(1L, "gabe", "gabe@steamclone.com"));

        mockMvc.perform(put("/api/usuarios/1/recargar").param("monto", "10000"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaDebitarSaldoYRetornar200() throws Exception {
        when(usuarioService.debitarSaldo(any(Long.class), any(BigDecimal.class)))
                .thenReturn(respuesta(1L, "gabe", "gabe@steamclone.com"));

        mockMvc.perform(put("/api/usuarios/1/debitar").param("monto", "5000"))
                .andExpect(status().isOk());
    }
}
