package com.almacen.ventas.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ClienteClient {

    private final WebClient.Builder webClientBuilder;
    private final String clienteServiceUrl;

    public ClienteClient(
            WebClient.Builder webClientBuilder,
            @Value("${cliente-service.url}") String clienteServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.clienteServiceUrl = clienteServiceUrl;
    }

    public void validarCliente(Long id) {
        webClientBuilder.build()
                .get()
                .uri(clienteServiceUrl + "/api/v1/clientes/" + id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}