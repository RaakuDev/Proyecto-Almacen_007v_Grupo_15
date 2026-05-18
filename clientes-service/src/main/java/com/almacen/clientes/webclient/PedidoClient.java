package com.almacen.clientes.webclient;

import com.almacen.clientes.dtos.response.PedidoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PedidoClient {

    private final WebClient.Builder webClientBuilder;
    private final String pedidoServiceUrl;

    public PedidoClient(
            WebClient.Builder webClientBuilder,
            @Value("${pedido-service.url}") String pedidoServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.pedidoServiceUrl = pedidoServiceUrl;
    }

    public List<PedidoResponse> obtenerPedidosPorCliente(Long clienteId) {

        List<PedidoResponse> pedidos = webClientBuilder.build()
                .get()
                .uri(pedidoServiceUrl + "/api/v1/pedidos/cliente/" + clienteId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Cliente no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en pedidos-service")))
                .bodyToMono(new ParameterizedTypeReference<List<PedidoResponse>>() {})
                .block();

        if (pedidos == null) {
            return List.of();
        }

        return pedidos;
    }
}