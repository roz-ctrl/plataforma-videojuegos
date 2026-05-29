package com.videojuegos.carrito.client.dto;

import java.math.BigDecimal;

/** Proyeccion del recurso remoto Juego (microservicio juegos-service). */
public class JuegoDTO {

    private Long id;
    private String titulo;
    private BigDecimal precioFinal;
    private boolean activo;

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

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
