package com.almacen.DetalleVentas.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

    public void descontarStock(Long productoId, Integer cantidad) {
        webClientBuilder.build()
                .put()
                .uri(inventarioServiceUrl + "/api/v1/inventario/producto/" + productoId + "/descontar/" + cantidad)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}