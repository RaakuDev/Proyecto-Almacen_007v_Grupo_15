package com.almacen.inventario_service.webclient;

import com.almacen.inventario_service.dtos.response.ProductoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

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
                        response -> Mono.error(new RuntimeException("Producto no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en productos-service")))
                .bodyToMono(ProductoResponse.class)
                .block();

        if (producto == null) {
            throw new RuntimeException("Producto no encontrado");
        }

        return producto;
    }

    public List<ProductoResponse> obtenerProductosPorCategoria(Long categoriaId) {

        List<ProductoResponse> productos = webClientBuilder.build()
                .get()
                .uri(productoServiceUrl + "/api/v1/productos/categoria/" + categoriaId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Categoría no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en productos-service")))
                .bodyToMono(new ParameterizedTypeReference<List<ProductoResponse>>() {})
                .block();

        if (productos == null) {
            throw new RuntimeException("No se encontraron productos para la categoría");
        }

        return productos;
    }
}