package com.videojuegos.resenas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO de entrada para crear/actualizar una resena. */
public class ResenaRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id del juego es obligatorio")
    private Long juegoId;

    @NotNull(message = "La calificacion es obligatoria")
    @Min(value = 1, message = "La calificacion minima es 1")
    @Max(value = 5, message = "La calificacion maxima es 5")
    private Integer calificacion;

    @Size(max = 1000, message = "El comentario no puede superar 1000 caracteres")
    private String comentario;

    @NotNull(message = "Debe indicar si recomienda el juego")
    private Boolean recomendado;

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

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Boolean getRecomendado() {
        return recomendado;
    }

    public void setRecomendado(Boolean recomendado) {
        this.recomendado = recomendado;
    }
}
