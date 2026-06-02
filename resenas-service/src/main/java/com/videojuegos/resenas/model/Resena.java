package com.videojuegos.resenas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa una resena de un usuario sobre un juego.
 * La restriccion unica (usuario_id, juego_id) impide que un usuario
 * deje mas de una resena por juego.
 */
@Entity
@Table(name = "resenas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "juego_id"}))
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "juego_id", nullable = false)
    private Long juegoId;

    /** Calificacion de 1 a 5 estrellas. */
    @Column(nullable = false)
    private Integer calificacion;

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
    private boolean recomendado;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Resena() {
    }

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

    public boolean isRecomendado() {
        return recomendado;
    }

    public void setRecomendado(boolean recomendado) {
        this.recomendado = recomendado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
