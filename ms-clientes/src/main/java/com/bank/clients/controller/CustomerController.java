package com.bank.clients.controller;

import com.bank.clients.controller.api.CustomerApi;
import com.bank.clients.domain.service.CustomerService;
import com.bank.clients.model.dto.CustomerRequest;
import com.bank.clients.model.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerApi {

    private final CustomerService customerService;

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> getAllCustomers(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(customerService.getAllCustomers()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> getCustomerById(Long id, ServerWebExchange exchange) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> createCustomer(Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        return customerRequest
                .flatMap(customerService::createCustomer)
                .map(customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> updateCustomer(Long id, Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        return customerRequest
                .flatMap(request -> customerService.updateCustomer(id, request))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteCustomer(Long id, ServerWebExchange exchange) {
        return customerService.deleteCustomer(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
