package com.mgbt.socialapp_backend.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Social App API")
                        .description("Social App API made with Spring Boot")
                        .contact(new Contact().url("https://github.com/MatiasGBT"))
                        .version("1.0")
                );
    }
}
