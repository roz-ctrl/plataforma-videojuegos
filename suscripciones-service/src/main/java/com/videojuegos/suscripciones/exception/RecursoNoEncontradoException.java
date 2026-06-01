package com.videojuegos.suscripciones.exception;

/** Se lanza cuando un recurso solicitado no existe (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
