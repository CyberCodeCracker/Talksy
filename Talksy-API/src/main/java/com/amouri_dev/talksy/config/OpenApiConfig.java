package com.amouri_dev.talksy.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Talksy API",
                        email = "contact@amouri-coding.com",
                        url = "https://cybercodecracker.github.io/portfolio/"
                ),
                description = "OpenApi documentation for Talksy API",
                title = "OpenAPI Specification",
                version = "0.1",
                license = @License(
                        name = "License",
                        url = "https://cybercodecracker.github.io/portfolio/license"
                ),
                termsOfService = "https://cybercodecracker.github.io/portfolio/terms"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080", description = "Local ENV"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
