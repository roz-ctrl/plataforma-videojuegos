package com.videojuegos.juegos.exception;

/**
 * Se lanza cuando falla la comunicacion con un microservicio remoto
 * (timeout, servicio caido, etc.). El handler la traduce a HTTP 503.
 */
public class ComunicacionException extends RuntimeException {

    public ComunicacionException(String mensaje) {
        super(mensaje);
    }
}
