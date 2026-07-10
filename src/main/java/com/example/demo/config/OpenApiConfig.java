package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Book Shop API",
                version = "1.0",
                description = "API documentation for the online book shop application"
        )
)
@Configuration
public class OpenApiConfig {
}
