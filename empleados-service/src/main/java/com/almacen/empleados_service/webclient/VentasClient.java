package com.almacen.empleados_service.webclient;

import com.almacen.empleados_service.dto.response.VentaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public List<VentaResponse> obtenerVentasPorEmpleado(Long empleadoId) {
        List<VentaResponse> ventas = webClientBuilder.build()
                .get()
                .uri(ventasServiceUrl + "/api/v1/ventas/empleado/" + empleadoId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Empleado no tiene ventas")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RuntimeException("Error en ventas-service")))
                .bodyToMono(new ParameterizedTypeReference<List<VentaResponse>>() {})
                .block();

        if (ventas == null) {
            return List.of();
        }

        return ventas;
    }
}