package com.videojuegos.resenas.client;

import com.videojuegos.resenas.client.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Cliente Feign hacia usuarios-service. */
@FeignClient(name = "usuarios-service", url = "${servicios.usuarios.url}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerPorId(@PathVariable("id") Long id);
}
