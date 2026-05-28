package com.videojuegos.juegos.client.dto;

/**
 * Proyeccion del recurso remoto Categoria (microservicio categorias-service).
 */
public class CategoriaDTO {

    private Long id;
    private String nombre;

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
}
