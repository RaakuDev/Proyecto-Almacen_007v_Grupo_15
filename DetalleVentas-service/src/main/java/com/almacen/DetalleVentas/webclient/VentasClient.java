package com.almacen.DetalleVentas.webclient;

import com.almacen.DetalleVentas.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VentasClient {

    private final WebClient.Builder webClientBuilder;
    private final String ventasServiceUrl;

    public VentasClient(
            WebClient.Builder webClientBuilder,
            @Value("${ventas-service.url}") String ventasServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.ventasServiceUrl = ventasServiceUrl;
    }

    public void validarVenta(Long id) {
        try {
            webClientBuilder.build()
                    .get()
                    .uri(ventasServiceUrl + "/api/v1/ventas/" + id)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (Exception e) {
            throw new RemoteServiceException(
                    "Error al comunicarse con ventas-service para validar venta ID: " + id
            );
        }
    }
}