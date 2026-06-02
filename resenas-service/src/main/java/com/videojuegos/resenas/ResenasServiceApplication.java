package com.videojuegos.resenas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Resenas: opiniones y calificaciones de la comunidad.
 * Valida contra Usuarios, Juegos y Biblioteca (solo se resena lo que se posee).
 */
@SpringBootApplication
@EnableFeignClients
public class ResenasServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResenasServiceApplication.class, args);
    }
}
