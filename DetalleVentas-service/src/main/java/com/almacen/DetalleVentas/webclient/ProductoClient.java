package com.almacen.DetalleVentas.webclient;

import com.almacen.DetalleVentas.dtos.response.ProductoResponse;
import com.almacen.DetalleVentas.exceptions.RemoteServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductoClient {

    private final WebClient webClient;

    public ProductoClient(@Value("${producto-service.url}") String urlBase) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }

    // Obtener producto por ID desde productos-service
    public ProductoResponse obtenerProducto(Long id) {
        try {
            return webClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
                    .block();

        } catch (Exception e) {
            throw new RemoteServiceException(
                    "Error al comunicarse con productos-service para obtener producto ID: " + id
            );
        }
    }
}