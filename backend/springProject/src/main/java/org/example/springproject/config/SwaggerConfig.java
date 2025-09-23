package org.example.springproject.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Travel-System-API")
                        .description("This is the API documentation of a tourism recommendation system, which is used to manage information such as users, scenic spots, routes, and orders.")
                        .version("v1.0.0")
                        .contact(new Contact().name("MRX").url("https://blog.csdn.net/").email("SuperMartian0413@outlook.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("External document")
                        .url("https://springdoc.org/"));
    }
}
