package com.videojuegos.logros;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Microservicio de Logros: trofeos desbloqueables por los jugadores.
 * Gestiona la definicion de logros por juego y el desbloqueo por usuario.
 */
@SpringBootApplication
@EnableFeignClients
public class LogrosServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogrosServiceApplication.class, args);
    }
}
