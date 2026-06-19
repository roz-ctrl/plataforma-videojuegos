package com.videojuegos.pagos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion de Swagger / OpenAPI del microservicio de Pagos.
 * Swagger UI: http://localhost:8086/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Pagos - GameVault")
                        .version("1.0")
                        .description("Microservicio que procesa los pagos y orquesta la compra entre servicios.")
                        .contact(new Contact().name("Equipo GameVault")));
    }
}
