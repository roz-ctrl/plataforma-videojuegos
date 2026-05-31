package com.videojuegos.pagos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** DTO de salida de una transaccion de pago. */
public class PagoResponseDTO {

    private Long id;
    private Long usuarioId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String referencia;
    private LocalDateTime fechaPago;
    private List<DetallePagoResponseDTO> detalles;

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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public List<DetallePagoResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePagoResponseDTO> detalles) {
        this.detalles = detalles;
    }
}
