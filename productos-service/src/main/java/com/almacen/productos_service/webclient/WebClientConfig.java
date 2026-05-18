package com.almacen.productos_service.webclient;

import jakarta.servlet.http.HttpServletRequest;
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

                    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                            .getRequestAttributes();

                    if (attributes != null) {

                        HttpServletRequest servletRequest = attributes.getRequest();

                        String token = servletRequest.getHeader("Authorization");

                        if (token != null && !token.isBlank()) {

                            request = ClientRequest.from(request)
                                    .header("Authorization", token)
                                    .build();
                        }
                    }
                    return next.exchange(request);
                });
    }
}