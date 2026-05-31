package com.videojuegos.pagos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada para procesar el pago del carrito de un usuario.
 */
public class ProcesarPagoRequestDTO {

    @NotNull(message = "El id del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El metodo de pago es obligatorio")
    @Pattern(regexp = "SALDO|TARJETA", message = "El metodo de pago debe ser SALDO o TARJETA")
    private String metodoPago;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}
