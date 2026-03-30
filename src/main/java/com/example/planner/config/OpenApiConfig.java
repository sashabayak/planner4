package com.example.planner.config;
import java.util.List;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  public OpenAPI customOpenAPI() {
	return new OpenAPI()
		.info(new Info()
			.title("Planner API")
			.version("1.0")
			.description("API для управления фотостудией")
			.contact(new Contact()
				.name("Planner Team")
				.email("info@planner.com"))
			.license(new License()
				.name("Apache 2.0")
				.url("https://springdoc.org")))
		.servers(List.of(
			new Server().url("http://localhost:8081").description("Local server")
		));
  }
}