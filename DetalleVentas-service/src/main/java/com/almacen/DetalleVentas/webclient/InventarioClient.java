package com.almacen.DetalleVentas.webclient;

import com.almacen.DetalleVentas.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RemoteServiceException("Error de inventario: no se pudo descontar el stock")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RemoteServiceException("Error en inventario-service")))
                .toBodilessEntity()
                .block();
    }
}