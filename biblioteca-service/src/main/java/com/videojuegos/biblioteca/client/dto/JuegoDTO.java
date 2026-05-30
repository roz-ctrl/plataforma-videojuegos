package com.videojuegos.biblioteca.client.dto;

/** Proyeccion del recurso remoto Juego. */
public class JuegoDTO {

    private Long id;
    private String titulo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
