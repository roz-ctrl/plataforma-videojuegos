package com.videojuegos.resenas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign hacia biblioteca-service.
 * Se usa para verificar que el usuario posee el juego antes de resenarlo.
 */
@FeignClient(name = "biblioteca-service", url = "${servicios.biblioteca.url}")
public interface BibliotecaClient {

    @GetMapping("/api/biblioteca/usuario/{usuarioId}/posee/{juegoId}")
    Boolean usuarioPoseeJuego(@PathVariable("usuarioId") Long usuarioId,
                              @PathVariable("juegoId") Long juegoId);
}
