package com.videojuegos.usuarios.exception;

/**
 * Se lanza cuando se viola una regla de negocio del dominio
 * (por ejemplo: email duplicado o saldo insuficiente).
 * El GlobalExceptionHandler la traduce a un HTTP 409 / 400.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
