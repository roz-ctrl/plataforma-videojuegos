package com.videojuegos.categorias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de Categorias: generos de videojuegos (RPG, Shooter, etc.).
 */
@SpringBootApplication
public class CategoriasServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoriasServiceApplication.class, args);
    }
}
