package com.videojuegos.suscripciones.dto;

import jakarta.validation.constraints.NotNull;

/** DTO de entrada para suscribir a un usuario a un plan. */
public class SuscripcionRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id del plan es obligatorio")
    private Long planId;

    private boolean renovacionAutomatica;

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

    public boolean isRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
    }
}
