package com.almacen.DetalleVentas.webclient;

import com.almacen.DetalleVentas.dtos.response.ProductoResponse;
import com.almacen.DetalleVentas.exceptions.NotFoundException;
import com.almacen.DetalleVentas.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
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

    public ProductoResponse obtenerProducto(Long id) {
        try {
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
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteServiceException(
                    "Error al comunicarse con productos-service para obtener producto ID: " + id
            );
        }
    }
}