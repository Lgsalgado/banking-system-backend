package com.bank.accounts.domain.service;

import com.bank.accounts.model.dto.CustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CustomerClient {

    private final WebClient webClient;

    public CustomerClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-clientes:8081").build();
    }

    public Mono<CustomerResponse> getCustomerById(Long customerId) {
        return this.webClient.get()
                .uri("/api/v1/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerResponse.class);
    }
}
