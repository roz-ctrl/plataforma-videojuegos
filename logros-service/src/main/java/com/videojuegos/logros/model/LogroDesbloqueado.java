package com.videojuegos.logros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Entidad JPA que registra que un usuario desbloqueo un logro.
 * Relacion N:1 con Logro (clave foranea local). La restriccion unica
 * (usuario_id, logro_id) impide desbloquear el mismo logro dos veces.
 */
@Entity
@Table(name = "logros_desbloqueados",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "logro_id"}))
public class LogroDesbloqueado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "logro_id", nullable = false)
    private Logro logro;

    @Column(name = "fecha_desbloqueo", nullable = false)
    private LocalDateTime fechaDesbloqueo;

    public LogroDesbloqueado() {
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

    public Logro getLogro() {
        return logro;
    }

    public void setLogro(Logro logro) {
        this.logro = logro;
    }

    public LocalDateTime getFechaDesbloqueo() {
        return fechaDesbloqueo;
    }

    public void setFechaDesbloqueo(LocalDateTime fechaDesbloqueo) {
        this.fechaDesbloqueo = fechaDesbloqueo;
    }
}
