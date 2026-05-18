package com.almacen.DetalleVentas.webclient;

import com.almacen.DetalleVentas.dtos.response.ProductoResponse;
import com.almacen.DetalleVentas.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

    public ProductoResponse obtenerProducto(Long id) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(productoServiceUrl + "/api/v1/productos/" + id)
                    .retrieve()
                    .bodyToMono(ProductoResponse.class)
                    .block();

        } catch (Exception e) {
            throw new RemoteServiceException(
                    "Error al comunicarse con productos-service para obtener producto ID: " + id
            );
        }
    }
}