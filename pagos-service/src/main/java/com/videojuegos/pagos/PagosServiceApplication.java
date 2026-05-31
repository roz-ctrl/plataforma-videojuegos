package com.videojuegos.pagos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Pagos. Orquesta el flujo de compra: lee el carrito,
 * cobra al usuario, registra los juegos en la biblioteca y cierra el carrito.
 */
@SpringBootApplication
@EnableFeignClients
public class PagosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PagosServiceApplication.class, args);
    }
}
