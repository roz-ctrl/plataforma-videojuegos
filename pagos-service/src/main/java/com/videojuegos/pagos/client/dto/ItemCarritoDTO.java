package com.videojuegos.pagos.client.dto;

import java.math.BigDecimal;

/** Proyeccion de una linea del carrito remoto. */
public class ItemCarritoDTO {

    private Long juegoId;
    private String tituloJuego;
    private BigDecimal precioUnitario;

    public Long getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(Long juegoId) {
        this.juegoId = juegoId;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
