package com.almacen.pedidos_service.webclient;

import com.almacen.pedidos_service.dtos.response.ClienteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ClienteClient {

    private final WebClient webClient;

    public ClienteClient(
            WebClient.Builder webClientBuilder,
            @Value("${cliente-service.url}") String urlBase
    ) {
        this.webClient = webClientBuilder
                .baseUrl(urlBase)
                .build();
    }

    public ClienteResponse obtenerClientePorId(Long id) {
        ClienteResponse cliente = webClient.get()
                .uri("/api/v1/clientes/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Cliente no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en clientes-service")))
                .bodyToMono(ClienteResponse.class)
                .block();

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        return cliente;
    }
}
