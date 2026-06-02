package com.videojuegos.resenas.client;

import com.videojuegos.resenas.client.dto.JuegoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Cliente Feign hacia juegos-service. */
@FeignClient(name = "juegos-service", url = "${servicios.juegos.url}")
public interface JuegoClient {

    @GetMapping("/api/juegos/{id}")
    JuegoDTO obtenerPorId(@PathVariable("id") Long id);
}
