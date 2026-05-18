package com.almacen.productos_service.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.almacen.productos_service.dtos.request.InventarioRequest;

@Component
public class InventarioClient {

    private final WebClient.Builder webClientBuilder;
    private final String inventarioServiceUrl;

    public InventarioClient(
            WebClient.Builder webClientBuilder,
            @Value("${inventario-service.url}") String inventarioServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.inventarioServiceUrl = inventarioServiceUrl;
    }

public void crearInventario(InventarioRequest request) {
    webClientBuilder.build()
            .post()
            .uri(inventarioServiceUrl + "/api/v1/inventario")
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .block();
}
}