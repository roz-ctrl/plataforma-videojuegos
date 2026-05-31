package com.videojuegos.pagos.client.dto;

/** Cuerpo enviado a biblioteca-service para registrar la propiedad de un juego. */
public class AdquirirJuegoDTO {

    private Long usuarioId;
    private Long juegoId;

    public AdquirirJuegoDTO() {
    }

    public AdquirirJuegoDTO(Long usuarioId, Long juegoId) {
        this.usuarioId = usuarioId;
        this.juegoId = juegoId;
    }

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
