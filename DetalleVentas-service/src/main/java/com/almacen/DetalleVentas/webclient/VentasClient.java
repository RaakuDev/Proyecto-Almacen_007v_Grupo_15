package com.almacen.DetalleVentas.webclient;

import com.almacen.DetalleVentas.exceptions.RemoteServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VentasClient {

    private final WebClient webClient;

    public VentasClient(@Value("${ventas-service.url}") String urlBase) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }

    // Validar que la venta existe en ventas-service
    public void validarVenta(Long id) {
        try {
            webClient.get()
                    .uri("/{id}", id)
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