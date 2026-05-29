package com.videojuegos.carrito.exception;

/** Falla de comunicacion con un microservicio remoto (HTTP 503). */
public class ComunicacionException extends RuntimeException {

    public ComunicacionException(String mensaje) {
        super(mensaje);
    }
}
