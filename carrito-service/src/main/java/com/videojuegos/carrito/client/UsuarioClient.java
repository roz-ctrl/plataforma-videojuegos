package com.videojuegos.carrito.client;

import com.videojuegos.carrito.client.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Cliente Feign para validar usuarios contra usuarios-service. */
@FeignClient(name = "usuarios-service", url = "${servicios.usuarios.url}")
public interface UsuarioClient {

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerPorId(@PathVariable("id") Long id);
}
