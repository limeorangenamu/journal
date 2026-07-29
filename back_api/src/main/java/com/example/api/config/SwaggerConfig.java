package com.example.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    String jwtSchemeName = "JWT TOKEN";

    SecurityRequirement securityRequirement =
        new SecurityRequirement().addList(jwtSchemeName);

    Components components = new Components()
        .addSecuritySchemes(
            jwtSchemeName,
            new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

    return new OpenAPI()
        .addSecurityItem(securityRequirement)
        .components(components)
        .info(apiInfo());
  }

  private Info apiInfo() {
    return new Info()
        .title("CodeArena Swagger")
        .description("CodeArena 유저 및 인증, ps, 알림에 관한 REST API")
        .version("1.0.0");
  }
}