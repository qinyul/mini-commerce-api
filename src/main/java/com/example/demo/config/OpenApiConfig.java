package com.example.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Value("${application.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Development environment");

        Info info = new Info()
                .title("Mini Commerde API")
                .description("""
                                         Spring Boot mini project.
                                                        Features:
                                                        - Product management
                                                        - Stock with concurrency control
                                                        - Order lifecycle with reservation
                        """)
                .version("1.0.0");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
