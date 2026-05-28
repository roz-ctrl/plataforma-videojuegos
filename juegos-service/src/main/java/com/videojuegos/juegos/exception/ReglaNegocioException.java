package com.videojuegos.juegos.exception;

/** Se lanza al violar una regla de negocio del dominio (HTTP 409). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
