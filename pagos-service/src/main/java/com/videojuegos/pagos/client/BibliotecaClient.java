package com.videojuegos.pagos.client;

import com.videojuegos.pagos.client.dto.AdquirirJuegoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/** Cliente Feign hacia biblioteca-service (para registrar los juegos comprados). */
@FeignClient(name = "biblioteca-service", url = "${servicios.biblioteca.url}")
public interface BibliotecaClient {

    @PostMapping("/api/biblioteca")
    void adquirir(@RequestBody AdquirirJuegoDTO dto);
}
