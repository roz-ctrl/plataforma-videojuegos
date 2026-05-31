package com.videojuegos.pagos.client;

import com.videojuegos.pagos.client.dto.CarritoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/** Cliente Feign hacia carrito-service. */
@FeignClient(name = "carrito-service", url = "${servicios.carrito.url}")
public interface CarritoClient {

    @GetMapping("/api/carrito/usuario/{usuarioId}")
    CarritoDTO obtenerCarrito(@PathVariable("usuarioId") Long usuarioId);

    @PutMapping("/api/carrito/usuario/{usuarioId}/pagar")
    CarritoDTO marcarComoPagado(@PathVariable("usuarioId") Long usuarioId);
}
