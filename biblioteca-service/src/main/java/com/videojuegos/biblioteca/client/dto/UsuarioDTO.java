package com.videojuegos.biblioteca.client.dto;

/** Proyeccion del recurso remoto Usuario. */
public class UsuarioDTO {

    private Long id;
    private boolean activo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
