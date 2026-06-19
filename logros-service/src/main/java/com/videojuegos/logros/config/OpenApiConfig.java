package com.videojuegos.logros.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI del microservicio de Logros.
 * Swagger UI: http://localhost:8089/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Logros - GameVault")
                        .version("1.0")
                        .description("Microservicio que gestiona los logros por juego y su desbloqueo por usuario.")
                        .contact(new Contact().name("Equipo GameVault")));
    }
}
