package com.videojuegos.suscripciones.dto;

import java.time.LocalDate;

/** DTO de salida de una suscripcion, con datos del plan asociado. */
public class SuscripcionResponseDTO {

    private Long id;
    private Long usuarioId;
    private Long planId;
    private String planNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private boolean renovacionAutomatica;

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

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanNombre() {
        return planNombre;
    }

    public void setPlanNombre(String planNombre) {
        this.planNombre = planNombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
    }
}
