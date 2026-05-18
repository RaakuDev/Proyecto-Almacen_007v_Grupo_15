package com.almacen.ventas.webclient;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.almacen.ventas.dtos.request.DetalleVentaRequest;
import com.almacen.ventas.dtos.response.DetalleVentaResponse;

@Component
public class DetalleVentaClient {

    private final WebClient.Builder webClientBuilder;
    private final String detalleVentaServiceUrl;

    public DetalleVentaClient(
            WebClient.Builder webClientBuilder,
            @Value("${detalle-venta-service.url}") String detalleVentaServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.detalleVentaServiceUrl = detalleVentaServiceUrl;
    }

    public List<DetalleVentaResponse> obtenerDetallesPorVenta(Long ventaId) {

        DetalleVentaResponse[] detalles = webClientBuilder.build()
                .get()
                .uri(detalleVentaServiceUrl + "/api/v1/detalles/venta/" + ventaId)
                .retrieve()
                .bodyToMono(DetalleVentaResponse[].class)
                .block();

        if (detalles == null) {
            return List.of();
        }

        return Arrays.asList(detalles);
    }

    public void crearDetalle(DetalleVentaRequest request) {

        webClientBuilder.build()
                .post()
                .uri(detalleVentaServiceUrl + "/api/v1/detalles")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}