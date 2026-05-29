package com.videojuegos.carrito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Carrito de Compras. Gestiona el carrito temporal antes
 * del pago y consulta a Usuarios y Juegos mediante Feign.
 */
@SpringBootApplication
@EnableFeignClients
public class CarritoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarritoServiceApplication.class, args);
    }
}
