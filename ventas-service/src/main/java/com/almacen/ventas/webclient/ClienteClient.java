package com.almacen.ventas.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ClienteClient {

    private final WebClient webClient;

    public ClienteClient(@Value("${cliente-service.url}") String urlBase) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }

    // 🔹 Validar que el cliente existe
    public void validarCliente(Long id) {
        webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}