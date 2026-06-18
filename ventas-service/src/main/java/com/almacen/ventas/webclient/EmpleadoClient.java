package com.almacen.ventas.webclient;

import com.almacen.ventas.dtos.response.EmpleadoResponse;
import com.almacen.ventas.exceptions.NotFoundException;
import com.almacen.ventas.exceptions.RemoteServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EmpleadoClient {

    private final WebClient webClient;

    public EmpleadoClient(
            WebClient.Builder webClientBuilder,
            @Value("${empleado-service.url}") String urlBase
    ) {
        this.webClient = webClientBuilder
                .baseUrl(urlBase)
                .build();
    }

    public EmpleadoResponse obtenerEmpleadoPorId(Long id) {
        EmpleadoResponse empleado = webClient.get()
                .uri("/api/v1/empleados/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new NotFoundException("Empleado no existe")))
                .onStatus(status -> status.is5xxServerError(),
                        response -> Mono.error(new RemoteServiceException("Error en empleados-service")))
                .bodyToMono(EmpleadoResponse.class)
                .block();

        if (empleado == null) {
            throw new NotFoundException("Empleado no encontrado");
        }

        return empleado;
    }
}