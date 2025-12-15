package com.bank.clients.domain.service;

import com.bank.clients.model.dto.CustomerRequest;
import com.bank.clients.model.dto.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerResponse> getAllCustomers();
    Mono<CustomerResponse> getCustomerById(Long id);
    Mono<CustomerResponse> createCustomer(CustomerRequest customerRequest);
    Mono<CustomerResponse> updateCustomer(Long id, CustomerRequest customerRequest);
    Mono<Void> deleteCustomer(Long id);
}
