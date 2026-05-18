package com.almacen.pedidos_service.webclient;

import com.almacen.pedidos_service.dtos.response.ProveedorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ProveedorClient {

    private final WebClient.Builder webClientBuilder;
    private final String proveedorServiceUrl;

    public ProveedorClient(
            WebClient.Builder webClientBuilder,
            @Value("${proveedor-service.url}") String proveedorServiceUrl
    ) {
        this.webClientBuilder = webClientBuilder;
        this.proveedorServiceUrl = proveedorServiceUrl;
    }

    public ProveedorResponse obtenerProveedorPorId(Long id) {

        String url = proveedorServiceUrl + "/api/v1/proveedores/" + id;

        log.info("Consultando proveedor en URL: {}", url);

        try {

            ProveedorResponse response = webClientBuilder.build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(ProveedorResponse.class)
                    .block();

            log.info("Proveedor encontrado correctamente");

            return response;

        } catch (Exception e) {

            log.error("ERROR REAL WEBCLIENT:", e);

            throw e;
        }
    }
}