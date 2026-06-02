package com.videojuegos.logros.dto;

import jakarta.validation.constraints.NotNull;

/** DTO de entrada para desbloquear un logro a un usuario. */
public class DesbloquearRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id del logro es obligatorio")
    private Long logroId;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLogroId() {
        return logroId;
    }

    public void setLogroId(Long logroId) {
        this.logroId = logroId;
    }
}
