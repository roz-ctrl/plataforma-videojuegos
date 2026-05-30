package com.videojuegos.biblioteca.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para registrar la adquisicion de un juego en la biblioteca.
 * Normalmente lo invoca el microservicio de Pagos tras una compra exitosa.
 */
public class AdquirirJuegoRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id del juego es obligatorio")
    private Long juegoId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(Long juegoId) {
        this.juegoId = juegoId;
    }
}
