package com.almacen.proveedores_service.webclient;

import com.almacen.proveedores_service.dtos.response.ProductoResponse;
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

    public List<ProductoResponse> obtenerProductosPorProveedor(Long proveedorId) {

        List<ProductoResponse> productos = webClientBuilder.build()
                .get()
                .uri(productoServiceUrl + "/api/v1/productos/proveedor/" + proveedorId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Proveedor no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en productos-service")))
                .bodyToMono(new ParameterizedTypeReference<List<ProductoResponse>>() {})
                .block();

        if (productos == null) {
            return List.of();
        }

        return productos;
    }
}