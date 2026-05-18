package com.almacen.DetalleVentas.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter((request, next) -> {

                    ServletRequestAttributes attributes =
                            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                    if (attributes != null) {
                        String token = attributes.getRequest().getHeader("Authorization");

                        if (token != null && token.startsWith("Bearer ")) {
                            ClientRequest nuevoRequest = ClientRequest.from(request)
                                    .header("Authorization", token)
                                    .build();

                            return next.exchange(nuevoRequest);
                        }
                    }

                    return next.exchange(request);
                });
    }
}