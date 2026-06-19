package com.videojuegos.resenas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI del microservicio de Resenas.
 * Swagger UI: http://localhost:8090/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Resenas - GameVault")
                        .version("1.0")
                        .description("Microservicio que gestiona las resenas y calificaciones de los juegos.")
                        .contact(new Contact().name("Equipo GameVault")));
    }
}
