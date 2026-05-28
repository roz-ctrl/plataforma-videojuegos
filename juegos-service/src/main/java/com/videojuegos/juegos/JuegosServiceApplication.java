package com.videojuegos.juegos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Juegos (catalogo). Se comunica con los microservicios de
 * Desarrolladoras y Categorias mediante Feign Client, por eso habilita Feign.
 */
@SpringBootApplication
@EnableFeignClients
public class JuegosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JuegosServiceApplication.class, args);
    }
}
