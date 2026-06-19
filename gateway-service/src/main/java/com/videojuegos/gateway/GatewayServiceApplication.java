package com.videojuegos.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway de la plataforma GameVault.
 * Es el punto unico de entrada: recibe las peticiones en el puerto 8080 y las
 * reenvia al microservicio correspondiente segun la ruta (configurado en
 * application.yml con Spring Cloud Gateway).
 */
@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
