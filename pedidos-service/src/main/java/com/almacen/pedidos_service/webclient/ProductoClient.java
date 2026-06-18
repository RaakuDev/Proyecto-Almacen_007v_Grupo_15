package com.almacen.pedidos_service.webclient;

import com.almacen.pedidos_service.dtos.response.ProductoResponse;
import com.almacen.pedidos_service.exceptions.NotFoundException;
import com.almacen.pedidos_service.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductoClient {

    private final WebClient.Builder webClientBuilder;
    private final String productoServiceUrl;

    public ProductoClient(
            WebClient.Builder webClientBuilder,
            @Value("${producto-service.url}") String productoServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.productoServiceUrl = productoServiceUrl;
    }

    public ProductoResponse obtenerProductoPorId(Long id) {
        ProductoResponse producto = webClientBuilder.build()
            .get()
            .uri(productoServiceUrl + "/api/v1/productos/" + id)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(),
                response -> Mono.error(new NotFoundException("Producto no existe")))
            .onStatus(status -> status.is5xxServerError(),
                response -> Mono.error(new RemoteServiceException("Error en productos-service")))
            .bodyToMono(ProductoResponse.class)
            .block();

        if (producto == null) {
            throw new NotFoundException("Producto no encontrado");
        }

        return producto;
    }
}