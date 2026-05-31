package com.videojuegos.pagos.dto;

import java.math.BigDecimal;

/** DTO de salida de una linea de la transaccion. */
public class DetallePagoResponseDTO {

    private Long juegoId;
    private String tituloJuego;
    private BigDecimal precio;

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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}
