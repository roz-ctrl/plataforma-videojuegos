package com.videojuegos.pagos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/** Cliente Feign hacia usuarios-service (para cobrar contra el saldo). */
@FeignClient(name = "usuarios-service", url = "${servicios.usuarios.url}")
public interface UsuarioClient {

    @PutMapping("/api/usuarios/{id}/debitar")
    void debitarSaldo(@PathVariable("id") Long id, @RequestParam("monto") BigDecimal monto);
}
