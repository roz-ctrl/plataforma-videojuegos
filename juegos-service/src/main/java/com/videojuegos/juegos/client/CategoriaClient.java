package com.videojuegos.juegos.client;

import com.videojuegos.juegos.client.dto.CategoriaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Cliente Feign para consumir el microservicio de Categorias.
 */
@FeignClient(name = "categorias-service", url = "${servicios.categorias.url}")
public interface CategoriaClient {

    @GetMapping("/api/categorias/{id}")
    CategoriaDTO obtenerPorId(@PathVariable("id") Long id);
}
