package com.almacen.ventas.webclient;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.almacen.ventas.dtos.response.DetalleVentaResponse;

@Component
public class DetalleVentaClient {

    private final WebClient webClient;

    public DetalleVentaClient(@Value("${detalle-venta-service.url}") String urlBase) {
        this.webClient = WebClient.builder()
                .baseUrl(urlBase)
                .build();
    }

    public List<DetalleVentaResponse> obtenerDetallesPorVenta(Long ventaId) {
        DetalleVentaResponse[] detalles = webClient.get()
                .uri("/venta/{ventaId}", ventaId)
                .retrieve()
                .bodyToMono(DetalleVentaResponse[].class)
                .block();

        return Arrays.asList(detalles);
    }
}