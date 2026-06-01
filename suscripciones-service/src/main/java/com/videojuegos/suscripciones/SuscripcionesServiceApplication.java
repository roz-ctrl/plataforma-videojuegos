package com.videojuegos.suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Suscripciones: pases mensuales para acceder a un catalogo.
 * Maneja Planes (catalogo de pases) y Suscripciones de usuarios.
 */
@SpringBootApplication
@EnableFeignClients
public class SuscripcionesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuscripcionesServiceApplication.class, args);
    }
}
