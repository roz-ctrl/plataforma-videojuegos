package com.videojuegos.logros.dto;

import java.time.LocalDateTime;

/** DTO de salida de un logro desbloqueado por un usuario. */
public class LogroDesbloqueadoResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long logroId;
    private String logroNombre;
    private Integer puntos;
    private LocalDateTime fechaDesbloqueo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getLogroNombre() {
        return logroNombre;
    }

    public void setLogroNombre(String logroNombre) {
        this.logroNombre = logroNombre;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public LocalDateTime getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(LocalDateTime fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }
}
