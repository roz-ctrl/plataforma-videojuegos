package com.videojuegos.pagos.client.dto;

import java.math.BigDecimal;
import java.util.List;

/** Proyeccion del carrito remoto (carrito-service). */
public class CarritoDTO {

    private Long id;
    private Long usuarioId;
    private String estado;
    private BigDecimal total;
    private List<ItemCarritoDTO> items;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ItemCarritoDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemCarritoDTO> items) {
        this.items = items;
    }
}
