package com.videojuegos.usuarios.exception;

/**
 * Se lanza cuando un recurso solicitado no existe en la base de datos.
 * El GlobalExceptionHandler la traduce a un HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
