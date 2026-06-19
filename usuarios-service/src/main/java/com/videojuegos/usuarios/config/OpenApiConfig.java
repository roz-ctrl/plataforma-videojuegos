package com.videojuegos.usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI para el microservicio de Usuarios.
 * Define el titulo, la version y la descripcion que se muestran en Swagger UI
 * (http://localhost:8081/swagger-ui.html).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usuariosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Usuarios - GameVault")
                        .version("1.0")
                        .description("Microservicio que gestiona los jugadores registrados y el saldo de su billetera.")
                        .contact(new Contact().name("Equipo GameVault")));
    }
}
