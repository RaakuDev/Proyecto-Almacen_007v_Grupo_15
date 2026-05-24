package com.almacen.productos_service.webclient;

import com.almacen.productos_service.dtos.response.ProveedorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProveedorClient {

    private final WebClient webClient;

    public ProveedorClient(
            WebClient.Builder webClientBuilder,
            @Value("${proveedor-service.url}") String urlBase
    ) {
        this.webClient = webClientBuilder
                .baseUrl(urlBase)
                .build();
    }

    public ProveedorResponse obtenerProveedorPorId(Long id) {
        ProveedorResponse proveedor = webClient.get()
                .uri("/api/v1/proveedores/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Proveedor no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en proveedores-service")))
                .bodyToMono(ProveedorResponse.class)
                .block();

        if (proveedor == null) {
            throw new RuntimeException("Proveedor no encontrado");
        }

        return proveedor;
    }
}