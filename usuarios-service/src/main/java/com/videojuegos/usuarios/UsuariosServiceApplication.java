package com.videojuegos.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del microservicio de Usuarios (jugadores registrados).
 * Cada microservicio es una aplicacion Spring Boot independiente con su
 * propia base de datos, cumpliendo el principio de "base de datos por servicio".
 */
@SpringBootApplication
public class UsuariosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsuariosServiceApplication.class, args);
    }
}
