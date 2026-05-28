package com.ivanolivendev.reservation.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI roomReservationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Room Reservation API")
                        .description("""
                                API REST para gerenciamento centralizado de salas, auditorios e reservas.
                                
                                Projeto desenvolvido para o desafio tecnico FADESP, simulando o cenario de uma
                                empresa de coworking e eventos empresariais que precisa substituir controles manuais
                                por uma API confiavel, rastreavel e facil de integrar.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("Ivan Oliveira")
                                .url("https://github.com/ivanolivendev"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente local")
                ))
                .tags(List.of(
                        new Tag()
                                .name("Rooms")
                                .description("Gerencia o cadastro de salas, a desativacao logica e a consulta de disponibilidade por data e horario."),
                        new Tag()
                                .name("Reservations")
                                .description("Gerencia o ciclo de vida das reservas: criacao, listagem, agenda diaria e cancelamento logico.")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio do projeto")
                        .url("https://github.com/ivanolivendev/room-reservation-api"));
    }
}
