package com.videojuegos.juegos.client.dto;

/**
 * Proyeccion del recurso remoto Desarrolladora (microservicio desarrolladoras-service).
 * Solo contiene los campos que este microservicio necesita consumir.
 */
public class DesarrolladoraDTO {

    private Long id;
    private String nombre;
    private boolean activa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
