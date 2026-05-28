package com.videojuegos.juegos.client;

import com.videojuegos.juegos.client.dto.DesarrolladoraDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para consumir el microservicio de Desarrolladoras.
 * La URL se inyecta desde application.properties (servicios.desarrolladoras.url).
 */
@FeignClient(name = "desarrolladoras-service", url = "${servicios.desarrolladoras.url}")
public interface DesarrolladoraClient {

    @GetMapping("/api/desarrolladoras/{id}")
    DesarrolladoraDTO obtenerPorId(@PathVariable("id") Long id);
}
