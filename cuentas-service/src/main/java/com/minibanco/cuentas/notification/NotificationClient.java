package com.minibanco.cuentas.notification;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class NotificationClient {

    private final RestClient restClient;
    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    public NotificationClient(@Value("${notifications.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(2));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Retry(name = "notifications", fallbackMethod = "sendFallback")
    @CircuitBreaker(name = "notifications")
    public void send(NotificationRequest request) {
        restClient.post()
                .uri("/api/notificaciones")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendFallback(NotificationRequest request, Throwable ex) {
        log.warn("Could not send notification for transfer with id {}: {}", request.transferId(), ex.getMessage());
    }
}
