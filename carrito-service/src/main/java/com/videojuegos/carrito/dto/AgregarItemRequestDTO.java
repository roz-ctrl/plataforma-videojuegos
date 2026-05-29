package com.videojuegos.carrito.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para agregar un juego al carrito de un usuario.
 */
public class AgregarItemRequestDTO {

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
