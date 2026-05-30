package com.videojuegos.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Biblioteca: los juegos que el usuario ya compro y puede descargar.
 */
@SpringBootApplication
@EnableFeignClients
public class BibliotecaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaServiceApplication.class, args);
    }
}
