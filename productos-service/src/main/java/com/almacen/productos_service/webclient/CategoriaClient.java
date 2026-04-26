package com.almacen.productos_service.webclient;

import com.almacen.productos_service.dtos.response.CategoriaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class CategoriaClient {

    private final WebClient webClient;

    public CategoriaClient(@Value("${categoria-service.url}") String urlBase) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }

    public CategoriaResponse obtenerCatPorId(Long id) {

        CategoriaResponse categoria = webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Categoría no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en categorias-service")))
                .bodyToMono(CategoriaResponse.class)
                .block();

        if (categoria == null) {
            throw new RuntimeException("Categoría no encontrada");
        }

        return categoria;
    }
}