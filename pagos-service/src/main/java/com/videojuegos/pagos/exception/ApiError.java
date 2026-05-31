package com.videojuegos.pagos.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Estructura uniforme de respuesta de error para todos los endpoints. */
public class ApiError {

    private LocalDateTime timestamp = LocalDateTime.now();
    private int estado;
    private String error;
    private String mensaje;
    private Map<String, String> detalles = new HashMap<>();

    public ApiError(int estado, String error, String mensaje) {
        this.estado = estado;
        this.error = error;
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getEstado() {
        return estado;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Map<String, String> getDetalles() {
        return detalles;
    }

    public void setDetalles(Map<String, String> detalles) {
        this.detalles = detalles;
    }
}
