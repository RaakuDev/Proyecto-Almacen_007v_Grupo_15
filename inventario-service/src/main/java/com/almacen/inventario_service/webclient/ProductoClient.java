package com.almacen.inventario_service.webclient;

import com.almacen.inventario_service.dtos.response.ProductoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductoClient {

    private final WebClient webClient;

    public ProductoClient(@Value("${producto-service.url}") String urlBase) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }

    public ProductoResponse obtenerProductoPorId(Long id) {

        ProductoResponse producto = webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Producto no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en productos-service")))
                .bodyToMono(ProductoResponse.class)
                .block();

        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }

        return producto;
    }
}
